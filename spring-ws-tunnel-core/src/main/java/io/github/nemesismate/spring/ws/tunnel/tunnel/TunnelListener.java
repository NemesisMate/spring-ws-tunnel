package io.github.nemesismate.spring.ws.tunnel.tunnel;

import lombok.Builder;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TunnelListener {

    Mono<Boolean> onTunnelUp(String tunnelId);

    Mono<Void> onTunnelDown(String tunnelId);

    @Builder
    class Default implements TunnelListener {

        @Builder.Default
        private Function<String, Boolean> onTunnelCreate = tunnelId -> true;

        @Builder.Default
        private Consumer<String> onTunnelDelete = tunnelId -> {};
        @Override
        public Mono<Boolean> onTunnelUp(String tunnelId) {
            return Mono.fromSupplier(() -> onTunnelCreate.apply(tunnelId));
        }

        @Override
        public Mono<Void> onTunnelDown(String tunnelId) {
            return Mono.fromRunnable(() -> onTunnelDelete.accept(tunnelId));
        }

    }

}
