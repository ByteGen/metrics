package com.bytegen.common.metrics.databuilder;

import com.bytegen.common.metrics.model.Tag;
import com.bytegen.common.metrics.constant.CounterConst;
import com.bytegen.common.metrics.provider.MetricsProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.Summarizable;
import com.yammer.metrics.core.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class FalconDataBuilder extends AbstractDataBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FalconDataBuilder.class);
    private static final MetricsProvider provider = MetricsProvider.getInstance();

    private Tag tag;
    private JsonArray jsonArray;

    private String endPoint;
    private long timeStamp;
    private long step;
    private Tag jobTag;

    public FalconDataBuilder(Tag tag, JsonArray jsonArray) {
        this.tag = tag;
        this.jsonArray = jsonArray;

        this.endPoint = provider.getEndPoint();
        this.timeStamp = provider.getCurrentTimeInSeconds();
        this.step = provider.getStep();
        this.jobTag = provider.getJobTag();
    }

    @Override
    public void buildFromCounter(String name, long value) {
        try {
            // 1. counter
            JsonObject json = new JsonObject();
            String tags = new Tag.TagBuilder().put(jobTag).put(tag).build().toTagString();
            json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
            json.addProperty(CounterConst.AGENT_KEY_METRIC, name);
            json.addProperty(CounterConst.AGENT_KEY_VALUE, value);
            json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "COUNTER");
            json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
            json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
            json.addProperty(CounterConst.AGENT_KEY_STEP, step);
            jsonArray.add(json);

        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon from number. ", e);
        }
    }

    @Override
    public void buildFromGauge(String name, Number number) {
        try {
            // 1. Gauge
            JsonObject json = new JsonObject();
            String tags = new Tag.TagBuilder().put(jobTag).put(tag).build().toTagString();
            json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
            json.addProperty(CounterConst.AGENT_KEY_METRIC, name);
            json.addProperty(CounterConst.AGENT_KEY_VALUE, number);
            json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
            json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
            json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
            json.addProperty(CounterConst.AGENT_KEY_STEP, step);
            jsonArray.add(json);

        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon from gauge. ", e);
        }
    }

    @Override
    public void buildFromMetered(String name, Metered metered) {
        try {
            {
                //1. count
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, name);
                json.addProperty(CounterConst.AGENT_KEY_VALUE, metered.count());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "COUNTER");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);

            }

            {
                //2. CPS-1-min
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "CPS-1-min");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, metered.oneMinuteRate());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

            {
                //3. CPS-5-min
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "CPS-5-min");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, metered.fiveMinuteRate());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon from metered. ", e);
        }
    }

    @Override
    public void buildFromTimed(String name, Timer timer) {
        try {
            {
                // 1. 75-percentile
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "75-percentile");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, timer.getSnapshot().get75thPercentile());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

            {
                // 2. 95-percentile
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "95-percentile");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, timer.getSnapshot().get95thPercentile());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

            {
                // 3. 99-percentile
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "99-percentile");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, timer.getSnapshot().get99thPercentile());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }
        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon from snapshot. ", e);
        }

    }

    public void buildFromSummary(String name, Summarizable summary) {
        try {
            {
                // 1. min-value
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "min-value");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, summary.min());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

            {
                // 2. max-value
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "max-value");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, summary.max());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }

            {
                // 3. mean-value
                JsonObject json = new JsonObject();
                String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
                json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
                json.addProperty(CounterConst.AGENT_KEY_METRIC, "mean-value");
                json.addProperty(CounterConst.AGENT_KEY_VALUE, summary.mean());
                json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
                json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
                json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
                json.addProperty(CounterConst.AGENT_KEY_STEP, step);
                jsonArray.add(json);
            }
        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon from histogram. ", e);
        }
    }


    ///////////////////////////////////////
    ////  the following is for special ////
    public void buildStepTotal(String name, long value, long oldValue) {
        try {
            // 1. step total
            JsonObject json = new JsonObject();
            String tags = new Tag.TagBuilder().put(jobTag).put(tag).put("name", name).build().toTagString();
            json.addProperty(CounterConst.AGENT_KEY_TAGS, tags);
            json.addProperty(CounterConst.AGENT_KEY_METRIC, "STEP-total");
            json.addProperty(CounterConst.AGENT_KEY_VALUE, value - oldValue);
            json.addProperty(CounterConst.AGENT_KEY_COUNTER_TYPE, "GAUGE");
            json.addProperty(CounterConst.AGENT_KEY_END_POINT, endPoint);
            json.addProperty(CounterConst.AGENT_KEY_TIME_STAMP, timeStamp);
            json.addProperty(CounterConst.AGENT_KEY_STEP, step);
            jsonArray.add(json);

        } catch (Exception e) {
            LOGGER.error("Metrics: failed to build falcon for step total. ", e);
        }
    }
}
