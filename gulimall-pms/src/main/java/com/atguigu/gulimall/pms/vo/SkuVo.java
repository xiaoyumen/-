package com.atguigu.gulimall.pms.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuVo {
    private String skuName;
    private String skuDesc;
    private String skuTitle;

    private String skuSubtitle;
    private BigDecimal weight;
    private BigDecimal price;
    private String[] images;
    //以上sku基本信息
    //当前sku对应的销售属性组合
    private List<SaleAttrVo> saleAttrs;

    private BigDecimal growsBounds;
    private BigDecimal buyBounds;
    private Integer[] work;
    //上面是积分设置的信息
    private Integer fullCount;
    private BigDecimal discount;
    private Integer ladderAddOther;
    //上面是 阶梯价格的信息

    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer fullAddOther;

}
