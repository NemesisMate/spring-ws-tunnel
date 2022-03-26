package io.github.nemesismate.spring.ws.tunnel.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@AllArgsConstructor
@ToString
public class TunnelMessage<T> {

    private final long id;

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = TunneledHttpResponse.class, name = "HTTP_RESPONSE"),
            @JsonSubTypes.Type(value = TunneledWsResponse.class, name = "WS_RESPONSE"),
    })
    private final T message;

    private final Type type;

    public enum Type {
        HTTP_REQUEST, HTTP_RESPONSE, WS_REQUEST, WS_RESPONSE
    }

    @JsonIgnore
    public boolean isWs() {
        return type == Type.WS_REQUEST || type == Type.WS_RESPONSE;
    }

    @JsonIgnore
    public boolean isHttp() {
        return type == Type.HTTP_REQUEST || type == Type.HTTP_RESPONSE;
    }

}

