package com.atguigu.gulimall.cart.service;

import com.atguigu.gulimall.cart.vo.CartVo;

import java.util.concurrent.ExecutionException;

public interface CartService {

    CartVo getCart(String userKey, String authorization) throws ExecutionException, InterruptedException ;

    CartVo addToCart(Long skuId, Integer num, String userKey, String authorization)  throws ExecutionException, InterruptedException ;

    CartVo updateCart(Long skuId, Integer num, String userKey, String authorization);

    CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization);
}
