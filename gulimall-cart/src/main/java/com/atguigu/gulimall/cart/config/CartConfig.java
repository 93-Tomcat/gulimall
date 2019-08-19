package com.atguigu.gulimall.cart.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class CartConfig {

    /**
     * int corePoolSize,
     * int maximumPoolSize,
     * long keepAliveTime,
     *                               TimeUnit unit,
     *                               BlockingQueue<Runnable> workQueue
     *
     *  8G;
     *  5G;
     *      1000:1G;
     *      4G;
     * @return
     */
    @Bean("mainExecutor")
    @Primary  //默认就是他
    public ThreadPoolExecutor mainThreadPoolExecutor(){
        //cpu核；
        //无界队列；
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 1000, 0L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(Integer.MAX_VALUE / 2));

        return executor;
    }

    @Bean("otherExecutor")
    public ThreadPoolExecutor noMainThreadPoolExecutor(){
        //cpu核；
        //无界队列；
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 1000, 0L,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(Integer.MAX_VALUE / 2));

        return executor;
    }
}
