package com.atguigu.gmall.product.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;

import java.util.List;

public interface BaseCategoryService {
    List<BaseCategory1> getAllCategory1();

    List<BaseCategory2> getCategory2ByCategory1Id(Long category1Id);

    List<BaseCategory3> getCategory3ByCategory1Id(Long category2Id);

    List<CategoryAndChildsTo> getAllCategoryAndChilds();

    BaseCategoryView getCagetgoryView(Long skuId);
}
