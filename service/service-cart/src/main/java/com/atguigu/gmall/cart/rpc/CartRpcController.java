package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/rpc/inner/cart")
public class CartRpcController {

    @Autowired
    CartService cartService;


    @GetMapping("/add/{skuId}")
    public Result<CartItem> addSkuToCart(@PathVariable Long skuId,
                                         @RequestParam("skuNum") Integer skuNum){

        CartItem cartItem = cartService.addSkuToCart(skuId,skuNum);

        return Result.ok(cartItem);
    }
}
