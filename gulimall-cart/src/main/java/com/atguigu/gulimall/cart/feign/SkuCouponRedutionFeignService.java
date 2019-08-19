package com.atguigu.gulimall.cart.feign;


import com.atguigu.gulimall.cart.to.SkuCouponTo;
import com.atguigu.gulimall.cart.vo.SkuFullReductionVo;
import com.atguigu.gulimall.commons.bean.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-sms")
public interface SkuCouponRedutionFeignService {

    @GetMapping("/sms/sku/coupon/{skuId}")
    public Resp<List<SkuCouponTo>> getCoupons(@PathVariable("skuId") Long skuId);

    @GetMapping("/sms/sku/reduction/{skuId}")
    public Resp<List<SkuFullReductionVo>> getRedutions(@PathVariable("skuId") Long skuId);
}
