package io.github.nemesismate.spring.ws.tunnel.api.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledWsRequest;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledWsResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.ENABLE_WS_API_PROP;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.PROPS_PREFIX;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_ID_PARAM_NAME;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_WS_API_PATH;

@Component
@ConditionalOnProperty(prefix = PROPS_PREFIX, name = ENABLE_WS_API_PROP, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TunnelWsApiEndpoint implements WebSocketHandler {

    private final TunnelWsApiService tunnelWsApiService;
    private final ObjectMapper objectMapper;

    @Bean
    public HandlerMapping tunnelWsApiEndpointMappings(TunnelWsApiEndpoint webSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put(TUNNEL_WS_API_PATH, webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        URI uri = webSocketSession.getHandshakeInfo().getUri();
        UriTemplate template = new UriTemplate(TUNNEL_WS_API_PATH);
        Map<String, String> parameters = template.match(uri.getPath());

        String tunnelId = Objects.requireNonNull(parameters.get(TUNNEL_ID_PARAM_NAME), TUNNEL_ID_PARAM_NAME);

        return webSocketSession.send(tunnelWsApiService.receiveMessages(tunnelId)
                .map(this::serialize)
                .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(message -> TunneledWsRequest.of(message.getPayloadAsText()))
                        .flatMap(request -> tunnelWsApiService.sendMessage(tunnelId, request)));
    }

    @SneakyThrows
    private String serialize(TunneledWsResponse tunneledWsResponse) {
        return objectMapper.writeValueAsString(tunneledWsResponse);
    }
}
