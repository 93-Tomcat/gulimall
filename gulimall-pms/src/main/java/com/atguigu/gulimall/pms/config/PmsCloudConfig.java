package com.atguigu.gulimall.pms.config;


import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.gulimall.pms.feign")
public class PmsCloudConfig {


    /**
     * 我们的线程池
     *
     * @return
     */
    @Bean("mainThreadPool")  //主业务线程池
    public ThreadPoolExecutor threadPool(@Value("${app.main.thread.coreSize}") Integer coreSize,
                                         @Value("${app.main.thread.max}") Integer max) {
        /**
         * int corePoolSize,
         * int maximumPoolSize,
         * long keepAliveTime,
         * TimeUnit unit,
         * BlockingQueue<Runnable> workQueue
         */

        return new ThreadPoolExecutor(coreSize,
                max, 0L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(Integer.MAX_VALUE/2));
    }

}
