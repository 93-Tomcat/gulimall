package com.atguigu.gulimall.pms.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.service.SpuInfoService;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.pms.service.SkuInfoService;




/**
 * sku信息
 *
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:11:20
 */
@Api(tags = "sku信息 管理")
@RestController
@RequestMapping("pms/skuinfo")
public class SkuInfoController {


    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SpuInfoService spuInfoService;

    @ApiOperation("根据商品的id(spuId)查出所有的sku信息")
    @GetMapping("/list/spu/{spuId}")
    public Resp<List<SkuInfoEntity>> spuSkuInfo(

            @PathVariable("spuId")Long spuId){

        List<SkuInfoEntity> skus = skuInfoService.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));


        return Resp.ok(skus);
    }


    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:skuinfo:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = skuInfoService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{skuId}")
    @PreAuthorize("hasAuthority('pms:skuinfo:info')")
    public Resp<SkuInfoEntity> info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return Resp.ok(skuInfo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:skuinfo:save')")
    public Resp<Object> save(@RequestBody SpuAllSaveVo spuInfo){
		//skuInfoService.save(skuInfo);

        //数据存储到数据库  数据落盘
        spuInfoService.spuBigSaveAll(spuInfo);
        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:skuinfo:update')")
    public Resp<Object> update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:skuinfo:delete')")
    public Resp<Object> delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return Resp.ok(null);
    }

}
