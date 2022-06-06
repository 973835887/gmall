package com.atguigu.gmall.feign.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "ware-manage",url = "${app.props.ware-url}")
public interface WareFeignClient {

    //检查商品是否有库存
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer skuNum);
}
