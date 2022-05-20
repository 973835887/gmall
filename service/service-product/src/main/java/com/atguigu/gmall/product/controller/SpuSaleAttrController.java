package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuSaleAttrController {
    @Autowired
    SpuSaleAttrService spuSaleAttrService;


    //根据spuId回属性信息
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable Long spuId){
       List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrService.getSpuSaleAttrList(spuId);
       return Result.ok(spuSaleAttrs);
    }
}
