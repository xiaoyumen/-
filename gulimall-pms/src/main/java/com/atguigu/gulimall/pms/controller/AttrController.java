package com.atguigu.gulimall.pms.controller;

import java.util.Arrays;
import java.util.Map;


import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.QueryCondition;
import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.entity.AttrGroupEntity;
import com.atguigu.gulimall.pms.service.AttrGroupService;
import com.atguigu.gulimall.pms.vo.AttrSaveVo;
import com.atguigu.gulimall.pms.vo.AttrwithGroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gulimall.pms.entity.AttrEntity;
import com.atguigu.gulimall.pms.service.AttrService;


/**
 * 商品属性
 *
 * @author xieweiquan
 * @email xx@atguigu.com
 * @date 2019-08-01 21:12:37
 */
@Api(tags = "商品属性 管理")
@RestController
@RequestMapping("pms/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Autowired
    private AttrGroupService attrGroupService;

    // /pms/attr/group/list/{groupId}
    @ApiOperation("查询某个分组下对应的所有属性")
    @GetMapping("/group/list/{groupId}")
    public Resp<PageVo> getgrouplistAttrs(
            @PathVariable("groupId") Long groupId,
            QueryCondition queryCondition) {
        return null;
    }

    ///pms/attr/base/{catId}
    @ApiOperation("查询所有基本属性")
    @GetMapping("/base/{catId}")
    public Resp<PageVo> getCateloogBaseAttrs(
            @PathVariable("catId") Long catId,
            QueryCondition queryCondition) {
        PageVo pageVo = attrService.queryPageCatelogBaseAttrs(queryCondition, catId, 1);
        return Resp.ok(pageVo);
    }

    @ApiOperation("查询所有销售属性")
    @GetMapping("/sale/{catId}")
    public Resp<PageVo> getCateloogSalesAttrs(
            @PathVariable("catId") Long catId,
            QueryCondition queryCondition) {
        PageVo pageVo = attrService.queryPageCatelogBaseAttrs(queryCondition, catId, 0);
        return Resp.ok(pageVo);
    }

    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attr:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrId}")
    @PreAuthorize("hasAuthority('pms:attr:info')")
    public Resp<AttrwithGroupVo> info(@PathVariable("attrId") Long attrId) {
        AttrwithGroupVo attrwithGroupVo = new AttrwithGroupVo();
        //1.查出属性信息

        AttrEntity attr = attrService.getById(attrId);
        BeanUtils.copyProperties(attr,attrwithGroupVo);
        //2.查出这个属性所在的分组信息

       AttrGroupEntity attrGroup = attrGroupService.getGroupInfoByAttrId(attrId);
        attrwithGroupVo.setGroup(attrGroup);

        return Resp.ok(attrwithGroupVo);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attr:save')")
    public Resp<Object> save(@RequestBody AttrSaveVo attr) {
        attrService.saveAttrRelation (attr);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attr:update')")
    public Resp<Object> update(@RequestBody AttrEntity attr) {
        attrService.updateById(attr);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attr:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return Resp.ok(null);
    }

}
