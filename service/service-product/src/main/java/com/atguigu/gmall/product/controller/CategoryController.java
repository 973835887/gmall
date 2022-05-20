package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/admin/product")
@RestController
public class CategoryController {

    @Autowired
    BaseCategoryService baseCategoryService;
    //查询所有的一级分类信息
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategoryService.getAllCategory1();
        return Result.ok(category1s);
    }

    //根据一级分类ID查询二级分类的所有信息
    //admin/product/getCategory2/4
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2ByCategory1Id(@PathVariable("category1Id") Long category1Id){
        List<BaseCategory2> category2s = baseCategoryService.getCategory2ByCategory1Id(category1Id);
        return Result.ok(category2s);
    }

    //根据一级分类ID跟二级分类ID查询三级分类的信息
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3ByCategory2Id(@PathVariable("category2Id") Long category2Id){
        List<BaseCategory3> category3s = baseCategoryService.getCategory3ByCategory1Id(category2Id);
        return Result.ok(category3s);
    }
}
