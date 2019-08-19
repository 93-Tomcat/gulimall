package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.to.SkuStockVo;
import com.atguigu.gulimall.commons.to.es.EsSkuAttributeValue;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.feign.EsFeignService;
import com.atguigu.gulimall.pms.feign.SmsSkuSaleInfoFeignService;
import com.atguigu.gulimall.pms.feign.WmsFeignService;
import com.atguigu.gulimall.pms.vo.BaseAttrVo;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDao spuInfoDao;

    @Autowired
    SpuInfoDescDao spuInfoDescDao;

    @Autowired
    ProductAttrValueDao spuAttrValueDao;

    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    SkuImagesDao imagesDao;

    @Autowired
    AttrDao attrDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    EsFeignService esFeignService;

    @Autowired
    SmsSkuSaleInfoFeignService smsSkuSaleInfoFeignService;

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    WmsFeignService wmsFeignService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryPageByCatId(QueryCondition queryCondition, Long catId) {

        //封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        if (catId != 0){
            //查全站
            wrapper.eq("catalog_id",catId);

            if (!StringUtils.isEmpty(queryCondition.getKey())){
                wrapper.and(obj -> {
                    obj.like("spu_name",queryCondition.getKey());
                    obj.or().like("id",queryCondition.getKey());
                    return obj;
                });
            }
        }

        //封装翻页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);

        //去数据库查询
        IPage<SpuInfoEntity> data = this.page(page, wrapper);

        //PageVo pageVo = new PageVo(data.getRecords(), data.getTotal(), data.getSize(), data.getCurrent());
        PageVo vo = new PageVo(data);

        return vo;
    }


    /**
     * 编译异常默认是不会滚的。
     * rollbackFor：指定让那些异常滚。
     * <p>
     * <p>
     * 运行时异常默认滚；
     * 默认生效。
     * <p>
     * timeout = 3;统计的是第一次开始sql与最后一次结束sql。
     * timeout特指数据库超时而不是业务超时
     *
     * @param spuInfo
     *
     *
     */

    @Override
    public void spuBigSaveAll(SpuAllSaveVo spuInfo) {

        /**
         * 第一次：
         *      大保存：
         *              this.saveSpuBaseInfo(设置了requires_new)
         *              this.saveSpuInfoImages(设置了requires_new)；
         * 如果有效果，数据库的这两个有东西，然而毫无变化；
         *
         * 第二次：
         *      大保存：
         *              this.saveSpuBaseInfo(设置了requires_new)
         *              spuInfoDescService.saveSpuInfoImages(设置了requires_new);
         *
         *  如果有效果，数据库的这两个有东西，然而别人就行，this不行；
         *
         * 第三次
         *      大保存：
         *          this代理.saveSpuBaseInfo(设置了requires_new)
         *          this代理.saveSpuInfoImages(设置了requires_new);
         *  这个是可以的.....
         *
         *
         *  为啥？
         *      @Transactional：底层是用aop；
         *      事务要生效必须是代理对象在调用；
         *      this不是代理对象，就相当于代码粘到了大方法里面，this.方法（）；是跟外面用的一个事务
         *
         *  解决：
         *      1）、把这些放别人的service；
         *      2）、如果能获取到本类的代理对象，直接调用本类方法就完事;
         *          如何获取：
         *              1）、导入aop的场景依赖；spring-boot-starter-aop
         *              2）、开启aop的高级功能；@EnableAspectJAutoProxy：开启自动代理
         *              3）、同时要暴露代理对象；@EnableAspectJAutoProxy(exposeProxy=true)
         *              4）、获取代理对象；
         *                  SpuInfoService proxy = (SpuInfoService) AopContext.currentProxy();
         */

        //1.保存spu的基本信息
        //1.1保存spu的基本信息
        Long spuId = this.saveSpuBaseInfo(spuInfo);
        //1.2保存spu的所有图片信息
        this.saveSpuInfoImages(spuId,spuInfo.getSpuImages());


        //2.保存spu的基本属性信息
        List<BaseAttrVo> baseAttrs = spuInfo.getBaseAttrs();
        this.saveSpuBaseAttrs(spuId,baseAttrs);


        //3.保存sku以及sku的营销相关信息
        this.saveSkuInfos(spuId,spuInfo.getSkus());
    }


    /**
     * 负责解析出数据做出相应的业务
     * @param spuInfo
     * @return
     */
    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuInfo) {


        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());

        spuInfoDao.insert(spuInfoEntity);

        return spuInfoEntity.getId();
    }

    @Override
    public void saveSpuInfoImages(Long spuId, String[] spuImages) {

        SpuInfoDescEntity entity = new SpuInfoDescEntity();
        entity.setSpuId(spuId);
        entity.setDecript(AppUtils.arrayToStringWithSeperator(spuImages,","));
        spuInfoDescDao.insertInfo(entity);


    }

    @Override
    public void saveSpuBaseAttrs(Long spuId, List<BaseAttrVo> baseAttrs) {

        ArrayList<ProductAttrValueEntity> allSave = new ArrayList<>();

        for (BaseAttrVo baseAttr : baseAttrs) {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setAttrId(baseAttr.getAttrId());
            entity.setAttrName(baseAttr.getAttrName());

            String[] selected = baseAttr.getValueSelected();
            entity.setAttrValue(AppUtils.arrayToStringWithSeperator(selected,","));
            entity.setAttrSort(0);
            entity.setQuickShow(1);
            entity.setSpuId(spuId);

            allSave.add(entity);
        }


        spuAttrValueDao.insertBatch(allSave);

    }


    /**
     * 传播行为；
     * 大保存：80s
     * 小保存：3s；
     * 但是小保存3s没作用；
     * <p>
     * 大保存{
     * 小保存(rollbackFor = {ArithmeticException.class},timeout = 3)
     * }
     *
     * @param spuId
     * @param skus
     */
    @GlobalTransactional
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        //查出spu的信息
        SpuInfoEntity spuInfo = this.getById(spuId);

        List<SkuSaleInfoTo> tos = new ArrayList<>();

        //保存sku的info信息
        for (SkuVo skuVo : skus) {

            String[] images = skuVo.getImages();

            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setBrandId(spuInfo.getId());
            skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,5).toUpperCase());

            if (images != null&& images.length>0){
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }

             skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            skuInfoEntity.setWeight(skuVo.getWeight());

            //保存sku的基本信息
            skuInfoDao.insert(skuInfoEntity);

            //保存sku的所有对应图片
            Long skuId = skuInfoEntity.getSkuId();
            for (int i = 0; i < images.length; i++) {
                SkuImagesEntity imagesEntity = new SkuImagesEntity();
                imagesEntity.setSkuId(skuId);
                imagesEntity.setDefaultImg(i == 0?1:0);
                imagesEntity.setImgUrl(images[i]);
                imagesEntity.setImgSort(0);
                imagesDao.insert(imagesEntity);
            }

            //当前sku的所有销售属性组合保存起来
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            for (SaleAttrVo attrVo : saleAttrs) {

                //查询当前属性的信息
                SkuSaleAttrValueEntity entity = new SkuSaleAttrValueEntity();
                entity.setAttrId(attrVo.getAttrId());
                //查出这个属性的真正的信息
                AttrEntity attrEntity = attrDao.selectById(attrVo.getAttrId());
                entity.setAttrName(attrEntity.getAttrName());
                entity.setAttrSort(0);
                entity.setAttrValue(attrVo.getAttrValue());
                entity.setSkuId(skuId);

                //sku与销售属性的关联关系
                skuSaleAttrValueDao.insert(entity);
            }

            //以上都是pms系统完成的工作


            //一下需要由sms完成,保存每一个sku的相关优惠数据
            SkuSaleInfoTo info = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo,info);
            info.setSkuId(skuId);
            tos.add(info);
        }

        //发给sms,让他去处理  我们不管
        smsSkuSaleInfoFeignService.saveSkuSaleInfos(tos);
    }

    /**
     * 商品上架/下架
     * @param spuId/商品ID
     * @param status/商品状态 是否是上架
     */
    @Override
    public void updateSpuStatus(Long spuId, Integer status) {

        if (status == 1){
            //上架
            spuUp(spuId,status);

        }else{

            spuDown(spuId,status);


        }

    }

    /**
     * 上架
     * @param spuId
     * @param status
     */
    private void spuUp(Long spuId,Integer status){

        //1.擦除我们接下来要使用的基本信息
        SpuInfoEntity spuInfoEntity = spuInfoDao.selectById(spuId);
        CategoryEntity categoryEntity = categoryDao.selectById(spuInfoEntity.getCatalogId());
        BrandEntity brandEntity = brandDao.selectById(spuInfoEntity.getBrandId());




        //2.上架:将商品需要的检索的信息放到es中,  下架:将商品需要检索的信息从es中删除
        List<EsSkuVo> skuVos = new ArrayList<>();
        //1)查出当前需要上架的spu的所有信息
        List<SkuInfoEntity> skus = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));

        //查出这个sku对应的sku的所有库存信息
        ArrayList<Long> skuIds = new ArrayList<>();
        skus.forEach(skuInfoEntity -> {
            skuIds.add(skuInfoEntity.getSkuId());
        });

        //1.2)远程检索到所有的sku的库存信息
        Resp<List<SkuStockVo>> infos = wmsFeignService.skuWareInfos(skuIds);
        List<SkuStockVo> skuStockVos = infos.getData();

        //1.3)查出所有可以供检索的属性
        List<ProductAttrValueEntity> spu_id = spuAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //1.4)过滤处可以检索的
        ArrayList<Long> attrIds = new ArrayList<>();
        spu_id.forEach(item ->{
            attrIds.add(item.getAttrId());
        });
        List<AttrEntity> list = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds).eq("search_type", 1));
        //1.5)在spu_id过滤出list的所有数据
        //List<ProductAttrValueEntity> productAttrValueEntities = new ArrayList<>();
        List<EsSkuAttributeValue> esSkuAttributeValues = new ArrayList<>();
        list.forEach(item ->{
            //当前能被检索的属性
            Long attrId = item.getAttrId();
            //拿到真正的值
            spu_id.forEach(s ->{
                if (item.getAttrId() == s.getAttrId()){
                    EsSkuAttributeValue value = new EsSkuAttributeValue();
                    value.setId(s.getId());
                    value.setName(s.getAttrName());
                    value.setProductAttributeId(s.getAttrId());
                    value.setSpuId(spuId);
                    value.setValue(s.getAttrValue());
                    esSkuAttributeValues.add(value);
                }
            });
        });

        //1.6)将productAttrValueEntities变成真正在es中存储的 vo对象




        if (skus !=null && skus.size() >0){
            //2)构造所有需要保存在es中的sku信息
            skus.forEach(skuInfoEntity -> {
                EsSkuVo skuVo = skuInfoToEsSkuVo(skuInfoEntity,spuInfoEntity,categoryEntity,brandEntity,skuStockVos,esSkuAttributeValues);
                skuVos.add(skuVo);
            });


            //3.远程调用search服务.将商品上架
            Resp<Object> resp = esFeignService.spuUp(skuVos);
            if (resp.getCode() ==0 ){
                //远程调用成功
                //本地修改数据库
                SpuInfoEntity entity = new SpuInfoEntity();
                entity.setId(spuId);
                entity.setPublishStatus(1);
                entity.setUodateTime(new Date());
                //按照id设置其他字段
                spuInfoDao.updateById(entity);

            }

        }




    }

    private void spuDown(Long spuId,Integer status){

    }


    /**
     * 将SkuInfoEntity加工成EsSkuVo
     * @param
     * @param spuInfoEntity
     * @param categoryEntity
     * @param brandEntity
     * @param skuStockVos
     * @param productAttrValueEntities
     * @return
     */
    private  EsSkuVo skuInfoToEsSkuVo(SkuInfoEntity sku, SpuInfoEntity spuInfoEntity, CategoryEntity categoryEntity, BrandEntity brandEntity, List<SkuStockVo> skuStockVos, List<EsSkuAttributeValue> productAttrValueEntities){

        EsSkuVo vo = new EsSkuVo();
        vo.setId(sku.getSkuId());
        vo.setBrandId(sku.getBrandId());
        //品牌名

        if (brandEntity != null){
            vo.setBrandName(brandEntity.getName());
        }
        //搜索的标题
        vo.setName(sku.getSkuTitle());
        //sku的图片
        vo .setPic(sku.getSkuDefaultImg());
        //sku的价格
        vo.setPrice(sku.getPrice());
        //sku的分类id
        vo.setProductCategoryId(sku.getCatalogId());


        if (categoryEntity != null){
            //sku分类的名字
            vo.setProductCategoryName(categoryEntity.getName());
        }

        //销量
        vo.setSale(0);

        //排序
        vo.setSort(0);

        //库存(查wms)
        skuStockVos.forEach(item ->{
            if (item.getSkuId() == sku.getSkuId()){
                vo.setStock(item.getStock());
            }
        });



        vo.setAttrValueList(productAttrValueEntities);

        return vo;

    }

}