package io.github.nemesismate.spring.ws.tunnel.demo.server;

import io.github.nemesismate.spring.ws.tunnel.tunnel.TunnelConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerResponse.BodyBuilder;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

@RequiredArgsConstructor
@Component
public class DemoEndpoints implements WebSocketHandler {

    private final MessageStreamService messageStreamService;

    @Bean
    public HandlerMapping demoWsMappings(DemoEndpoints webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/demo-ws", webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        Sinks.Many<String> wsSendStream = Sinks.many().unicast().onBackpressureBuffer();

        return webSocketSession.send(wsSendStream.asFlux()
                        .map(Object::toString)
                        .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText)
                        .doOnNext(message -> messageStreamService.send("Ws received: " + message + " <-- Responding"))
                        .doOnNext(message -> wsSendStream.emitNext("Ws response to: " + message, FAIL_FAST)));
    }

    @Bean
    public RouterFunction<ServerResponse> htmlRouter(@Value("classpath:/public/client.html") Resource html) {
        return route(GET("/"), request -> ok().contentType(MediaType.TEXT_HTML).bodyValue(html));
    }

    @Bean
    public RouterFunction<ServerResponse> mainRouter() {
        return route()
                .add(RouterFunctions.route(RequestPredicates.path("/demo/**"), request -> request.bodyToMono(String.class)
                        .doOnNext(body -> messageStreamService.send("Http received: " + body + " <-- Responding"))
                        .flatMap(requestBody -> okWithCustomHeader().bodyValue("Http response to: " + requestBody))
                        .switchIfEmpty(okWithCustomHeader().build())))
                .GET("/demo-stream", request -> ok().contentType(MediaType.TEXT_EVENT_STREAM).body(messageStreamService.getStream(), Object.class))
                .build();
    }

    @Bean
    public TunnelConfig TunnelConfig() {
        return TunnelConfig.builder()
                .onTunnelCreate(id -> {
                    messageStreamService.send("Created tunnel: " + id);
                    return true;
                })
                .onTunnelDelete(id -> messageStreamService.send("Deleted tunnel: " + id))
                .build();
    }

    private BodyBuilder okWithCustomHeader() {
        return ok().header("X-Demo-Tunnel-Custom", "headerValue");
    }

}
