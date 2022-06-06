package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/rpc/inner/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    //把商品添加到购物车
    @GetMapping("/add/{skuId}")
    Result<CartItem> addSkuToCart(@PathVariable Long skuId, @RequestParam("skuNum") Integer skuNum);

    //删除选中的商品
    @GetMapping("/delete/Checked")
    Result deleteChecked();

    //获取选中的商品列表
    @GetMapping("/check/list")
    Result<List<CartItem>> getCheckItem();
}
