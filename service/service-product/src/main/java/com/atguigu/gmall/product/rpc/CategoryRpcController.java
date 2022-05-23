package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rpc/inner/product")
public class CategoryRpcController {

    @Autowired
    BaseCategoryService baseCategoryService;

    //获取所有的分类以及子分类信息
    @GetMapping("/categorys")
    public Result<List<CategoryAndChildsTo>> getAllCategoryAndChilds(){
       List<CategoryAndChildsTo> categoryAndChildsToList = baseCategoryService.getAllCategoryAndChilds();
        System.out.println("返回的数据 = " + categoryAndChildsToList);
        return Result.ok(categoryAndChildsToList);
    }


    //获取一个sku的分类层级信息
    @GetMapping("/category/view/{skuId}")
    public Result<BaseCategoryView> getCagetgoryView(@PathVariable("skuId") Long skuId){
        BaseCategoryView result = baseCategoryService.getCagetgoryView(skuId);
        return Result.ok(result);
    }
}
