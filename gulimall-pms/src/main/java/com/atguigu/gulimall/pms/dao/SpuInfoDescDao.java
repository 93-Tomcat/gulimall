package com.atguigu.gulimall.pms.dao;

import com.atguigu.gulimall.pms.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * spu信息介绍
 * 
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:11:20
 */
@Mapper
public interface SpuInfoDescDao extends BaseMapper<SpuInfoDescEntity> {

    void insertInfo(SpuInfoDescEntity entity);
}
