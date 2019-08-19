package com.atguigu.gulimall.sms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.sms.dao.CouponDao;
import com.atguigu.gulimall.sms.dao.SkuFullReductionDao;
import com.atguigu.gulimall.sms.dao.SkuLadderDao;
import com.atguigu.gulimall.sms.entity.CouponEntity;
import com.atguigu.gulimall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.sms.entity.SkuLadderEntity;
import com.atguigu.gulimall.sms.feign.SpuFeignService;
import com.atguigu.gulimall.sms.service.SkuCouponReductionService;
import com.atguigu.gulimall.sms.to.SkuCouponTo;
import com.atguigu.gulimall.sms.to.SkuInfoTo;
import com.atguigu.gulimall.sms.to.SkuReductionTo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkuCouponReductionServiceImpl implements SkuCouponReductionService {

    @Autowired
    CouponDao couponDao;

    @Autowired
    SkuLadderDao ladderDao;

    @Autowired
    SkuFullReductionDao reductionDao;

    @Autowired
    SpuFeignService spuFeignService;


    @Override
    public List<SkuCouponTo> getCoupons(Long skuId) {
        //1、根据skuId查出是spuId
        List<SkuCouponTo> couponTos = new ArrayList<>();

        Resp<SkuInfoTo> info = spuFeignService.info(skuId);
        if (info.getData() != null) {
            Long spuId = info.getData().getSpuId();
            List<CouponEntity> tos = couponDao.selectCouponsBySpuId(spuId);

            if (tos != null && tos.size() > 0) {
                for (CouponEntity to : tos) {
                    SkuCouponTo couponTo = new SkuCouponTo();
                    couponTo.setAmount(to.getAmount());
                    couponTo.setCouponId(to.getId());
                    couponTo.setDesc(to.getCouponName());
                    couponTo.setSkuId(skuId);

                    couponTos.add(couponTo);
                }
            }

        }

        return couponTos;
    }

    @Override
    public List<SkuReductionTo> getRedutions(Long skuId) {


        List<SkuLadderEntity> ladderEntities = ladderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));

        List<SkuFullReductionEntity> reductionEntities = reductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));

        List<SkuReductionTo> tos = new ArrayList<>();

        ladderEntities.forEach((item)->{
            SkuReductionTo to = new SkuReductionTo();
            // 0-打折
            BeanUtils.copyProperties(item,to);
            to.setDesc("满"+item.getFullCount()+"件，享受"+item.getDiscount()+"折优惠");
            to.setType(0);
            tos.add(to);
        });


        reductionEntities.forEach((item)->{
            SkuReductionTo to = new SkuReductionTo();
            //1-满减
            BeanUtils.copyProperties(item,to);
            to.setDesc("消费满"+item.getFullPrice()+"，减"+item.getReducePrice()+"");
            to.setType(1);
            tos.add(to);
        });


        return tos;
    }
}
