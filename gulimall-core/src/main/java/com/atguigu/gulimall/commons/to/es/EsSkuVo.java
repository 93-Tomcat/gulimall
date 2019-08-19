package com.atguigu.gulimall.commons.to.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 以sku为单位存在es中；
 *
 * 1）、sku的基本信息
 * 2）、sku的品牌，分类，等...
 * 3）、sku的检索属性信息；
 *
 *
 */
@Data
public class EsSkuVo {

    private Long id;  //skuId
    private Long brandId; //品牌id
    private String brandName;  //品牌名
    private Long productCategoryId;  //sku的分类id
    private String productCategoryName; //sku的名字


    private String pic; //sku的默认图片
    private String name;//这是需要检索的sku的标题
    private BigDecimal price;//sku-price；
    private Integer sale;//sku-sale 销量
    private Integer stock;//sku-stock 库存
    private Integer sort;//排序分 热度分


    //保存当前sku所有需要检索的属性；
    //检索属性来源于sku的全部属性【基本属性，销售属性】中的search_type=1
    private List<EsSkuAttributeValue> attrValueList;//检索属性




}
