package io.github.nemesismate.spring.ws.tunnel.contract;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Builder
@Getter
@ToString
public class TunneledHttpRequest implements TunneledRequest {

    private final HttpMethod method;
    private final HttpHeaders headers;
    private final String body;
    private final String path;

}
