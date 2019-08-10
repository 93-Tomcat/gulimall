package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.pms.dao.AttrDao;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.vo.AttrGroupWithAttrsVo;
import com.sun.xml.internal.fastinfoset.util.ValueArray;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.AttrGroupDao;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;

import java.util.ArrayList;
import java.util.List;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    AttrDao attrDao;


    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageAttrGroupsByCatId(QueryCondition queryCondition, Long catId) {
        
        //获取封装的分页条件
        IPage<AttrGroupEntity> page = new Query<AttrGroupEntity>().getPage(queryCondition);

        //获取查询条件
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id",catId);

        IPage<AttrGroupEntity> data = this.page(page, wrapper);

        //查出的所有分组
        List<AttrGroupEntity> records = data.getRecords();

        //查出每一个分组的属性以及他的属性信息的对象
        List<AttrGroupWithAttrsVo> groupWithAttrs = new ArrayList<>(records.size());

        for (AttrGroupEntity record : records) {

            //创建一个vo准备收集所有需要的数据
            AttrGroupWithAttrsVo vo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(record,vo);

            Long groupId = record.getAttrGroupId();

            //获取当前分组的所有属性
            List<AttrAttrgroupRelationEntity> relationEntities = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", groupId));

            if (relationEntities != null &&relationEntities.size()>0){
                List<Long> attrIds = new ArrayList<>();

                for (AttrAttrgroupRelationEntity entity : relationEntities) {
                    attrIds.add(entity.getAttrId());
                }

                //查出当前分组所有真正的属性
                List<AttrEntity> attrEntities = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds));
                vo.setAttrEntities(attrEntities);
            }


            //把vo放到集合中
            groupWithAttrs.add(vo);
        }

        return new PageVo(groupWithAttrs,data.getTotal(),data.getSize(),data.getCurrent());
    }

    @Override
    public AttrGroupEntity getGroupInfoByAttrId(Long attrId) {

        AttrGroupEntity attrGroupEntity = null;

                //1.根据attrId去中间表找到在哪一个组
        AttrAttrgroupRelationEntity one = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrId));

        //2.在根据分组id找到分组信息
        if (one != null){
            Long attrGroupId = one.getAttrGroupId();
            attrGroupEntity = attrGroupDao.selectById(attrGroupId);
        }

        return attrGroupEntity;
    }

}