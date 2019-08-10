package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.sms.dao.SkuBoundsDao;
import com.atguigu.gulimall.sms.entity.SkuBoundsEntity;
import com.atguigu.gulimall.sms.service.SkuBoundsService;

import java.util.List;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {

    @Autowired
    SkuBoundsDao boundsDao;

    @Autowired
    SkuLadderDao ladderDao;

    @Autowired
    SkuFullReductionDao fullReductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public void saveSkuAllSaleInfo(List<SkuSaleInfoTo> to) {

        if (to != null && to.size() > 0) {
            for (SkuSaleInfoTo info : to) {

                //sku_bounds  积分信息的保存
                SkuBoundsEntity boundsEntity = new SkuBoundsEntity();
                //优惠生效情况  四个状态位 从左到右
                // 0 - 无优惠,成长积分是否赠送
                // 1 - 无优惠,购物积分是否增送
                // 2 - 有优惠,成长积分是否增送
                // 3 - 有优惠,购物积分是否赠送[状态位 0:不赠送  1:赠送]

                Integer[] work = info.getWork();
                Integer i = work[3]*1+work[2]*2+work[1]*4+work[0]*8;

                boundsEntity.setWork(i);
                boundsEntity.setBuyBounds(info.getBuyBounds());
                boundsEntity.setGrowBounds(info.getGrowBounds());
                boundsEntity.setSkuId(info.getSkuId());

                boundsDao.insert(boundsEntity);

                //sku_ladder  阶梯价格的保存
                SkuLadderEntity ladderEntity = new SkuLadderEntity();
                ladderEntity.setFullCount(info.getFullCount());
                ladderEntity.setDiscount(info.getDiscount());
                ladderEntity.setAddOther(info.getLadderAddOther());
                ladderEntity.setSkuId(info.getSkuId());

                ladderDao.insert(ladderEntity);

                //sku_full_reduction  满减信息保存
                SkuFullReductionEntity fullReductionEntity = new SkuFullReductionEntity();
                BeanUtils.copyProperties(info,fullReductionEntity);
                fullReductionEntity.setAddOther(info.getFullAddOther());

                fullReductionDao.insert(fullReductionEntity);
            }
        }


    }


}