package io.github.nemesismate.spring.ws.tunnel.api.http;

import io.github.nemesismate.spring.ws.tunnel.contract.TunneledHttpRequest;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledHttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Optional;

import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.ENABLE_HTTP_API_PROP;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.PROPS_PREFIX;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_HTTP_API_BASE_PATH;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_HTTP_API_PATH;
import static io.github.nemesismate.spring.ws.tunnel.WsTunnelConstants.TUNNEL_ID_PARAM_NAME;

@RestController
@ConditionalOnProperty(prefix = PROPS_PREFIX, name = ENABLE_HTTP_API_PROP, havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class TunnelHttpApiEndpoint {

    private final TunnelHttpApiService tunnelHttpApiService;

    @RequestMapping(path = TUNNEL_HTTP_API_PATH)
    public Mono<ResponseEntity<byte[]>> tunnelRequest(@PathVariable(name = TUNNEL_ID_PARAM_NAME) String tunnelId, ServerHttpRequest request) {
        String expandedPath = TUNNEL_HTTP_API_BASE_PATH + "/" + tunnelId;
        String uriString = request.getURI().toString();
        String callPathAndQuery = uriString.substring(uriString.indexOf(expandedPath) + expandedPath.length());

        return DataBufferUtils.join(request.getBody())
                        .map(dataBuffer -> {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            DataBufferUtils.release(dataBuffer);
                            return bytes;
                        }).map(bytes -> Optional.of(Base64.getEncoder().encodeToString(bytes)))
                        .switchIfEmpty(Mono.just(Optional.empty()))
                        .map(body -> {
                            var builder = TunneledHttpRequest.builder()
                                .method(request.getMethod())
                                .headers(request.getHeaders())
                                .path(callPathAndQuery);
                            return body
                                    .map(builder::body)
                                    .orElse(builder)
                                    .build();
                        })
                .flatMap(tunneledHttpRequest -> tunnelHttpApiService.makeRequest(tunnelId, tunneledHttpRequest))
                .map(this::mapResponse)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    public ResponseEntity<byte[]> mapResponse(TunneledHttpResponse tunneledHttpResponse) {
        return ResponseEntity
                .status(tunneledHttpResponse.getStatus())
                .headers(tunneledHttpResponse.getHeaders())
                .body(Base64.getDecoder().decode(tunneledHttpResponse.getBody()));
    }

}
