package io.github.nemesismate.spring.ws.tunnel.contract;

import lombok.Value;

@Value(staticConstructor = "of")
public class TunneledWsRequest implements TunneledRequest {

    String body;

}
