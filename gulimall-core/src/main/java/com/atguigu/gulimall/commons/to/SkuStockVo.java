package com.atguigu.gulimall.commons.to;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SkuStockVo {
    /**
     * sku_id
     */
    @ApiModelProperty(name = "skuId",value = "sku_id")
    private Long skuId;
    /**
     * 仓库id
     */
    @ApiModelProperty(name = "wareId",value = "仓库id")
    private Long wareId;
    /**
     * 库存数
     */
    @ApiModelProperty(name = "stock",value = "库存数")
    private Integer stock;
}
