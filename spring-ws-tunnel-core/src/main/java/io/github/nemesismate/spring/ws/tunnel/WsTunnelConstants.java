package io.github.nemesismate.spring.ws.tunnel;

public class WsTunnelConstants {

    public static final String SYSTEM_NAME = "ws-tunnel";

    public static final String TUNNEL_ID_PARAM_NAME = "tunnelId";

    public static final String TUNNEL_CONTEXT_PATH = "/" + SYSTEM_NAME;
    public static final String TUNNEL_API_PARAM_PATH = "/{" + TUNNEL_ID_PARAM_NAME + "}";

    public static final String TUNNEL_CONNECTOR_PATH = TUNNEL_CONTEXT_PATH + "-connector" + TUNNEL_API_PARAM_PATH;
    public static final String TUNNEL_HTTP_API_BASE_PATH = TUNNEL_CONTEXT_PATH + "-http";
    public static final String TUNNEL_HTTP_API_PATH = TUNNEL_HTTP_API_BASE_PATH + TUNNEL_API_PARAM_PATH + "/**";
    public static final String TUNNEL_WS_API_PATH = TUNNEL_CONTEXT_PATH + "-ws" + TUNNEL_API_PARAM_PATH;


    public static final String PROPS_PREFIX = SYSTEM_NAME;
    public static final String ENABLE_HTTP_API_PROP = "http-endpoint-enabled";
    public static final String ENABLE_WS_API_PROP = "ws-endpoint-enabled";

}
