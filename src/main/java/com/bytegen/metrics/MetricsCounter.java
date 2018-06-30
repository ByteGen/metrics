package com.bytegen.metrics;

import com.bytegen.metrics.common.DoubleGauge;
import com.bytegen.metrics.databuilder.FalconDataBuilder;
import com.bytegen.metrics.provider.MetricsProvider;
import com.bytegen.metrics.common.LongGauge;
import com.google.gson.JsonArray;
import com.bytegen.metrics.common.Tag;
import com.bytegen.metrics.constant.CounterConst;
import com.yammer.metrics.core.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class MetricsCounter {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCounter.class);

    private MetricsCounter() {
    }

    private static final MetricsRegistry mr = new MetricsRegistry();

    //Gauge
    private static final ConcurrentMap<String, LongGauge> longGauges = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, DoubleGauge> doubleGauges = new ConcurrentHashMap<>();
    //Counter
    private static final ConcurrentMap<String, Counter> counters = new ConcurrentHashMap<>();
    //Meter
    private static final ConcurrentMap<String, Meter> meters = new ConcurrentHashMap<>();
    //Timer
    private static final ConcurrentMap<String, Timer> timers = new ConcurrentHashMap<>();
    //Histogram
    private static final ConcurrentMap<String, Histogram> histograms = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, LongGauge> helperLongGauges = new ConcurrentHashMap<>();

    //Tag
    private static final ConcurrentMap<String, Tag> tags = new ConcurrentHashMap<>();

    public static void additionMetricTagIfAbsent(String name, Tag tag) {
        if (null != tag) {
            tags.putIfAbsent(name, tag);
        }
    }

    private static final MetricsProvider provider = MetricsProvider.getInstance();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    static {
        startPerformanceCounterPushTask();
    }

    private static void startPerformanceCounterPushTask() {
        if (provider.getPushOn()) {
            LOGGER.debug("Metrics: starting performance counter...");
            scheduler.scheduleAtFixedRate(new MetricsPushTask(), provider.getStep() * 1000, provider.getStep() * 1000, TimeUnit.MILLISECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    LOGGER.debug("Metrics: shutting down performance counter...");
                    scheduler.shutdown();
                }
            }));
        }
    }

    // record name in map
    private static String getGaugeName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".GAUGE";
    }

    private static String getCounterName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".COUNTER";
    }

    private static String getMeterName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".METER";
    }

    private static String getTimerName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".TIMER";
    }

    private static String getHistogramName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".HIST";
    }

    private static String getHelperGaugeName(String name) {
        return StringUtils.defaultIfBlank(name, CounterConst.DEFAULT_NAME) + ".HELPER";
    }

    /**
     * Mark the occurrence of a given number of events.
     * Meter
     *
     * @param name
     * @param count
     */
    public static void count(String name, long count) {
        setMeterCount(name, count);
    }

    /**
     * Use Timer to count duration.
     * Timer
     *
     * @param name
     * @param duration
     */
    public static void countDuration(String name, long duration) {
        setTimerValue(name, duration);
    }

    /**
     * counter to inc
     *
     * @param name
     * @param count
     */
    public static void inc(String name, long count) {
        setCounterCount(name, count);
    }

    /**
     * counter to dec
     *
     * @param name
     * @param count
     */
    public static void dec(String name, long count) {
        inc(name, 0 - count);
    }

    /**
     * Update histogram
     *
     * @param name
     * @param value
     */
    public static void updateHistogram(String name, long value) {
        setHistogramValue(name, value);
    }


    // ------------basic counter method start----------------

    /**
     * Record the current value
     *
     * @param name
     * @param value
     */
    public static void setGaugeValue(String name, long value) {
        getLongGauge(name).setValue(value);
    }

    public static void setGaugeValue(String name, double value) {
        getDoubleGauge(name).setValue(value);
    }

    private static void setHelperLongGauge(String name, long value) {
        getHelperLongGauge(name).setValue(value);
    }

    /**
     * Get the current value
     *
     * @param name
     * @return
     */
    public static long getLongGaugeValue(String name) {
        return getLongGauge(name).value();
    }

    public static double getDoubleGaugeValue(String name) {
        return getDoubleGauge(name).value();
    }

    private static long getHelperLongGaugeValue(String name) {
        return getHelperLongGauge(name).value();
    }

    private static LongGauge getLongGauge(String name) {
        return longGauges.computeIfAbsent(name,
                name1 -> (LongGauge) mr.newGauge(MetricsCounter.class, getGaugeName(name), new LongGauge()));
    }

    private static DoubleGauge getDoubleGauge(String name) {
        return doubleGauges.computeIfAbsent(name,
                name1 -> (DoubleGauge) mr.newGauge(MetricsCounter.class, getGaugeName(name), new DoubleGauge()));
    }

    private static LongGauge getHelperLongGauge(String name) {
        return helperLongGauges.computeIfAbsent(name,
                name1 -> (LongGauge) mr.newGauge(MetricsCounter.class, getHelperGaugeName(name), new LongGauge()));
    }

    /**
     * Mark the occurrence of a given number of events.
     * Meter
     *
     * @param name
     * @param count
     */
    public static void setMeterCount(String name, long count) {
        getMeter(name).mark(count);
    }

    public static long getMeterCount(String name) {
        return getMeter(name).count();
    }

    public static double getMeterOneMinuteRate(String name) {
        return getMeter(name).oneMinuteRate();
    }

    public static double getMeterFiveMinuteRate(String name) {
        return getMeter(name).fiveMinuteRate();
    }

    public static double getMeterFifteenMinuteRate(String name) {
        return getMeter(name).fifteenMinuteRate();
    }

    public static double getMeterMeanRate(String name) {
        return getMeter(name).meanRate();
    }

    private static Meter getMeter(String name) {
        return meters.computeIfAbsent(name,
                name1 -> mr.newMeter(MetricsCounter.class, getMeterName(name1), "Meter", TimeUnit.SECONDS));
    }


    /**
     * A timer measures both the rate that a particular piece of code is called and the distribution of its duration.
     *
     * @param name
     * @param duration
     */
    public static void setTimerValue(String name, long duration) {
        getTimer(name).update(duration, TimeUnit.MILLISECONDS);
    }

    public static double getTimerMeanRateValue(String name) {
        return getTimer(name).meanRate();
    }

    public static double getTimerMeanValue(String name) {
        return getTimer(name).mean();
    }

    public static double getTimerMinValue(String name) {
        return getTimer(name).min();
    }

    public static double getTimerMaxValue(String name) {
        return getTimer(name).max();
    }

    /**
     * get a timer by name, timer could be used to measure the performance of a piece of code
     *
     * @param name
     * @return
     */
    public static Timer getTimer(String name) {
        return timers.computeIfAbsent(name,
                name1 -> mr.newTimer(MetricsCounter.class, getTimerName(name)));
    }

    /**
     * Counter
     *
     * @param name
     * @param count
     */
    public static void setCounterCount(String name, long count) {
        getCounter(name).inc(count);
    }

    private static Counter getCounter(String name) {
        return counters.computeIfAbsent(name,
                name1 -> mr.newCounter(MetricsCounter.class, getCounterName(name)));
    }

    public static long getCounterCount(String name) {
        return getCounter(name).count();
    }

    /**
     * set Histogram
     *
     * @param name
     * @param value
     */
    public static void setHistogramValue(String name, long value) {
        getHistogram(name).update(value);
    }

    public static double getHistogramMedianValue(String name) {
        return getHistogram(name).getSnapshot().getMedian();
    }

    public static double getHistogram75thValue(String name) {
        return getHistogram(name).getSnapshot().get75thPercentile();
    }

    public static double getHistogram95thValue(String name) {
        return getHistogram(name).getSnapshot().get95thPercentile();
    }

    public static double getHistogram98thValue(String name) {
        return getHistogram(name).getSnapshot().get98thPercentile();
    }

    public static double getHistogram99thValue(String name) {
        return getHistogram(name).getSnapshot().get99thPercentile();
    }

    public static double getHistogram999thValue(String name) {
        return getHistogram(name).getSnapshot().get999thPercentile();
    }

    /**
     * get Histogram by name
     *
     * @param name
     * @return
     */
    private static Histogram getHistogram(String name) {
        return histograms.computeIfAbsent(name,
                name1 -> mr.newHistogram(MetricsCounter.class, getHistogramName(name)));
    }
    // ------------basic counter method end----------------

    /**
     * For push data to local Agent
     *
     * @return
     */
    public static String toJsonArrayString() {
        JsonArray data = new JsonArray();

        getLongGaugeData(data);
        getDoubleGaugeData(data);
        getCounterData(data);
        getMeterData(data);
        getTimerData(data);
        getHistogramData(data);

        return data.toString();
    }

    private static void getLongGaugeData(JsonArray jsonArray) {
        for (Map.Entry<String, LongGauge> entry : longGauges.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromGauge(entry.getKey(), entry.getValue());
        }
    }

    private static void getDoubleGaugeData(JsonArray jsonArray) {
        for (Map.Entry<String, DoubleGauge> entry : doubleGauges.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromGauge(entry.getKey(), entry.getValue());
        }
    }

    private static void getCounterData(JsonArray jsonArray) {
        for (Map.Entry<String, Counter> entry : counters.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromCounter(entry.getKey(), entry.getValue());

            falconDataBuilder.buildStepTotal(entry.getKey(), entry.getValue().count(), getHelperLongGaugeValue(entry.getKey()));
            setHelperLongGauge(entry.getKey(), entry.getValue().count());
        }
    }

    private static void getMeterData(JsonArray jsonArray) {
        for (Map.Entry<String, Meter> entry : meters.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromMeter(entry.getKey(), entry.getValue());
        }
    }

    private static void getTimerData(JsonArray jsonArray) {
        for (Map.Entry<String, Timer> entry : timers.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromTimer(entry.getKey(), entry.getValue());
            entry.getValue().clear();
        }
    }

    private static void getHistogramData(JsonArray jsonArray) {
        for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
            FalconDataBuilder falconDataBuilder = new FalconDataBuilder(tags.get(entry.getKey()), jsonArray);
            falconDataBuilder.buildFromHistogram(entry.getKey(), entry.getValue());
            entry.getValue().clear();
        }
    }
}
