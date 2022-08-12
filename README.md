# spring-ws-tunnel

![build main](https://github.com/NemesisMate/spring-ws-tunnel/actions/workflows/main.yml/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.nemesismate/spring-ws-tunnel-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.nemesismate/spring-ws-tunnel-core/)

This project can turn any spring application into an API tunnel gateway, allowing a local server to act as one located in the same network the gateway is without requiring special routings (the client becomes the server and the server becomes the client).

This is achieved by using a webscoket connection as a tunnel to forward HTTP or other WS requests.

Any **remote** communication (HTTP or WS) following a client -> server model:

![](./images/client_server.png)

Can be tunneled to a **local** server:

![](./images/tunnel.png)

### Libraries
- **spring-ws-tunnel-core**: main tunneling functionality. This library is required to setup your tunnel gateway.
- **spring-ws-tunnel-demo-server**: minimal server setup example. It showcases the tunneling system, including a Web UI (tunnel client) served as an static application.

### Tunnel Setup
Once included the library in the application (io.github.nemesismate:spring-ws-tunnel-core), enabling the tunnel functionality is done with the relevant annotations.

```
@EnableTunnelHttpApi
@EnableTunnelWsApi
@SpringBootApplication
public class TunnelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TunnelApplication.class, args);
    }

}
```

The application will complain if the tunneling is enabled but a `TunnelListener` bean doesn't exist.

```
@Bean
public TunnelListener tunnelListener() {
    return TunnelListener.Default.builder()
            .onTunnelCreate(id -> {
                System.out.println("Created tunnel: " + id);
                return true;
            })
            .onTunnelDelete(id -> System.out.println("Deleted tunnel: " + id))
            .build();
}
```

### Tunnel Setup - Spring Cloud Gateway (WIP)
A very simple and powerful approach to setup this Tunnel applicationis with Spring Cloud Gateway.
