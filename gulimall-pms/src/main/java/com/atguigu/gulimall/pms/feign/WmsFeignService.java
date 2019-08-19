package com.atguigu.gulimall.pms.feign;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-wms")
public interface WmsFeignService {

    @PostMapping("/wms/waresku/skus")
    public Resp<List<SkuStockVo>> skuWareInfos(@RequestBody List<Long> skuIds);
}
