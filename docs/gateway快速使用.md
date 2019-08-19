# Spring Cloud Gateway

## 1、创建网关项目
```xml
<properties>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.SR2</spring-cloud.version>
        <nacos.version>0.2.2.RELEASE</nacos.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>${nacos.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
            <version>${nacos.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
```

## 2、开启服务注册发现功能
```java
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

}
```

## 3、核心概念；

- Route：路由；把什么请求路由到那个地方；
- Predicate：断言（满足条件）；【RoutePredicateFactories】RoutePredicateFactory
- Filter：过滤器；

    只要满足Predicate指定的条件。就将请求Route到指定地方，
可以使用Filter来对请求产生一些定制的变化；
    - GatewayFilterFactory:旗下的所有是配置路由规则上具体路由某些请求才过滤
    
    - GlobalFilter：旗下的都是具有全局过滤功能的；
    org.springframework.cloud.gateway.filter

```yaml
示例

spring:
  cloud:
    gateway:
      routes:
      - id: after_route #定义第一种路由规则
        uri: http://example.org # 满足predicates指定的所有条件以后就来到uri指定的地方
        predicates:
        - After=2017-01-20T17:42:47.789-07:00[America/Denver]
        filters:
          - 过滤器的名=值
          
          - StripPrefix=2 #简单过滤器
          - name: Retry #过滤器名
            args: #过滤器定制的参数值
              retries: 3 #
              statuses: BAD_GATEWAY
```

```yaml

spring:
  cloud:
    gateway:
      routes:
      - id: world_route
        uri: lb://gulimall-pms
        predicates:
          - Path=/world
      - id: hello_route
        uri: lb://gulimall-oms
        predicates:
          - Path=/hello
        filters:
          - AddResponseHeader=leifengyang,666

```



