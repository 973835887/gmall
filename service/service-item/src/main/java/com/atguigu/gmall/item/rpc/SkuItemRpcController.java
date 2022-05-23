package com.atguigu.gmall.item.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.item.service.SkuDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/rpc/inner/item")
@RestController
public class SkuItemRpcController {

    @Autowired
    SkuDetailService skuDetailService;


    //查询当前sku所在的分类的完整信息
    // category1Id category1Name
    // category2Id category2Name
    // category3Id category3Name
    @GetMapping("/sku/detail/{skuId}")
    public Result<SkuDetailTo> getSku(@PathVariable Long skuId){

        SkuDetailTo skuDetailTo = skuDetailService.getDetail(skuId);
        return Result.ok(skuDetailTo);
    }
}
