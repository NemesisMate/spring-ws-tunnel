package io.github.nemesismate.spring.ws.tunnel.contract;

import lombok.Value;

@Value
public class TunneledWsResponse implements TunneledResponse {
    
    Object body;

}
