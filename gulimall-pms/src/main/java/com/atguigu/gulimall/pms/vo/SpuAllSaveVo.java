package com.atguigu.gulimall.pms.vo;

import com.atguigu.gulimall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

@Data
public class SpuAllSaveVo extends SpuInfoEntity {
    //当前spu的所有 基本属性名值对
    private List<BaseAttr> baseAttrs;
    //当前spu对应的所有sku信息
    private  List<SkuVo> skus;
    //spu的详情图
    private String [] spuImages;
}
