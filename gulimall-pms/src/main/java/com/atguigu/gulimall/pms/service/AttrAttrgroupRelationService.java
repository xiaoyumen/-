package com.atguigu.gulimall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import org.aspectj.lang.annotation.AdviceName;


/**
 * 属性&属性分组关联
 *
 * @author xieweiquan
 * @email xx@atguigu.com
 * @date 2019-08-01 21:12:37
 */

public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageVo queryPage(QueryCondition params);
}

