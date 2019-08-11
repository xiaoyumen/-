package com.atguigu.gulimall.commons.to;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SkuSaleInfoTo {
    private Long skuId;
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
