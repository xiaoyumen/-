package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.to.SkuSaleInfoTo;
import com.atguigu.gulimall.commons.utils.AppUtils;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.feign.WorldServer;
import com.atguigu.gulimall.pms.vo.BaseAttr;
import com.atguigu.gulimall.pms.vo.SaleAttrVo;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SkuInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    SpuInfoDescDao spuInfoDescDao;
    @Autowired
    ProductAttrValueDao attrValueDao;
    @Autowired
    SkuInfoDao skuInfoDao;
    @Autowired
    SkuImagesDao skuImagesDao;
    @Autowired
    AttrDao attrDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Autowired
    WorldServer feignserver;
    @Autowired
    SpuImagesDao  spuImagesDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageVo(page);
    }

 @GlobalTransactional
    //@Transactional
    @Override
    public void spuBigSaveAll(SpuAllSaveVo spuInfo) {
        SkuInfoService proxy = (SkuInfoService) AopContext.currentProxy();
        //1.保存spu的基本信息
        //1.1存spu的基本信息
        Long spuId =proxy.saveSpuBaseInfo(spuInfo);
        //1.2保存spu的所有图片信息
        proxy.saveSpuInfoImages(spuId,spuInfo.getSpuImages());
        //2.保存spu的基本属性信息
        List<BaseAttr> baseAttrs = spuInfo.getBaseAttrs();
        proxy.saveSpuBaseAttrs(spuId,baseAttrs);
        //3.保存sku以及sku的营销相关信息
        proxy.saveSkuInfos(spuId,spuInfo.getSkus());
        int i=10/0;
    }

    //负责解析出数据做出相应的业务
    @Transactional
    @Override
    public Long saveSpuBaseInfo(SpuAllSaveVo spuInfo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuInfo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(new Date());
        spuInfoDao.insert(spuInfoEntity);
        return spuInfoEntity.getId();
    }

    @Transactional
    @Override
    public void saveSpuInfoImages(Long spuId, String[] images) {
        StringBuffer urls = new StringBuffer();
        if (images!=null) {
            for (String image : images) {
                urls.append(image);
                urls.append(",");
            }
        }
        SpuInfoDescEntity entity = new SpuInfoDescEntity();
        entity.setSpuId(spuId);
         entity.setDecript(urls.toString());
        spuInfoDescDao.insertInfo(entity);


    }

    @Transactional
    @Override
    public void saveSpuBaseAttrs(Long spuId, List<BaseAttr> baseAttrs) {

        ArrayList<ProductAttrValueEntity> allsave = new ArrayList<>();
        if (baseAttrs!=null&&baseAttrs.size()>0){
            baseAttrs.forEach(baseAttr -> {
                ProductAttrValueEntity entity = new ProductAttrValueEntity();
                entity.setAttrId(baseAttr.getAttrId());
                entity.setAttrName(baseAttr.getAttrName());
                String[] valuSelected = baseAttr.getValueSelected();
                if (valuSelected!=null&&valuSelected.length>0) {
                    entity.setAttrValue(AppUtils.arrayToStringWithSeperator(valuSelected, ","));
                }
                entity.setAttrSort(0);
                entity.setQuickShow(1);
                entity.setSpuId(spuId);
                allsave.add(entity);
            });
            attrValueDao.insertBatch(allsave);
        }

    }

    @Transactional
    @Override
    public void saveSkuInfos(Long spuId, List<SkuVo> skus) {
        //1.保存sku的info信息
        SpuInfoEntity spuInfo = spuInfoDao.selectById(spuId);
        ArrayList<SkuSaleInfoTo> tos = new ArrayList<>();

        for (SkuVo skuVo : skus) {
            String[] images = skuVo.getImages();
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            skuInfoEntity.setBrandId(spuInfo.getBrandId());
            skuInfoEntity.setCatalogId(skuInfoEntity.getCatalogId());
            skuInfoEntity.setPrice(skuVo.getPrice());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString().substring(0,5).toLowerCase());
            if (images!=null&&images.length>0) {
                skuInfoEntity.setSkuDefaultImg(skuVo.getImages()[0]);
            }
            skuInfoEntity.setSkuDesc(skuVo.getSkuDesc());
            skuInfoEntity.setSkuName(skuVo.getSkuName());
            skuInfoEntity.setSkuSubtitle(skuVo.getSkuSubtitle());
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setWeight(skuVo.getWeight());
            skuInfoEntity.setSkuTitle(skuVo.getSkuTitle());
            //保存sku的基本信息
            skuInfoDao.insert(skuInfoEntity);
            //2.保存sku所有对应图片
            Long skuId = skuInfoEntity.getSkuId();
            for (int i = 0; i < images.length; i++)  {
                SkuImagesEntity imagesEntity = new SkuImagesEntity();
                imagesEntity.setSkuId(skuId);
                imagesEntity.setDefaultImg(i==0?1:0);
                imagesEntity.setImgUrl(images[i]);
                imagesEntity.setImgSort(0);
                skuImagesDao.insert(imagesEntity);
            }
            //3.保存sku所有的销售属性保存起来
            List<SaleAttrVo> saleAttrs = skuVo.getSaleAttrs();
            saleAttrs.forEach(saleAttrVo -> {
                //查询当前属性的信息
                SkuSaleAttrValueEntity saleEntity = new SkuSaleAttrValueEntity();
                saleEntity.setAttrId(saleAttrVo.getAttrId());
                //查出这个属性的真正信息
                AttrEntity attrEntity = attrDao.selectById(saleAttrVo.getAttrId());
                saleEntity.setAttrName(attrEntity.getAttrName());
                saleEntity.setAttrSort(0);
                saleEntity.setAttrValue(saleAttrVo.getAttrValue());
                saleEntity.setSkuId(skuId);
                //sku与销售属性的关联关系
                skuSaleAttrValueDao.insert(saleEntity);
            });
            //以上都是pms系统完成的工作

            //以下需要由sms完成，保存每一个sku的相关优惠数据
            SkuSaleInfoTo info = new SkuSaleInfoTo();
            BeanUtils.copyProperties(skuVo,info);
            info.setSkuId(skuId);
            //发给sms,让他去处理

            tos.add(info);
        }
        //发给sms,让他去处理
        log.info("pms准备发出数据。。。{}",tos);
        feignserver.saveSkuSaleInfos(tos);
        log.info("pms给sms发出数据完成。。。。");

        }
    }

