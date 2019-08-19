package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.vo.SkuItemDetailVo;

import java.util.concurrent.ExecutionException;

/**
 * 商品详情查询服务
 *
 */
public interface ItemServie {
    SkuItemDetailVo getDetail(Long skuId) throws ExecutionException, InterruptedException;
}
