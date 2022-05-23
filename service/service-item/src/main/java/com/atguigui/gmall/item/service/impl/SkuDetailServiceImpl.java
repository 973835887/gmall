package com.atguigui.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigui.gmall.item.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ProductFeignClient productFeignClient;

    //查询skuId指定的详情数据
    @Override
    public SkuDetailTo getDetail(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();
        //查询分类信息
        Result<BaseCategoryView> cagetgoryView = productFeignClient.getCagetgoryView(skuId);
        if (cagetgoryView.isOk()){
            skuDetailTo.setCategoryView(cagetgoryView.getData());
        }

        //查询sku信息 + 查询sku的图片列表
        Result<SkuInfo> skuInfo = productFeignClient.getSkuInfo(skuId);
        if (skuInfo.isOk()){
            skuDetailTo.setSkuInfo(skuInfo.getData());
        }


        //查询价格price信息
        Result<BigDecimal> skuPrice = productFeignClient.getSkuPrice(skuId);
        if (skuPrice.isOk()){
            skuDetailTo.setPrice(skuPrice.getData());
        }

        //查询所有对应的销售属性集合
        Result<List<SpuSaleAttr>> result = productFeignClient.getSpuSaleAttrAndValueBySkuId(skuId);
        if (result.isOk()){
            skuDetailTo.setSpuSaleAttrList(result.getData());
        }

        //查询sku对应的valuejson数据
        Result<Map<String, String>> valueJson = productFeignClient.getSkuValueJson(skuId);
        if (valueJson.isOk()){
            Map<String, String> data = valueJson.getData();
            System.out.println("data = " + data);
            String str = JSONs.toStr(data);
            skuDetailTo.setValuesSkuJson(str);
        }

        return skuDetailTo;
    }
}
