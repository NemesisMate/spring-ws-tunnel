package io.github.nemesismate.spring.ws.tunnel.tunnel;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TunnelService {

    private final Map<String, TunnelSink> tunnelSinks = new HashMap<>();

    public Mono<TunnelSink> createTunnelSink(String tunnelId) {
        return Mono.fromCallable(() -> tunnelSinks.compute(tunnelId, (key, value) -> {
                    if(value != null) {
                        throw new IllegalStateException();
                    }
                    return new TunnelSink();
                })).subscribeOn(Schedulers.single())
                .publishOn(Schedulers.parallel());
    }

    public Mono<TunnelSink> getTunnelSink(String tunnelId) {
        return Mono.fromSupplier(() -> tunnelSinks.get(tunnelId))
                .subscribeOn(Schedulers.single())
                .publishOn(Schedulers.parallel());
    }

    public Mono<Void> deleteTunnelSink(String tunnelId) {
        return Mono.fromRunnable(() -> tunnelSinks.remove(tunnelId).close())
                .subscribeOn(Schedulers.single())
                .then();
    }

}
