package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartItem;

import java.math.BigDecimal;
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

    //修改购物车数量
    void updateCartItemNum(Long skuId, Integer skuNum);

    //修改购物车的选择数量
    void updateCartItemStatus(Long skuId, Integer status);

    //删除购物车中指定的商品
    void deleteCartItem(Long skuId);

    //删除购物车中选中的商品
    void deleteChecked();

    //删除购物车
    void deleteCart(String cartKey);

    //设置超时时间
    void setCartTimeout(String cartKey);

    //判断这个购物车是否溢出
    void validateCartOverflow(String cartkey);

    //更新指定购物车中某个商品的价格
    void updateCartItemPrice(String cartKey, Long skuId, BigDecimal price);

    //异步提交给线程池;批量更新购物车中的商品价格
    void updatePriceBatch(String cartkey);

}
