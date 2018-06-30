package com.bytegen.metrics.provider;

import com.bytegen.metrics.MetricsCounter;
import com.bytegen.metrics.common.Tag;
import com.bytegen.metrics.constant.CounterConst;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class MetricsProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsProvider.class);

    private static volatile long step = 0;
    private static volatile boolean pushOn = true;
    private static volatile String endPoint = null;
    private static volatile Tag jobTag;
    private static volatile String metricPrefix = null;

    public static final long DEFAULT_AGENT_STEP = 3 * 60; // unit s
    public static final boolean DEFAULT_PUSH_ON = true;

    private MetricsProvider() {
        String configFile = StringUtils.defaultIfBlank(System.getenv(CounterConst.METRICS_CONFIG_PATH), "/metrics.properties");
        loadPerformanceCounterConfig(configFile);
        initEndPoint();
    }

    private void loadPerformanceCounterConfig(final String resourceName) {
        try (InputStream inputStream = MetricsCounter.class.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                LOGGER.warn("Metrics: failed to load performance config file:{}. ", resourceName);
                step = DEFAULT_AGENT_STEP;
                return;
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            // metric_prefix
            String prefix = properties.getProperty(CounterConst.AGENT_KEY_METRIC_PREFIX);
            if (!StringUtils.isBlank(prefix)) {
                metricPrefix = prefix;
            }

            // tags like k1:v1,k2:v2
            String tags = properties.getProperty(CounterConst.AGENT_KEY_TAGS);
            if (!StringUtils.isBlank(tags)) {
                jobTag = new Tag.TagBuilder().put(tags.replaceAll(":", "=")).build();
            }

            // step
            String stepValue = properties.getProperty(CounterConst.AGENT_KEY_STEP);
            if (StringUtils.isBlank(stepValue)) {
                step = DEFAULT_AGENT_STEP;
            } else {
                step = NumberUtils.toLong(stepValue);
            }

            // pushOn switch
            String pushOnValue = properties.getProperty(CounterConst.AGENT_KEY_PUSH_ON);
            if (StringUtils.isBlank(pushOnValue)) {
                pushOn = DEFAULT_PUSH_ON;
            } else {
                pushOn = Boolean.valueOf(pushOnValue);
            }

            LOGGER.debug("Metrics: succeed to load performance config file:{}, step:{}, push_on:{}. ", resourceName, step, pushOn);
        } catch (IOException e) {
            LOGGER.error("Metrics: could not open properties. ", e);
        }
    }

    private static void initEndPoint() {
        endPoint = getLocalHostName();
        if (StringUtils.isBlank(endPoint)) {
            endPoint = "unknown";
        }
    }

    public static String getLocalHostName() {
        String hostname = System.getenv("HOSTNAME");
        if (StringUtils.isEmpty(hostname)) {
            try {
                Process pro = Runtime.getRuntime().exec("hostname");
                pro.waitFor();
                try (InputStream in = pro.getInputStream();
                     BufferedReader read = new BufferedReader(new InputStreamReader(in, CharEncoding.UTF_8))) {
                    hostname = read.readLine();
                }

            } catch (IOException var4) {
                LOGGER.error("Metrics: getLocalHostName IOException. ", var4);
            } catch (InterruptedException var5) {
                LOGGER.error("Metrics: getLocalHostName InterruptedException. ", var5);
            }
        }

        return hostname;
    }

    public Tag getJobTag() {
        return jobTag;
    }

    public long getStep() {
        return step;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public boolean getPushOn() {
        return pushOn;
    }

    public String getMetricPrefix() {
        return metricPrefix;
    }

    public long getCurrentTimeInSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    private static class MetricsProviderHolder {
        private final static MetricsProvider holder = new MetricsProvider();
    }

    public static MetricsProvider getInstance() {
        return MetricsProviderHolder.holder;
    }
}
