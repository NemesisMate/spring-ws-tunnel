package io.github.nemesismate.spring.ws.tunnel.tunnel;

import io.github.nemesismate.spring.ws.tunnel.contract.TunnelMessage;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledRequest;
import io.github.nemesismate.spring.ws.tunnel.contract.TunneledResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
public class TunnelSink {

    private final MessageSink<TunnelMessage<TunneledRequest>> requestSink = new MessageSink<>();
    private final MessageSink<TunnelMessage<TunneledResponse>> responseSink = new MessageSink<>();

    private final AtomicLong idGenerator = new AtomicLong();

    public void addResponse(TunnelMessage<TunneledResponse> tunneledResponse) {
        responseSink.addMessage(tunneledResponse);
    }

    public long addHttpRequest(TunneledRequest tunneledRequest) {
        return addRequest(tunneledRequest, TunnelMessage.Type.HTTP_REQUEST);
    }

    public long addWsRequest(TunneledRequest tunneledRequest) {
        return addRequest(tunneledRequest, TunnelMessage.Type.WS_REQUEST);
    }

    public void close() {
        requestSink.closeSink();
        responseSink.closeSink();
    }

    public Flux<TunnelMessage<TunneledRequest>> getRequestFlux() {
        return requestSink.getMessageFlux();
    }

    public Flux<TunnelMessage<TunneledResponse>> getResponseFlux() {
        return responseSink.getMessageFlux();
    }

    long addRequest(TunneledRequest tunneledRequest, TunnelMessage.Type type) {
        long requestId = idGenerator.incrementAndGet();

        requestSink.addMessage(new TunnelMessage<>(requestId, tunneledRequest, type));

        return requestId;
    }

}
