package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    CartService cartService;

    //查询购物车列表数据
    @GetMapping("cartList")
    public Result getCartList(){

       List<CartItem> cartItems = cartService.getCartItems();

        return Result.ok(cartItems);
    }

    //添加或者删减购物车数量
    @PostMapping("addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,@PathVariable("skuNum") Integer skuNum){
        cartService.updateCartItemNum(skuId,skuNum);
        return Result.ok();
    }

    //更改购物车中商品的选中数量
//    /checkCart/41/0
    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable Long skuId,@PathVariable("status") Integer status){
        cartService.updateCartItemStatus(skuId,status);
        return Result.ok();
    }

    //删除购物车中指定的商品
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable Long skuId){
        cartService.deleteCartItem(skuId);
        return Result.ok();
    }

}
