package com.atguigu.gulimall.pms.vo;

import lombok.Data;

@Data
public class BaseAttr {
    private Long attrId;
    private String attrName;
    private String [] valueSelected;
}
