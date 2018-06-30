package com.bytegen.metrics.spring;

import com.bytegen.metrics.MetricsCounter;
import com.bytegen.metrics.provider.MetricsProvider;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * User: xiang
 * Date: 2018/6/29
 * Desc:
 */
@Aspect
@Component
public class MetricsProcess {
    private static final String SEPARATOR = ".";

    @Pointcut("@annotation(com.bytegen.metrics.spring.MetricsAspect)")
    public void beanAnnotatedWithMetrics() {
        // omit
    }

    @Around(value = "beanAnnotatedWithMetrics()&&" + "@annotation(metrics)", argNames = "joinPoint,metrics")
    public Object performanceTrace(ProceedingJoinPoint joinPoint, MetricsAspect metrics) throws Throwable {
        long timeStart = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            String name = generateName(joinPoint, metrics.value());
            // Use Meter to record count.
            MetricsCounter.setMeterCount(name, 1);
            // Use Timer to record duration and distribution.
            MetricsCounter.setTimerValue(name, (System.currentTimeMillis() - timeStart));
        }
    }

    private String generateName(ProceedingJoinPoint joinPoint, String name) {
        String prefix = MetricsProvider.getInstance().getMetricPrefix();
        if (StringUtils.isBlank(prefix)) {
            prefix = joinPoint.getSignature().getDeclaringTypeName();
        }

        if (StringUtils.isBlank(name)) {
            return joiner(prefix, joinPoint.getSignature().getName());
        }
        return joiner(prefix, name);
    }

    private String joiner(String... keys) {
        return StringUtils.join(keys, SEPARATOR);
    }

}
