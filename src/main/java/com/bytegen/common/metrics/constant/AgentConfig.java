package com.bytegen.common.metrics.constant;

import org.apache.commons.lang3.StringUtils;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class AgentConfig {
    private static AgentConfig instance;

    private static final String DEFAULT_AGENT_HOST_PORT = "http://127.0.0.1:1988";
    private static final String DEFAULT_AGENT_PATH_URI = "/v1/push";

    private String agentHostPort;
    private String agentPathUri;

    private AgentConfig() {
        agentHostPort = StringUtils.defaultIfBlank(System.getenv(CounterConst.AGENT_HOST_PORT), DEFAULT_AGENT_HOST_PORT);
        agentPathUri = StringUtils.defaultIfBlank(System.getenv(CounterConst.AGENT_PATH_URI), DEFAULT_AGENT_PATH_URI);
    }

    public static AgentConfig getInstance() {
        if (null == instance) {
            synchronized (AgentConfig.class) {
                if (null == instance) {
                    instance = new AgentConfig();
                }
            }
        }
        return instance;
    }

    public String getAgentHostPort() {
        return agentHostPort;
    }

    public void setAgentHostPort(String agentHostPort) {
        this.agentHostPort = agentHostPort;
    }

    public String getAgentPathUri() {
        return agentPathUri;
    }

    public void setAgentPathUri(String agentPathUri) {
        this.agentPathUri = agentPathUri;
    }
}
