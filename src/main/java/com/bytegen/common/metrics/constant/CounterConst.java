package com.bytegen.common.metrics.constant;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public interface CounterConst {
    /**
     * Limitation
     */
    int KEY_MAX_LENGTH = 128;

    /**
     * Units
     */
    String UNIT_SECOND = "秒";
    String UNIT_COUNTER = "次";
    String UNIT_GAUGE = "";
    String UNIT_CPS = "CPS";  // Speed per second, (now_count - last_count) / (now_time - last_time)
    String UINT_MS = "ms";

    /**
     * Seperator
     */
    String PATH_SEPARATOR_STRING = "-";

    /**
     * default value
     */
    String DEFAULT_GROUP = "default.group";
    String DEFAULT_NAME = "default";

    /**
     * push performance counter data to local agent
     */
    String METRICS_CONFIG_PATH = "metrics_config";
    String AGENT_HOST_PORT = "host_port";
    String AGENT_PATH_URI = "path_uri";

    String AGENT_KEY_METRIC = "metric";
    String AGENT_KEY_VALUE = "value";
    String AGENT_KEY_END_POINT = "endpoint";
    String AGENT_KEY_TIME_STAMP = "timestamp";
    String AGENT_KEY_STEP = "step";
    String AGENT_KEY_TAGS = "tags";
    String AGENT_KEY_COUNTER_TYPE = "counterType";
    String AGENT_KEY_PUSH_ON = "push_on";
    String AGENT_KEY_METRIC_PREFIX = "metric_prefix";
}
