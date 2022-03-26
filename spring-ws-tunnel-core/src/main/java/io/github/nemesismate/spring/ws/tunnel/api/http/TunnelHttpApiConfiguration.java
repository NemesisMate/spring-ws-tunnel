package io.github.nemesismate.spring.ws.tunnel.api.http;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
@Import({
        TunnelHttpApiEndpoint.class,
        TunnelHttpApiService.class,
})
public class TunnelHttpApiConfiguration {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Import(TunnelHttpApiConfiguration.class)
    public @interface EnableTunnelHttpApi {}


}
