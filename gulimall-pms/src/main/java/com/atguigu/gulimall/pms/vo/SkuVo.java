package com.atguigu.gulimall.pms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo {

    /**sku的基本信息
     * 姓名   描述   标题   副标题  重量  商品价格  sku图片
     */

    private String skuName;

    private String skuDesc;

    private String skuTitle;

    private String skuSubtitle;

    private BigDecimal weight;

    private BigDecimal price;

    private String[] images;

    //sku对应的销售属性组合
    private List<SaleAttrVo> saleAttrs;


    //"buyBounds": 0, //赠送的购物积分
    // "growBounds": 0, //赠送的成长积分
    // "work": [0,1,1,0], //积分生效情况
    // "fullCount": 0, //满几件
    // "discount": 0, //打几折
    // "ladderAddOther": 0, //阶梯价格是否可以与其他优惠叠加
    // "fullPrice": 0, //满多少
    // "reducePrice": 0, //减多少
    // "fullAddOther": 0, //满减是否可以叠加其他优惠
    private BigDecimal growBounds;

    private BigDecimal buyBounds;

    //优惠的生效情况 [4个状态位,从右到左]
    //0 - 无优惠 成长积分是否赠送
    //1 - 无优惠 购物积分是否赠送
    //2 - 有优惠 成长积分是否赠送
    //3 - 有优惠 购物积分是否赠送
    private Integer[] work;
    //上面是积分设置

    private Integer fullCount;

    private BigDecimal discount;

    private Integer ladderAddOther;
    //上面是  阶梯价格的信息

    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

}
