package com.atguigu.gulimall.oms.config;


import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class OmsSwaggerConfig {

    @Bean("订单管理系统")
    public Docket userApis(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("订单管理系统")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                .paths(PathSelectors.regex("/oms.*"))
                .build()
                .apiInfo(apiInfo())
                .enable(true);
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("谷粒商城-订单管理系统接口文档")
                .description("订单管理平台的文档")
                .termsOfServiceUrl("http://www.atguigu.com")
                .version("1.0")
                .build();
    }
}

