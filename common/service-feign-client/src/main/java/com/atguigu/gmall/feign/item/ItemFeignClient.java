package com.atguigu.gmall.feign.item;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-item")
@RequestMapping("/rpc/inner/item")
public interface ItemFeignClient {
    //查询当前sku所在的分类的完整信息
    // category1Id category1Name
    // category2Id category2Name
    // category3Id category3Name
    @GetMapping("/sku/detail/{skuId}")
    public Result<SkuDetailTo> getSku(@PathVariable Long skuId);
}
