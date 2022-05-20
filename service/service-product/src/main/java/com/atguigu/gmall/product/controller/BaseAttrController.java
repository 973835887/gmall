package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {
    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    //根据分类信息查询所有的平台属性
    @GetMapping("/attrInfoList/{categoryId1}/{categoryId2}/{categoryId3}")
    public Result getAttrInfoList(@PathVariable("categoryId1") Long categoryId1,
                                  @PathVariable("categoryId2") Long categoryId2,
                                  @PathVariable("categoryId3") Long categoryId3){
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoService.getAttrInfoList(categoryId1,categoryId2,categoryId3);
        return Result.ok(baseAttrInfoList);
    }

    //添加/修改属性名和值
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveOrUpdateAttrAndValue(baseAttrInfo);
        return Result.ok();
    }

    //根据属性名的ID查询属性值信息
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable Long attrId){
        List<BaseAttrValue> baseAttrValues =  baseAttrInfoService.getAttrValueList(attrId);
        return Result.ok(baseAttrValues);
    }
}
