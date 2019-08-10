package com.atguigu.gulimall.pms.config;


import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.atguigu.gulimall.pms.feign")
public class PmsCloudConfig {

}
