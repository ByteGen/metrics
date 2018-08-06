package com.bytegen.common.metrics.model;

import com.yammer.metrics.core.Gauge;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class LongGauge extends Gauge<Long> {
    private Long value = 0L;

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public Long value() {
        return value;
    }
}
