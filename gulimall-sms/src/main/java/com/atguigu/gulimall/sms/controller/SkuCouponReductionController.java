package com.atguigu.gulimall.sms.controller;


import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.service.SkuCouponReductionService;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@ApiOperation("sku优惠券以及满减信息检索")
@RestController
@RequestMapping("/sms")
public class SkuCouponReductionController {


    @Autowired
    SkuCouponReductionService skuCouponReductionService;
    /**
     * gulimall-core maven仓库有核心包；
     *
     * @param skuId
     * @return
     */
    @GetMapping("/sku/coupon/{skuId}")
    public Resp<List<SkuCouponTo>> getCoupons(@PathVariable("skuId") Long skuId){



        List<SkuCouponTo> tos = skuCouponReductionService.getCoupons(skuId);
        return Resp.ok(tos);
    }

    @GetMapping("/sku/reduction/{skuId}")
    public Resp<List<SkuReductionTo>> getRedutions(@PathVariable("skuId") Long skuId){

        List<SkuReductionTo> tos = skuCouponReductionService.getRedutions(skuId);
        return Resp.ok(tos);
    }
}
