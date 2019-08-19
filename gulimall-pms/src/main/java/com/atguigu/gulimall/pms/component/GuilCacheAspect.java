package com.atguigu.gulimall.pms.component;
import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.pms.annotation.GuliCache;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 0.导入AOP-start的
 * 1.声明这是一个切面,放进容器中
 * 2.申明通知方法和切入点表达式
 */
@Component
@Aspect
@Slf4j
public class GuilCacheAspect {

    @Autowired
    StringRedisTemplate redisTemplate;

    ReentrantLock lock = new ReentrantLock();

    /**
     * 环绕通知
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.atguigu.gulimall.pms.annotation.GuliCache)")
    public Object around(ProceedingJoinPoint point) throws Throwable{

        Object result = null;

        Object[] args = point.getArgs();

        //拿到注解的值
        //方法名
        MethodSignature signature = (MethodSignature)point.getSignature();
        GuliCache guliCache = signature.getMethod().getAnnotation(GuliCache.class);

        if (guliCache == null){
            //无需缓存
            return point.proceed(args);
        }

        //需要缓存
        String prefix = guliCache.prefix();
        if (args != null){
            for (Object arg : args) {
                prefix += ":"+arg.toString();
            }
        }

        //去查这个方法之前有没有缓存的数据
/*
        String s = redisTemplate.opsForValue().get(prefix);
        if (!StringUtils.isEmpty(s)){
            //如果拿到的数据不是null,缓存有数据,目标方法不用执行
            Class type = signature.getReturnType();
            result = JSON.parseObject(s,type);
        }else{
            //目标方法真正执行
            lock.lock();
            result =  point.proceed(args);

            redisTemplate.opsForValue().set(prefix,JSON.toJSONString(result));
        }
*/

        Object cache = getFromCache(prefix, signature);
        if (cache != null){
            return cache;
        }else {
            lock.lock();
            //双检查
            cache = getFromCache(prefix, signature);
            if (cache == null){
                result =  point.proceed(args);
                redisTemplate.opsForValue().set(prefix,JSON.toJSONString(result));
            }else {
               return cache;
            }
        }


        if (lock.isLocked()){
            lock.unlock();
        }


        return result;
    }

    public Object getFromCache(String prefix,Signature signature){
        String s = redisTemplate.opsForValue().get(prefix);
        if (!StringUtils.isEmpty(s)){
            //如果拿到的数据不是null,缓存有数据,目标方法不用执行
            Class type = ((MethodSignature)signature).getReturnType();
             return JSON.parseObject(s,type);
        }
        return null;
    }

    public void clearCurrentCache(String prefix){
        redisTemplate.delete(prefix);
    }
}
