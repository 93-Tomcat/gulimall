package com.atguigu.gulimall.oms.dao;

import com.atguigu.gulimall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author 93丨
 * @email 17717080887_job@163.com
 * @date 2019-08-01 20:20:54
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
