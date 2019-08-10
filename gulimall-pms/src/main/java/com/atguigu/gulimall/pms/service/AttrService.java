package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.vo.AttrSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:11:20
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);

    PageVo queryPageCatelogBaseAttrs(QueryCondition queryCondition, Long catId);

    PageVo queryPageCatelogSaleAttrs(QueryCondition queryCondition, Long catId);

    void saveAttrAndRelation(AttrSaveVo attr);
}

