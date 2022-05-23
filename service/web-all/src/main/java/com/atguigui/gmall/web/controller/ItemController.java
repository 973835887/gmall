package com.atguigui.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("/{skuId}.html")
    public String itemPage(@PathVariable Long skuId, Model model){
        //查询当前sku所在的分类的完整信息
        // category1Id category1Name
        // category2Id category2Name
        // category3Id category3Name
        Result<SkuDetailTo> skuDetail = itemFeignClient.getSku(skuId);
        if (skuDetail.isOk()){
            SkuDetailTo skuDetailData = skuDetail.getData();
            model.addAttribute("categoryView",skuDetailData.getCategoryView());

            //查询当前sku信息  skuid skuName skuDefualtImg skuImageList
            model.addAttribute("skuInfo",skuDetailData.getSkuInfo());

            //查询当前sku的价格 price
            model.addAttribute("price",skuDetailData.getPrice());

            //spuSaleAttrList 查询sku对应的销售属性集合
            model.addAttribute("spuSaleAttrList",skuDetailData.getSpuSaleAttrList());
            //valuesSkuJson  当前spu可用的销售属性值组合
            model.addAttribute("valuesSkuJson",skuDetailData.getValuesSkuJson());
        }
        return "item/index";
    }
}
