package io.github.nemesismate.spring.ws.tunnel.demo.server;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

@Service
public class MessageStreamService {

    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    public void send(String message) {
        sink.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    public Flux<String> getStream() {
        return sink.asFlux();
    }

}
