package com.bytegen.common.metrics.model;

import com.yammer.metrics.core.Gauge;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class DoubleGauge extends Gauge<Double> {
    private Double value = 0.0;

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public Double value() {
        return value;
    }
}
