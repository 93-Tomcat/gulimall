package com.atguigu.gulimall.pms.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GuliCache {

    String prefix() default "cache";
    //缓存的值就是方法的返回值
}
