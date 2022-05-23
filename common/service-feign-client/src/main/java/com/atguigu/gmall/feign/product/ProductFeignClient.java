package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient("service-product")
@RequestMapping("/rpc/inner/product")
public interface ProductFeignClient {
    //获取所有的分类以及子分类信息  /rpc/inner/product/categorys
    @GetMapping("/categorys")
    //weball直接访问
    @ResponseBody
    public Result<List<CategoryAndChildsTo>> getAllCategoryAndChilds();

    //获取一个sku的分类层级信息
    @GetMapping("/category/view/{skuId}")
    public Result<BaseCategoryView> getCagetgoryView(@PathVariable("skuId") Long skuId);

    //获取skuinfo信息
    @GetMapping("/getSkuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable Long skuId);

    //查询价格信息
    @GetMapping("/skuInfo/price/{skuId}")
    public Result<BigDecimal>  getSkuPrice(@PathVariable Long skuId);

    //查询指定sku对应的spu对应的销售属性名和值
    @GetMapping("/skuInfo/spuSaleAttrAndValues/{skuId}")
    public Result<List<SpuSaleAttr>> getSpuSaleAttrAndValueBySkuId(@PathVariable Long skuId);

    //查询sku的valuejson数据
    @GetMapping("sku/valueJson/{skuId}")
    public Result<Map<String,String>> getSkuValueJson(@PathVariable Long skuId);
}
