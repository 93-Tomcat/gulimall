package com.atguigu.gulimall.pms.controller.api;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.service.ItemServie;
import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class ItemController {

    @Autowired
    ItemServie itemServie;

    @GetMapping("/item/{skuId}.html")
    public Resp<SkuItemDetailVo> skuDetails(

            @PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {

        //获取商品详情
        SkuItemDetailVo skuItemDetailVo = itemServie.getDetail(skuId);

        return Resp.ok(skuItemDetailVo);
    }
}
