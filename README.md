# Metrics
[Falcon](https://github.com/open-falcon/falcon-plus)打点的工具, 仅作学习参考使用.

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
    
    @MetricsAspect
    public void sampleMethod() {
        // do something
    }
}
```
