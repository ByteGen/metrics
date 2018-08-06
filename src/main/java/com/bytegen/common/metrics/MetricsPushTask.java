package com.bytegen.common.metrics;

import com.bytegen.common.metrics.constant.AgentConfig;
import com.bytegen.common.metrics.provider.AgentProvider;
import com.bytegen.common.metrics.provider.MetricsProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class MetricsPushTask extends TimerTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsPushTask.class);

    private static final AgentConfig agentConfig = AgentConfig.getInstance();
    private static final AgentProvider agentClientProvider = AgentProvider.getInstance();

    @Override
    public void run() {
        try {
            // push performance counter data to local agent through HTTP Protocol
            String content = MetricsCounter.toJsonArrayString();
            LOGGER.debug("Metrics: sending performance data: {} ", content);

            // do not send data if endpoint is end with .local
            String endPoint = MetricsProvider.getInstance().getEndPoint();
            if (StringUtils.isBlank(endPoint) || endPoint.toLowerCase().endsWith(".local")) {
                LOGGER.warn("Metrics: performance push task stopped for endpoint: {} ", endPoint);
                return;
            }

            String result = agentClientProvider.send(content);
            if (!isSuccess(result)) {
                LOGGER.error("Metrics: failed to push performance data to {}/{}, result:{}",
                        agentConfig.getAgentHostPort(), agentConfig.getAgentPathUri(), result);
            }
        } catch (Throwable e) {
            LOGGER.error("Metrics: unexpected exception. ", e);
        }
    }

    boolean isSuccess(String result) {
        return "success".equalsIgnoreCase(result);
    }
}
