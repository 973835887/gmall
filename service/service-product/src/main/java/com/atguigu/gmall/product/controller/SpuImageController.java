package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.product.service.SpuImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("//admin/product")
public class SpuImageController {

    @Autowired
    SpuImageService spuImageService;

    //回显图片信息
    @GetMapping("/spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable Long spuId){
       List<SpuImage> spuImageList = spuImageService.getSpuImageList(spuId);
       return Result.ok(spuImageList);
    }
}
