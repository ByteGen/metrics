package com.bytegen.metrics.spring;

import com.bytegen.metrics.provider.MetricsProvider;
import com.bytegen.metrics.MetricsCounter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
public class MetricsInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsInterceptor.class);

    private static final String SEPARATOR = ".";
    private static final String PERFORM_COUNTER_TIMER = "PerformCounterTimer_e734ca410a0dd6dab51378e378195cb9";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(PERFORM_COUNTER_TIMER, System.currentTimeMillis());
        return Boolean.TRUE;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // omitted
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String name = generateName(handler);
        Object timeAttr = request.getAttribute(PERFORM_COUNTER_TIMER);

        if (null == timeAttr) {
            // Use Meter to record count.
            MetricsCounter.setMeterCount(name, 1);
        } else {
            long timeStart = (Long) timeAttr;

            // Use Meter to record count.
            MetricsCounter.setMeterCount(name, 1);
            // Use Timer to record duration and distribution.
            MetricsCounter.setTimerValue(name, (System.currentTimeMillis() - timeStart));
        }

        if (ex != null) {
            String key = joiner(name, ex.getClass().getSimpleName());
            MetricsCounter.count(key, 1);
        }
    }

    public static String generateName(Object handler) {
        String prefix = MetricsProvider.getInstance().getMetricPrefix();

        if (null != handler && handler instanceof HandlerMethod) {
            // method invoke success
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            if (StringUtils.isBlank(prefix)) {
                prefix = handlerMethod.getMethod().getDeclaringClass().getTypeName();
            }
            return joiner(prefix, handlerMethod.getMethod().getName());
        } else {
            LOGGER.warn("Metrics: called with unknown handler: {} ", handler);
            return joiner(prefix, "unknownHandler");
        }
    }

    public static String joiner(String... keys) {
        return StringUtils.join(keys, SEPARATOR);
    }
}
