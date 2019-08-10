package com.atguigu.gulimall.pms.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-sms")
public interface SmsSkuSaleInfoFeignService {

    @PostMapping("/sms/skubounds/saleinfo/save")
    public Resp<Object> saveSkuSaleInfos(@RequestBody List<SkuSaleInfoTo> to);
}
