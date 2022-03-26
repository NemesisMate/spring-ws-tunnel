package io.github.nemesismate.spring.ws.tunnel.api.http;

import io.github.nemesismate.spring.ws.tunnel.tunnel.TunnelService;
import io.github.nemesismate.spring.ws.tunnel.contract.TunnelMessage;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledHttpRequest;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledHttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TunnelHttpApiService {

    private final TunnelService tunnelService;

    public Mono<TunneledHttpResponse> makeRequest(String tunnelId, TunneledHttpRequest tunneledHttpRequest) {
        return tunnelService.getTunnelSink(tunnelId)
                .flatMap(sink -> {
                    Mono<Long> requestId = Mono.fromCallable(() -> sink.addHttpRequest(tunneledHttpRequest)).cache();

                    return sink.getResponseFlux()
                            .filterWhen(response -> requestId.map(id -> response.isHttp() && id == response.getId()))
                            .next()
                            .doOnSubscribe(subscription -> requestId.subscribe())
                            .map(TunnelMessage::getMessage)
                            .cast(TunneledHttpResponse.class)
                            .timeout(Duration.ofSeconds(60));
                });
    }

}
