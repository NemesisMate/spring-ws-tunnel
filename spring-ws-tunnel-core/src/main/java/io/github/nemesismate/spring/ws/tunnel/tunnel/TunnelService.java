package io.github.nemesismate.spring.ws.tunnel.tunnel;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TunnelService {

    private final Map<String, TunnelSink> tunnelSinks = new ConcurrentHashMap<>();

    public Mono<TunnelSink> createTunnelSink(String tunnelId) {
        return Mono.fromCallable(() -> tunnelSinks.compute(tunnelId, (key, value) -> {
                    if(value != null) {
                        throw new IllegalStateException();
                    }
                    return new TunnelSink();
                }));
    }

    public Mono<TunnelSink> getTunnelSink(String tunnelId) {
        return Mono.justOrEmpty(tunnelSinks.get(tunnelId));
    }

    public void deleteTunnelSink(String tunnelId) {
        tunnelSinks.remove(tunnelId).close();
    }

}
