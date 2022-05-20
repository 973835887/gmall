package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
public class SpuInfoController {

    @Autowired
    SpuInfoService spuInfoService;


    //查询spuinfo分页列表信息
    @GetMapping("/{pageNum}/{pageSize}")
    public Result getSpuInfoPage(@PathVariable Long pageNum,
                             @PathVariable Long pageSize,
                             @RequestParam Long category3Id){
        Page<SpuInfo> result = spuInfoService.getPageInfo(pageNum,pageSize,category3Id);
        return Result.ok(result);
    }


    //添加spu属性信息
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.savespuInfo(spuInfo);
        return Result.ok();
    }


}
