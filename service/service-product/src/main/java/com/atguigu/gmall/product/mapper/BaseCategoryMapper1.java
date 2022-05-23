package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseCategoryMapper1 extends BaseMapper<BaseCategory1> {
    List<CategoryAndChildsTo> getAllCategoryAndChilds();

    BaseCategoryView getCagetgoryView(@Param("skuId") Long skuId);
}
