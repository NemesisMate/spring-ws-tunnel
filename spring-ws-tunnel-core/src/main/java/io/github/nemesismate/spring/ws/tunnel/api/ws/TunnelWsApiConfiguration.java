package io.github.nemesismate.spring.ws.tunnel.api.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
@Import({
        TunnelWsApiEndpoint.class,
        TunnelWsApiService.class,
})
public class TunnelWsApiConfiguration {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(TunnelWsApiConfiguration.class)
    public @interface EnableTunnelWsApi {}

}
