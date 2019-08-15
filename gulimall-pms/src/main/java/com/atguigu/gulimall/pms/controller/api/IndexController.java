package com.atguigu.gulimall.pms.controller.api;

import com.atguigu.gulimall.commons.bean.Resp;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.service.CategoryService;
import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/cates")
    public Resp<Object> levelGatelogs() {
        List<CategoryEntity> categoryByLevel = categoryService.getCategoryByLevel(1);
        return Resp.ok(categoryByLevel);
    }

    @GetMapping("/cates/{id}")
    public Resp<Object> levelGatelogs(@PathVariable("id") Integer id) {
        List<CategoryWithChildrensVo> childrens = categoryService.getCategoryChildrensAndSubById(id);
        return Resp.ok(childrens);
    }
}
