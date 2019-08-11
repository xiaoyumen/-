package com.atguigu.gulimall.pms.service;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import com.atguigu.gulimall.pms.vo.BaseAttr;
import com.atguigu.gulimall.pms.vo.SkuVo;
import com.atguigu.gulimall.pms.vo.SpuAllSaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.SkuInfoEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import java.util.List;


/**
 * sku信息
 *
 * @author xieweiquan
 * @email xx@atguigu.com
 * @date 2019-08-01 21:12:36
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageVo queryPage(QueryCondition params);

    void spuBigSaveAll(SpuAllSaveVo skuInfo);
  Long  saveSpuBaseInfo( SpuAllSaveVo spuInfoEntity);

    void saveSpuInfoImages(Long spuId, String[] images);

    void saveSpuBaseAttrs(Long spuId, List<BaseAttr> baseAttrs);

    void saveSkuInfos(Long spuId, List<SkuVo> skus);
}

