package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartItem;

import java.util.List;

public interface CartService {
    //添加商品到购物车
    CartItem addSkuToCart(Long skuId, Integer skuNum);

    //决定使用购物车哪个键
    String determinCartkey();

    //保存一个商品到购物车
    CartItem saveSkuToCart(Long skuId, Integer skuNum,String cartKey);

    //查询购物车中所有商品
    List<CartItem> getCartItems();

}
