package com.atguigu.gulimall.pms.service.impl;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.commons.to.SkuStockVo;
import com.atguigu.gulimall.commons.to.es.EsSkuAttributeValue;
import com.atguigu.gulimall.commons.to.es.EsSkuVo;
import com.atguigu.gulimall.pms.dao.*;
import com.atguigu.gulimall.pms.entity.*;
import com.atguigu.gulimall.pms.feign.EsFeignService;
import com.atguigu.gulimall.pms.feign.WmsFeignService;
import com.atguigu.gulimall.pms.vo.SkuVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.service.SpuInfoService;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    EsFeignService esFeignService;
    @Autowired
    SpuInfoDao spuInfoDao;
    @Autowired
    SkuInfoDao skuInfoDao;

    @Autowired
    BrandDao brandDao;
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    ProductAttrValueDao attrValueDao;
    @Autowired
    AttrDao attrDao;
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
        //1.封装查询条件
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        //.写crud，首先要想想sql语句怎么写
        if (catId != 0) {
            queryWrapper.eq("catalog_id", catId);
            if (!StringUtils.isEmpty(queryCondition.getKey())) {
                queryWrapper.and(obj -> {
                    obj.like("spu_name", queryCondition.getKey());
                    obj.or().like("id", queryCondition.getKey());
                    return obj;
                });
            }
        }
        //2.封装翻页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(queryCondition);
        IPage<SpuInfoEntity> data = this.page(page, queryWrapper);
        PageVo vo = new PageVo(data);
        return vo;

    }

    /**
     * 商品上下架
     *
     * @param spuId
     * @param status
     */
    @Override
    public void updateSpuStates(Long spuId, Integer status) {

        if (status == 1) {
            spuUp(spuId, status);

        } else {
            spuDown(spuId, status);
        }


    }

    private void spuUp(Long spuId, Integer status) {
        //1.查出我们接下来使用的基本信息
        SpuInfoEntity spuInfoEntity = spuInfoDao.selectById(spuId);
        BrandEntity brandEntity = brandDao.selectById(spuInfoEntity.getBrandId());
        CategoryEntity categoryEntity = categoryDao.selectById(spuInfoEntity.getCatalogId());

        //2.上架：将商品需要检索的信息放在es中，下架：将商品需要检索的信息从es中删除

        List<EsSkuVo> skuVo = new ArrayList<>();
        //1).查出当前需要上架的spu的所有sku信息

        List<SkuInfoEntity> skus = skuInfoDao.selectList(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        //1.1查出这个spu对应的sku的所有库存信息
       List<Long> skuIds = new ArrayList<>();
        skus.forEach(skuInfoEntity -> {
            skuIds.add(skuInfoEntity.getSkuId());
        });
        //1.2远程检索到所有sku的库存信息
        Resp<List<SkuStockVo>> infos = wmsFeignService.skuWareInfos(skuIds);
        List<SkuStockVo> skuStockVos = infos.getData();
        //1.3查出当前spu所有可以供检索的属性

        List<ProductAttrValueEntity> spu_id = attrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //1.4过滤出可以被检索的
        ArrayList<Object> attrIds = new ArrayList<>();
        spu_id.forEach(item->{
            attrIds.add(item.getAttrId());
        });
        List<AttrEntity> list = attrDao.selectList(new QueryWrapper<AttrEntity>().in("attr_id", attrIds).eq("search_type", 1));
        //1.5 在spu_id 过滤出list的所有数据
//       List<ProductAttrValueEntity> productAttrValueEntities = new ArrayList<>();
        List<EsSkuAttributeValue> esSkuAttributeValues = new ArrayList<>();
        list.forEach(item->{
            //当前能被检索的属性
            Long attrId = item.getAttrId();
            //拿到真正的值
            spu_id.forEach(s->{
                if (item.getAttrId()==s.getAttrId()){

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
        //1.6 将productAttrValueEntities变成正在在es中存储的vo对象
        if (skus!=null&&skus.size()>0){
            //2.构造所有需要保存在es中的sku信息
           skus.forEach(skuInfoEntity -> {
               EsSkuVo skuvo = skuInfoToEsSkuVo(skuInfoEntity,spuInfoEntity,brandEntity,categoryEntity,skuStockVos,esSkuAttributeValues);
               skuVo.add(skuvo);
           });
        }
        //3.远程调用search服务，将商品上架
        Resp<Object> resp = esFeignService.spuUp(skuVo);
        if (resp.getCode() == 0) {
            //远程调用成功
            //本地修改数据库
            SpuInfoEntity entity = new SpuInfoEntity();
            entity.setId(spuId);
            entity.setPublishStatus(1);
            entity.setUodateTime(new Date());
            //按照id更新其他设置了的字段
            spuInfoDao.updateById(entity);
        }

    }

    private void spuDown(Long spuId, Integer status) {

    }
    private EsSkuVo skuInfoToEsSkuVo(SkuInfoEntity skuInfoEntity, SpuInfoEntity spuInfoEntity, BrandEntity brandEntity, CategoryEntity categoryEntity, List<SkuStockVo> skuStockVos, List<EsSkuAttributeValue> productAttrValueEntities){
        EsSkuVo vo = new EsSkuVo();
        vo.setId(skuInfoEntity.getSkuId());
        vo.setBrandId(skuInfoEntity.getBrandId());

        if (brandEntity!=null){
            vo.setBrandName(brandEntity.getName());
        }
        //搜索的标题
        vo.setName(skuInfoEntity.getSkuTitle());
        //sku的图片
        vo.setPic(skuInfoEntity.getSkuDefaultImg());
        //sku的价格
        vo.setPrice(skuInfoEntity.getPrice());
        //所属分类的id
        vo.setProductCategoryId(skuInfoEntity.getCatalogId());
            //所属分类的名字
        if (categoryEntity!=null){
            //所属分类的名字
            vo.setProductCategoryName(categoryEntity.getName());

        }
        vo.setSale(0);
        vo.setSort(0);
        //wms
        skuStockVos.forEach(item->{
            if (item.getSkuId()== skuInfoEntity.getSkuId()){
                vo.setStock(item.getStock());

            }
        });
        vo.setAttrValueList(productAttrValueEntities);

        return vo;
    }
}