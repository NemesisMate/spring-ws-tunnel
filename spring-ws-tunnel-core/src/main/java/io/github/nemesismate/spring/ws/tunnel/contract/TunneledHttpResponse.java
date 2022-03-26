package io.github.nemesismate.spring.ws.tunnel.contract;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Value;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

@Value
public class TunneledHttpResponse implements TunneledResponse {

    int status;
    @JsonDeserialize(using = HttpHeadersDeserializer.class)
    HttpHeaders headers;
    String body;

    public static class HttpHeadersDeserializer extends StdDeserializer<HttpHeaders> {

        public HttpHeadersDeserializer() {
            super(HttpHeaders.class);
        }

        @Override
        public HttpHeaders deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            HttpHeaders headers = new HttpHeaders();
            jp.getCodec().<JsonNode>readTree(jp)
                    .fields().forEachRemaining(field -> headers.add(field.getKey(), field.getValue().asText()));
            return headers;
        }
    }
}
