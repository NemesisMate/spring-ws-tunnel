package io.github.nemesismate.spring.ws.tunnel.tunnel;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.function.Function;

@Builder
@Getter
public class TunnelConfig {

    @Builder.Default
    private Function<String, Boolean> onTunnelCreate = id -> true;

    @Builder.Default
    private Consumer<String> onTunnelDelete = id -> {};

}
