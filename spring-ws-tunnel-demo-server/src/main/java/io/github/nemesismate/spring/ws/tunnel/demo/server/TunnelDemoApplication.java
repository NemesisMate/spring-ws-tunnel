package io.github.nemesismate.spring.ws.tunnel.demo.server;

import io.github.nemesismate.spring.ws.tunnel.api.http.TunnelHttpApiConfiguration;
import io.github.nemesismate.spring.ws.tunnel.api.ws.TunnelWsApiConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

@SpringBootApplication
@TunnelHttpApiConfiguration.EnableTunnelHttpApi
@TunnelWsApiConfiguration.EnableTunnelWsApi
public class TunnelDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TunnelDemoApplication.class, args);
	}

	@Bean
	public WebSocketHandlerAdapter handlerAdapter() {
		return new WebSocketHandlerAdapter(webSocketService());
	}

	@Bean
	public WebSocketService webSocketService() {
		return new HandshakeWebSocketService(new ReactorNettyRequestUpgradeStrategy());
	}

}
