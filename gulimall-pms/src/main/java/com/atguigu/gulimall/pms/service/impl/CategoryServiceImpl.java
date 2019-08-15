package com.atguigu.gulimall.pms.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.pms.vo.CategoryWithChildrensVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gulimall.commons.bean.PageVo;
import com.atguigu.gulimall.commons.bean.Query;
import com.atguigu.gulimall.commons.bean.QueryCondition;

import com.atguigu.gulimall.pms.dao.CategoryDao;
import com.atguigu.gulimall.pms.entity.CategoryEntity;
import com.atguigu.gulimall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public List<CategoryEntity> getCategoryByLevel(Integer level) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        if (level!=0){
            queryWrapper.eq("cat_level",level);
        }

        List<CategoryEntity> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<CategoryEntity> getCategoryChildrensById(Integer catId) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        if (catId!=0){
            queryWrapper.eq("parent_cid",catId);
        }

        List<CategoryEntity> list = baseMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public List<CategoryWithChildrensVo> getCategoryChildrensAndSubById(Integer id) {

        List<CategoryWithChildrensVo> vos=null;

        String s = redisTemplate.opsForValue().get(Constant.CACHE_CATELOG);
        if (!StringUtils.isEmpty(s)){
         vos= JSON.parseArray(s,CategoryWithChildrensVo.class);
        }else {
            //1.缓存中没有，查数据库
            vos =categoryDao.selectCategoryChildrenWithChildrens(id);

        }
        return vos;
    }

}