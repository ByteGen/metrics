package com.bytegen.common.metrics.databuilder;

import com.yammer.metrics.core.*;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public interface DataBuilder {

    /**
     * Build falcon data from Gauge. longGauge/doubleGauge.
     * Including Number.
     *
     * @param name
     * @param gauge
     */
    void buildFromGauge(String name, Gauge<? extends Number> gauge);

    /**
     * Build falcon data from Counter.
     * Including Number.
     *
     * @param name
     * @param counter
     */
    void buildFromCounter(String name, Counter counter);

    /**
     * Build falcon data from Meter.
     * Including Metered.
     *
     * @param name
     * @param meter
     */
    void buildFromMeter(String name, Meter meter);

    /**
     * Build falcon data from Timer.
     * Including Snapshot.
     *
     * @param name
     * @param timer
     */
    void buildFromTimer(String name, Timer timer);

    /**
     * Build falcon data from Histogram.
     * Including Histogram.
     *
     * @param name
     * @param histogram
     */
    void buildFromHistogram(String name, Histogram histogram);

}
