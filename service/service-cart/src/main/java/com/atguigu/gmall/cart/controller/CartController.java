package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
