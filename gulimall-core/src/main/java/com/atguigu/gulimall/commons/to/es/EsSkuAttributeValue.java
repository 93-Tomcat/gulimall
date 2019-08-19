package com.atguigu.gulimall.commons.to.es;

import lombok.Data;

/**
 * 这是商品和属性的关联关系vo
 */
@Data
public class EsSkuAttributeValue {

    private static final long serialVersionUID = 1L;
    private Long id;  //商品和属性关联的数据表的主键id
    private Long productAttributeId; //当前sku对应的属性的attr_id
    private String name;//属性名  电池
    private String value;//3G   3000mah
    private Long spuId;//这个属性关系对应的spu的id

}
