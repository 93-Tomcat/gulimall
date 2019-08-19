package com.atguigu.gulimall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.pms.annotation.GuliCache;
import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.CategoryDao;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> getCategoryByLevel(Integer level) {

        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();

        if (level != 0){
            wrapper.eq("cat_level",level);
        }

        List<CategoryEntity> entities = categoryDao.selectList(wrapper);
        return entities;
    }

    @Override
    public List<CategoryEntity> getCategoryChildrensById(Integer catId) {

        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_cid",catId);
        List<CategoryEntity> list = categoryDao.selectList(wrapper);

        return list;
    }

    @GuliCache(prefix = Constant.CACHE_CATELOG)
    @Override
    public List<CategoryWithChildrensVo> getCategoryChildrensAndSubsById(Integer id) {

        List<CategoryWithChildrensVo> vos = categoryDao.selectCategoryChildrenWithChildrens(id);;
        /**
         * 1.缓存穿透:null值缓存,设置短暂的过期时间
         * 2.缓存雪崩:设置不同的过期时间
         * 3.缓存击穿:分布式锁
         *
         * key: 前缀+id
         */
/*        String s = redisTemplate.opsForValue().get(Constant.CACHE_CATELOG);

        if (!StringUtils.isEmpty(s)){
            //缓存中有去查缓存的
            vos =  JSON.parseArray(s,CategoryWithChildrensVo.class);

        }else{
            //1.缓存里没有去查数据库的
            vos = categoryDao.selectCategoryChildrenWithChildrens(id);
            //2.换到缓存中
            redisTemplate.opsForValue().set(Constant.CACHE_CATELOG,JSON.toJSONString(vos));
        }*/

        return vos;
    }

}