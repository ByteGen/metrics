package com.bytegen.common.metrics.databuilder;

import com.yammer.metrics.core.*;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public abstract class AbstractDataBuilder implements DataBuilder {

    @Override
    public void buildFromGauge(String name, Gauge<? extends Number> gauge) {
        buildFromGauge(name, gauge.value());
    }

    @Override
    public void buildFromCounter(String name, Counter counter) {
        buildFromCounter(name, counter.count());
    }

    @Override
    public void buildFromMeter(String name, Meter meter) {
        buildFromMetered(name, meter);
    }

    @Override
    public void buildFromTimer(String name, Timer timer) {
        buildFromTimed(name, timer);
        buildFromSummary(name, timer);
    }

    @Override
    public void buildFromHistogram(String name, Histogram histogram) {
        buildFromSummary(name, histogram);
    }

    /**
     * Build falcon data from Gauge
     *
     * @param name
     * @param value
     */
    public abstract void buildFromGauge(String name, Number value);

    /**
     * Build falcon data from Counter
     *
     * @param name
     * @param value
     */
    public abstract void buildFromCounter(String name, long value);

    /**
     * Build falcon data from Metered.
     * Including count, CPS-1-min, CPS-5-min, CPS-15-min.
     *
     * @param name
     * @param metered
     */
    public abstract void buildFromMetered(String name, Metered metered);

    /**
     * Build falcon data from Timer.
     * Including 75-percentile, 95-percentile, 99-percentile.
     *
     * @param name
     * @param timer
     */
    public abstract void buildFromTimed(String name, Timer timer);

    /**
     * Build falcon data from Summary
     * Including min, max, mean
     *
     * @param name
     * @param summary
     */
    public abstract void buildFromSummary(String name, Summarizable summary);
}
