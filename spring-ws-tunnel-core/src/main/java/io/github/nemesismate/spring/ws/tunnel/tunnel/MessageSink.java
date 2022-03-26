package io.github.nemesismate.spring.ws.tunnel.tunnel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

public class MessageSink<T> {
    private final Sinks.Many<T> requestSink;

    public MessageSink() {
        this(Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false));
    }

    public MessageSink(Sinks.Many<T> requestSink) {
        this.requestSink = requestSink;
    }

    public void addMessage(T tunneledRequest) {
        requestSink.emitNext(tunneledRequest, FAIL_FAST);
    }

    public Flux<T> getMessageFlux() {
        return requestSink.asFlux();
    }

    public void closeSink() {
        requestSink.emitComplete(FAIL_FAST);
    }

}
