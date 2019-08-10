package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spu的全量信息[spu基本信息,spu的基本属性,所有sku的基本信息,sku的促销信息]
 */
@Data
public class SpuAllSaveVo extends SpuInfoEntity {

    //spu的详情图
    private String[] spuImages;

    //当前spu的所有基本属性值
    private List<BaseAttrVo> baseAttrs;

    //当前spu所对应的的sku信息
    private List<SkuVo> skus;
}




