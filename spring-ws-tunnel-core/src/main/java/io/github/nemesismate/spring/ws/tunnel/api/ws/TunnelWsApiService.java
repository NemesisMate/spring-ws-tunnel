package io.github.nemesismate.spring.ws.tunnel.api.ws;

import io.github.nemesismate.spring.ws.tunnel.contract.TunnelMessage;
import io.github.nemesismate.spring.ws.tunnel.tunnel.TunnelService;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledWsRequest;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledWsResponse;
import io.github.nemesismate.spring.ws.tunnel.tunnel.TunnelSink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TunnelWsApiService {

    private final TunnelService tunnelService;

    public Mono<Void> sendMessage(String tunnelId, TunneledWsRequest tunneledWsRequest) {
        return tunnelService.getTunnelSink(tunnelId)
                .doOnNext(sink -> sink.addWsRequest(tunneledWsRequest))
                .then();
    }

    public Flux<TunneledWsResponse> receiveMessages(String tunnelId) {
        return tunnelService.getTunnelSink(tunnelId)
                .flatMapMany(TunnelSink::getResponseFlux)
                .filter(TunnelMessage::isWs)
                .map(TunnelMessage::getMessage)
                .cast(TunneledWsResponse.class);
    }

}
