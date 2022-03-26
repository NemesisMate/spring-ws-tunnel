package io.github.nemesismate.spring.ws.tunnel;

import io.github.nemesismate.spring.ws.tunnel.tunnel.TunnelConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(TunnelConfiguration.class)
public class WsTunnelAutoConfiguration {

}
