package com.atguigui.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.cart.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

    //购物车列表页
    @GetMapping("/cart.html")
    public String cartPage(){

        return "cart/index";
    }

    //添加一个商品到购物车
    @GetMapping("/addCart.html")
    public String addCartPage(@RequestParam("skuId") Long skuId,
                              @RequestParam("skuNum") Integer skuNum,
                              Model model,
                              HttpServletRequest request){

        String userId = request.getHeader("UserId");
        String userTempId = request.getHeader("UserTempId");

        //TODO 远程调用
        Result<CartItem> cartItem = cartFeignClient.addSkuToCart(skuId, skuNum);

        if (cartItem.isOk());{

            CartItem item = cartItem.getData();

            model.addAttribute("skuInfo",item);
            model.addAttribute("skuNum",item.getSkuNum());
        }

        return "cart/addCart";
    }
}
