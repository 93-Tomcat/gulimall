package com.atguigu.gulimall.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class SmsSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //super.configure(http);
        http.authorizeRequests().antMatchers("/**").permitAll();

        //csrf不关闭  所有的post请求都要带令牌  所以前期测试要求关闭
        http.csrf().disable();
    }
}
