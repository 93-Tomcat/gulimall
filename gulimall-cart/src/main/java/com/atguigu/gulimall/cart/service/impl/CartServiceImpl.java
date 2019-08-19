package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.cart.feign.SkuCouponRedutionFeignService;
import com.atguigu.gulimall.cart.feign.SkuFeignService;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.to.SkuCouponTo;
import com.atguigu.gulimall.cart.vo.CartItemVo;
import com.atguigu.gulimall.cart.vo.CartVo;
import com.atguigu.gulimall.cart.vo.SkuCouponVo;
import com.atguigu.gulimall.cart.vo.SkuFullReductionVo;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuInfoVo;
import com.atguigu.gulimall.commons.utils.GuliJwtUtils;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Autowired
    SkuFeignService skuFeignService;

    @Autowired
    SkuCouponRedutionFeignService skuCouponRedutionFeignService;

    @Autowired
    @Qualifier("mainExecutor")
    ThreadPoolExecutor executor;

    /**
     * 获取购物车
     *
     * @param userKey
     * @param authorization
     *
     * @return
     */
    @Override
    public CartVo getCart(String userKey, String authorization) throws ExecutionException, InterruptedException {

        CartVo cartVo = new CartVo();

        CartKey cartKey = getKey(userKey, authorization);
        //购物车在Redis中保存

        if (cartKey.isLogin()) {
            //登录了；在线的购物车；
            //合并购物车
            mergeCart(userKey, Long.parseLong(cartKey.getKey()));
        } else {
            cartVo.setUserKey(cartKey.getKey());
        }

        //获取购物车的购物项
        List<CartItemVo> cartItems = getCartItems(cartKey.getKey());
        cartVo.setItems(cartItems);


        return cartVo;
    }

    /**
     * 临时购物车。临时userKey
     * 在线购物车。userId
     *
     * @param skuId
     * @param num
     * @param userKey
     * @param authorization
     *
     * @return
     */
    @Override
    public CartVo addToCart(Long skuId, Integer num, String userKey, String authorization) throws ExecutionException, InterruptedException {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();
        //1、获取购物车
        RMap<String, Object> map = redisson.getMap(Constant.CART_PREFIX + cartKey);


        if (key.isLogin() && !StringUtils.isEmpty(userKey)) {
            //登录状态,合并购物车。
            mergeCart(userKey, Long.parseLong(cartKey));
        }

        //将此商品添加到购物车
        CartItemVo itemVo = addCartItemVo(skuId, num, cartKey);


        CartVo cartVo = new CartVo();
        if (!key.isLogin()) {
            //没登录每次都将临时购物车的userKey返回给前端。
            cartVo.setUserKey(cartKey);
        }
        cartVo.setItems(Arrays.asList(itemVo));
        //未登录购物车一个月过期。自动续期，
        if (!key.isLogin()) {
            redisTemplate.expire(Constant.CART_PREFIX + cartKey, Constant.UNLOGIN_CART_TIMEOUT, TimeUnit.MINUTES);
        }

        return cartVo;
    }

    @Override
    public CartVo updateCart(Long skuId, Integer num, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        String keyKey = key.getKey();
        RMap<String, String> cart = redisson.getMap(Constant.CART_PREFIX + keyKey);


        String itemJson = cart.get(skuId.toString());
        CartItemVo itemVo = JSON.parseObject(itemJson, CartItemVo.class);
        itemVo.setNum(num);
        //修改购物车，覆盖redis数据；
        cart.put(skuId.toString(), JSON.toJSONString(itemVo));


        //获取购物车最新的所有购物项
        List<CartItemVo> cartItems = getCartItems(keyKey);
        CartVo cartVo = new CartVo();
        cartVo.setItems(cartItems);

        return cartVo;
    }

    @Override
    public CartVo checkCart(Long[] skuId, Integer status, String userKey, String authorization) {
        CartKey key = getKey(userKey, authorization);
        String cartKey = key.getKey();
        RMap<String, String> cart = redisson.getMap(Constant.CART_PREFIX + cartKey);

        if (skuId != null && skuId.length > 0) {
            for (Long sku : skuId) {
                String json = cart.get(sku.toString());
                CartItemVo itemVo = JSON.parseObject(json, CartItemVo.class);
                itemVo.setCheck(status == 0 ? false : true);
                //更新购物车
                cart.put(sku.toString(), JSON.toJSONString(itemVo));
            }
        }


        //获取到这个购物车的所有记录
        List<CartItemVo> cartItems = getCartItems(cartKey);

        CartVo cartVo = new CartVo();
        cartVo.setItems(cartItems);

        return cartVo;
    }


    /**
     * 1）、只要登录了返回的是用户id；
     * <p>
     * 2）、没登录返回临时的购物车key
     *
     * @param userKey
     * @param authorization
     *
     * @return
     */
    private CartKey getKey(String userKey, String authorization) {

        CartKey cartKey = new CartKey();
        String key = "";

        if (!StringUtils.isEmpty(authorization)) {
            //登录了;
            Map<String, Object> body = GuliJwtUtils.getJwtBody(authorization);
            Long id = Long.parseLong(body.get("id").toString());
            key = id + "";
            cartKey.setKey(key);
            cartKey.setLogin(true);
            if (!StringUtils.isEmpty(userKey)) {
                cartKey.setMerge(true);
            }
        } else {
            //没登录


            //第一次啥都没有
            if (!StringUtils.isEmpty(userKey)) {
                key = userKey;
                cartKey.setLogin(false);

                cartKey.setMerge(false);
            } else {
                key = UUID.randomUUID().toString().replace("-", "");
                cartKey.setLogin(false);
                cartKey.setMerge(false);
                cartKey.setTemp(true);//这是一个临时
            }

        }

        cartKey.setKey(key);
        return cartKey;

    }


    /**
     * 将临时购物车和在线购物车合并
     *
     * @param userKey
     * @param userId
     */
    private void mergeCart(String userKey, Long userId) throws ExecutionException, InterruptedException {
        RMap<String, String> tempCart = redisson.getMap(Constant.CART_PREFIX + userKey);

        //合并
        Collection<String> values = tempCart.values();
        if (values != null && values.size() > 0) {
            for (String value : values) {
                CartItemVo itemVo = JSON.parseObject(value, CartItemVo.class);
                //将临时购物车里面的这个购物项添加到在线购物车里面
                addCartItemVo(itemVo.getSkuId(), itemVo.getNum(), userId.toString());
            }
        }

        //合并以后删除临时购物车
        redisTemplate.delete(Constant.CART_PREFIX + userKey);

    }

    /**
     * 将skuId添加到指定购物车
     *
     * @param skuId
     * @param num
     * @param cartKey 不用传递拼前缀的，我们自动拼
     *
     * @return
     */
    private CartItemVo addCartItemVo(Long skuId, Integer num, String cartKey) throws ExecutionException, InterruptedException {
        CartItemVo vo = null;
        RMap<String, String> cart = redisson.getMap(Constant.CART_PREFIX + cartKey);

        //2、添加购物车之前先确定购物车中有没有这个商品，如果有就数量+1 如果没有新增
        String item = (String) cart.get(skuId.toString());
        if (!StringUtils.isEmpty(item)) {
            //购车之前有
            CartItemVo itemVo = JSON.parseObject(item, CartItemVo.class);
            itemVo.setNum(itemVo.getNum() + num);
            cart.put(skuId.toString(), JSON.toJSONString(itemVo));
            vo = itemVo;
        } else {
            CartItemVo itemVo = new CartItemVo();

            //1）、封装基本信息
            CompletableFuture<Void> infoAsync = CompletableFuture.runAsync(() -> {
                //1、查询sku当前商品的详情；
                Resp<SkuInfoVo> sKuInfoForCart = skuFeignService.getSKuInfoForCart(skuId);
                SkuInfoVo data = sKuInfoForCart.getData();
                //2、购物项
                BeanUtils.copyProperties(data, itemVo);
                itemVo.setNum(num);
            }, executor);


            //2）、封装了优惠券信息
            CompletableFuture<Void> couponAsync = CompletableFuture.runAsync(() -> {

                //3、获取 当前购物项的优惠券相关信息  //itemVo.setCoupons();
                Resp<List<SkuCouponTo>> coupons = skuCouponRedutionFeignService.getCoupons(skuId);

                //To封装别人传来的数据
                List<SkuCouponTo> data = coupons.getData();


                //vo提取别人传来的数据里面有用的数据
                List<SkuCouponVo> vos = new ArrayList<>();
                if (data != null && data.size() > 0) {
                    for (SkuCouponTo datum : data) {
                        SkuCouponVo couponVo = new SkuCouponVo();
                        BeanUtils.copyProperties(datum, couponVo);
                        vos.add(couponVo);
                    }
                }
                itemVo.setCoupons(vos);
            }, executor);


            //3、封装商品的满减信息
            CompletableFuture<Void> reductionAsync = CompletableFuture.runAsync(() -> {
                Resp<List<SkuFullReductionVo>> redutions = skuCouponRedutionFeignService.getRedutions(skuId);
                List<SkuFullReductionVo> data = redutions.getData();
                if (data != null && data.size() > 0) {
                    itemVo.setReductions(data);
                }
            }, executor);



            CompletableFuture.allOf(infoAsync,couponAsync,reductionAsync).get();
            //3、保存购物车数据
            cart.put(skuId.toString(), JSON.toJSONString(itemVo));

            vo = itemVo;
        }


        return vo;
    }

    /**
     * 不用传递前缀
     *
     * @param cartKey
     *
     * @return
     */
    private List<CartItemVo> getCartItems(String cartKey) {
        List<CartItemVo> vos = new ArrayList<>();
        //1、没登录获取临时购物车
        RMap<String, String> cart = redisson.getMap(Constant.CART_PREFIX + cartKey);
        Collection<String> values = cart.values();
        if (values != null && values.size() > 0) {
            for (String value : values) {
                CartItemVo itemVo = JSON.parseObject(value, CartItemVo.class);
                vos.add(itemVo);
            }
        }
        return vos;
    }
}

@Data
class CartKey {
    private String key;
    private boolean login;
    private boolean temp;
    private boolean merge;

}