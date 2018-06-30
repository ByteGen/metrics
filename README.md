# Metrics
[Falcon](https://github.com/open-falcon/falcon-plus)打点的工具, 仅作学习参考使用.

需要注意：metrics给出的CPS-1-min、CPS-5-min是一个rate值，并不是精确的数值。

## Usage

1. 添加 pom 依赖
```
<dependency>
    <groupId>com.bytegen</groupId>
    <artifactId>metrics</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
2. 如果使用 intercept 方式, 添加 web config
```java
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MetricsInterceptor()).addPathPatterns("/**");
    }
}
```
3. 如果使用 AOP 方式, 使用 annotation
```java
@Component
public class Sample {
    
    @MetricsAspect("testMetric")
    public void sampleMethod() {
        // do something
    }
}
```
4. // other use as:
```java
public class Sample {
 
    public void sampleMethod() {
        long timeStart = System.currentTimeMillis();
        
        // do something
        
        // Use Meter to record count.
        MetricsCounter.setMeterCount(name, 1);
        // Use Timer to record duration.
        MetricsCounter.setTimerValue(name, (System.currentTimeMillis() - timeStart));
        // Use Histogram to record distribution
        MetricsCounter.setHistogramValue(name, (System.currentTimeMillis() - timeStart));
    }
}
```
