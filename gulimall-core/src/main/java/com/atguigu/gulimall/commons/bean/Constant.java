package com.atguigu.gulimall.commons.bean;

public class Constant {

    public static final String ES_SPU_INDEX = "gulimall";
    public static final String ES_SPU_TYPE = "spu";
    public static final String CACHE_CATELOG = "cache:catelog:";
    public static final String LOGIN_USER_PREFIX = "login:user:";
    public static final Long LOGIN_USER_TIMEOUT_MINUTES = 30L;//30分钟
    public static  final String CART_PREFIX = "cart:user:";
    public static  final Long UNLOGIN_CART_TIMEOUT = 60*24*30L;//未登录状态购物车数据只有一个月的时长

}
