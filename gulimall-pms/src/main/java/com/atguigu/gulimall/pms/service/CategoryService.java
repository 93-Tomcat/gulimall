package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:11:20
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntity> getCategoryByLevel(Integer level);

    List<CategoryEntity> getCategoryChildrensById(Integer catId);

    List<CategoryWithChildrensVo> getCategoryChildrensAndSubsById(Integer id);
}

