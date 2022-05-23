package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rpc/inner/product")
public class SkuRpcController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    //查询skuInfo信息
    @GetMapping("/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable Long skuId){
        //查询skuinfo信息
        SkuInfo skuInfo = skuInfoService.getById(skuId);
        List<SkuImage> skuImages = skuImageService.list(new QueryWrapper<SkuImage>().eq("sku_id", skuId));
        skuInfo.setSkuImageList(skuImages);
        return Result.ok(skuInfo);
    }

    //查询价格信息
    @GetMapping("/skuInfo/price/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable Long skuId){
        BigDecimal skuInfoPrice = skuInfoService.getSkuPrice(skuId);
        return Result.ok(skuInfoPrice);
    }

    //查询指定sku对应的spu对应的销售属性名和值
    @GetMapping("/skuInfo/spuSaleAttrAndValues/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrAndValueBySkuId(@PathVariable Long skuId){
        List<SpuSaleAttr> spuSaleAttrs = skuInfoService.getSpuSaleAttrAndValueBySkuId(skuId);
        return Result.ok(spuSaleAttrs);
    }


    //查询sku的valuejson数据
    @GetMapping("sku/valueJson/{skuId}")
    public Result<Map<String,String>> getSkuValueJson(@PathVariable Long skuId){
        Map<String,String> map = skuSaleAttrValueService.getSkuValueJson(skuId);
        return Result.ok(map);
    }

}
