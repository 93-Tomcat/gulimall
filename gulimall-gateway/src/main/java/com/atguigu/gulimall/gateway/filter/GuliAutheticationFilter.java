package com.atguigu.gulimall.gateway.filter;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证过滤器
 * 1、写一个全局的网关的过滤器
 * 2、把他加入到容器中
 *
 *
 * 3、只有能访问的请求才拦截
 *
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GuliAutheticationFilter implements GlobalFilter {


    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * Webflux的编程方法，流式编程；
     * @param exchange
     * @param chain
     * @return
     *
     * doFilter(req,resp,filterChain){
     *
     * }
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("网关全局令牌验证开始.....");
        //1、判断请求头中是否携带了授权字段 Authorization：如果携带就验令牌；
        //Bearer dsdadasdasdada

        //token jwt  oauth2
        //Bearer jwt
        //OAuth dsada
        ServerHttpRequest request = exchange.getRequest();
        List<String> strings = request.getHeaders().get("Authorization");
        if(strings != null && strings.size()>0){
            String s = strings.get(0);
            //System.out.println("取出来东西了....?"+strings.get(0));
            try {
                //验证令牌通过，放行了
                Map<String, Object> body = GuliJwtUtils.getJwtBody(s);
                //自动进行redis的数据续期
                //{"id":1,"token":"b7c1ce9e434a4827b495c191e943aec6"}
                String token = (String) body.get("token");
                String key = Constant.LOGIN_USER_PREFIX+token;
                //自动续期
                redisTemplate.expire(key,30L, TimeUnit.MINUTES);

                return chain.filter(exchange);
            }catch (Exception e){
                //检查失败
                ServerHttpResponse response = exchange.getResponse();
                //设置403状态。
                response.setStatusCode(HttpStatus.FORBIDDEN);

                return response.setComplete();
            }

            
        }

        log.info("网关全局令牌验证结束.....");

        //
        return chain.filter(exchange);
    }
}
