package com.atguigu.gulimall.pms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.pms.entity.SpuCommentEntity;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;


/**
 * 商品评价
 *
 * @author xieweiquan
 * @email xx@atguigu.com
 * @date 2019-08-01 21:12:36
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageVo queryPage(QueryCondition params);
}
