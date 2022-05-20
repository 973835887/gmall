package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product")
public class SkuInfoController {

    @Autowired
    SkuInfoService skuInfoService;

    //保存库存单元
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo){
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    //分页查询skuinfo列表信息
    @GetMapping("/list/{page}/{limit}")
    public Result getSkuInfoList(@PathVariable("page") Long page,
                                 @PathVariable("limit") Long limit){

        Page<SkuInfo> skuInfoPage = new Page<>(page,limit);
        Page<SkuInfo> infoPage = skuInfoService.page(skuInfoPage);
        return Result.ok(infoPage);
    }

    //上架
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.updateStatus(skuId,1);
        return Result.ok();
    }

    //下架
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.updateStatus(skuId,0);
        return Result.ok();
    }

}
