package io.github.nemesismate.spring.ws.tunnel.tunnel;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledRequest;
import io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants;
import io.github.nemesismate.spring.ws.tunnel.contract.TunnelMessage;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_CONNECTOR_PATH;

@Component
@RequiredArgsConstructor
public class TunnelEndpoint implements WebSocketHandler {

    private final ObjectMapper objectMapper;

    private final TunnelService tunnelService;

    private final TunnelConfig tunnelConfig;

    @Bean
    public HandlerMapping webSocketHandlerMapping(TunnelEndpoint webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(TUNNEL_CONNECTOR_PATH, webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        URI uri = webSocketSession.getHandshakeInfo().getUri();
        UriTemplate template = new UriTemplate(WsTunnelConstants.TUNNEL_CONNECTOR_PATH);
        Map<String, String> parameters = template.match(uri.getPath());

        String tunnelId = parameters.get(WsTunnelConstants.TUNNEL_ID_PARAM_NAME);

        return tunnelService.createTunnelSink(tunnelId)
                .filter(tunnelSink -> tunnelConfig.getOnTunnelCreate().apply(tunnelId))
                .switchIfEmpty(Mono.error(IllegalAccessError::new))
                .flatMap(tunnelSink ->
                        webSocketSession.send(
                                tunnelSink.getRequestFlux()
                                        .map(this::serialize)
                                        .map(webSocketSession::textMessage))
                                .and(webSocketSession.receive()
                                        .map(WebSocketMessage::getPayloadAsText)
                                        .map(this::deserialize)
                                        .doOnNext(tunnelSink::addResponse))
                                .doOnTerminate(() -> {
                                    tunnelService.deleteTunnelSink(tunnelId);
                                    tunnelConfig.getOnTunnelDelete().accept(tunnelId);
                                }));
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private TunnelMessage<TunneledResponse> deserialize(String text) {
        return objectMapper.readValue(text, TunnelMessage.class);
    }

    @SneakyThrows
    private String serialize(TunnelMessage<TunneledRequest> tunnelMessage) {
        return objectMapper.writeValueAsString(tunnelMessage);
    }
}