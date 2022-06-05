package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.cart.CartItem;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.UserAuthTo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    ProductFeignClient productFeignClient;

    @Override
    public CartItem addSkuToCart(Long skuId, Integer skuNum) {
        //决定使用哪个购物车主键
        String cartkey = determinCartkey();

        CartItem cartItem = saveSkuToCart(skuId, skuNum, cartkey);

        return cartItem;
    }

    //决定购物车使用的键
    @Override
    public String determinCartkey() {
        String prefix = RedisConst.CART_KEY_PREFIX;
        //获取用户信息
        UserAuthTo userAuth = AuthUtil.getUserAuth();
        if (userAuth.getUserId() != null){
            //用户登录
            return prefix+userAuth.getUserId();
        } else  {
            //未登录
            return prefix+userAuth.getUserTempId();
        }
    }

    //保存商品到购物车
    @Override
    public CartItem saveSkuToCart(Long skuId, Integer skuNum, String cartKey) {
        //绑定一个指定购物车的操作
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        Boolean key = cart.hasKey(skuId.toString());
        if (!key){
            //如果没存过就是新增
            //1. 远程调用商品服务查一下这个商品的详细信息
            Result<SkuInfo> skuInfo = productFeignClient.getSkuInfo(skuId);

            //2. 制作一个CartItem
            CartItem cartItem = convertSkuInfoToCartItem(skuInfo.getData());
            cartItem.setSkuNum(skuNum);

            //3.并且转为json,存到redis
            String json = JSONs.toStr(cartItem);
            cart.put(skuId.toString(),json);
            return cartItem;
        }else {
            // 有就是添加数量
            String json = cart.get(skuId.toString());
            CartItem cartItem = JSONs.strToObj(json, new TypeReference<CartItem>() {
            });
            cartItem.setSkuNum(cartItem.getSkuNum()+skuNum);
            //写会redis中
            cart.put(skuId.toString(),JSONs.toStr(cartItem));

            return cartItem;
        }
    }

    //查询购物车列表
    @Override
    public List<CartItem> getCartItems() {
        String cartkey= determinCartkey();

        List<CartItem> cartItems = getItemLists(cartkey);

        return cartItems;
    }

    //获取购物车所有数据
    private List<CartItem> getItemLists(String cartkey) {
        //1.拿到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartkey);
        //2.获取所有的商品
        List<String> values = cart.values();
        //3.List<String>转成List<CartItem>
        List<CartItem> cartItems = values.stream().map((josnStr) -> {
            CartItem cartItem = JSONs.strToObj(josnStr, new TypeReference<CartItem>() {
            });
            return cartItem;
        }).sorted((o1,o2)-> o2.getUpdateTime().compareTo(o1.getUpdateTime()))
        .collect(Collectors.toList());

        return cartItems;
//        List<CartItem> cartItems = new ArrayList<>();
//        for (String json : values) {
//            CartItem cartItem = JSONs.strToObj(json, new TypeReference<CartItem>() {
//            });
//            cartItems.add(cartItem);
//        }
    }

    //保存cartItem数据
    private CartItem convertSkuInfoToCartItem(SkuInfo data) {
        UserAuthTo userAuth = AuthUtil.getUserAuth();

        CartItem cartItem = new CartItem();
        cartItem.setId(data.getId());

        if (userAuth.getUserId()!=null){
            cartItem.setUserId(userAuth.getUserId().toString());
        }else {
            cartItem.setUserId(userAuth.getUserTempId());
        }


        cartItem.setSkuId(data.getId());

        cartItem.setSkuNum(0);

        cartItem.setSkuDefaultImg(data.getSkuDefaultImg());
        cartItem.setSkuName(data.getSkuName());

        cartItem.setIsChecked(1);
        cartItem.setCreateTime(new Date());
        cartItem.setUpdateTime(new Date());

        cartItem.setCartPrice(data.getPrice());
        cartItem.setSkuPrice(data.getPrice());
        return cartItem;
    }
}
