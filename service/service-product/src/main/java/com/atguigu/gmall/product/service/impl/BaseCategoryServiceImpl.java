package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;
import com.atguigu.gmall.product.mapper.BaseCategoryMapper1;
import com.atguigu.gmall.product.mapper.BaseCategoryMapper2;
import com.atguigu.gmall.product.mapper.BaseCategoryMapper3;
import com.atguigu.gmall.product.service.BaseCategoryService;
import com.atguigu.gmall.cache.service.CacheService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseCategoryServiceImpl implements BaseCategoryService {
    @Autowired
    BaseCategoryMapper1 baseCategoryMapper1;
    @Autowired
    BaseCategoryMapper2 baseCategoryMapper2;
    @Autowired
    BaseCategoryMapper3 baseCategoryMapper3;
    @Autowired
    CacheService cacheService;

    @Override
    public List<BaseCategory1> getAllCategory1() {

        return baseCategoryMapper1.selectList(null);
    }

    @Override
    public List<BaseCategory2> getCategory2ByCategory1Id(Long category1Id) {

        QueryWrapper<BaseCategory2> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category1_id",category1Id);
        List<BaseCategory2> category2s = baseCategoryMapper2.selectList(queryWrapper);
        return category2s;
    }

    @Override
    public List<BaseCategory3> getCategory3ByCategory1Id(Long category2Id) {
        QueryWrapper<BaseCategory3> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category2_id",category2Id);
        List<BaseCategory3> baseCategory3s = baseCategoryMapper3.selectList(queryWrapper);
        return baseCategory3s;
    }


    //获取所有的分类数据以及子分类
    @Override
    public List<CategoryAndChildsTo> getAllCategoryAndChilds() {
        //1,查询缓存
        Object cacheData = cacheService.getCacheData(RedisConst.CATEGORY_KEY, new TypeReference<List<CategoryAndChildsTo>>() {
        });

        if (cacheData == null){
            //2,缓存没有,查询数据库
            List<CategoryAndChildsTo> categoryAndChildsToList = baseCategoryMapper1.getAllCategoryAndChilds();
            //3.放入缓存
            cacheService.save(RedisConst.CATEGORY_KEY,categoryAndChildsToList);
            return categoryAndChildsToList;
        }

        //4.返回缓存数据
        return (List<CategoryAndChildsTo>) cacheData;
    }

    //获取一个skuId的层级信息
    @Override
    public BaseCategoryView getCagetgoryView(Long skuId) {
        BaseCategoryView result = baseCategoryMapper1.getCagetgoryView(skuId);
        return result;
    }
}
