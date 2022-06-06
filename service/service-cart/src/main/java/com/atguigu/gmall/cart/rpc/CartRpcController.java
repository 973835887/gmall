package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartItem;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/rpc/inner/cart")
public class CartRpcController {

    @Autowired
    CartService cartService;

    //添加商品到购物车
    @GetMapping("/add/{skuId}")
    public Result<CartItem> addSkuToCart(@PathVariable Long skuId,
                                         @RequestParam("skuNum") Integer skuNum){

        CartItem cartItem = cartService.addSkuToCart(skuId,skuNum);

        return Result.ok(cartItem);
    }

    //删除选中的商品
    @GetMapping("/delete/Checked")
    public Result deleteChecked(){
        cartService.deleteChecked();
        return Result.ok();
    }

    //获取选中的商品列表
    @GetMapping("/check/list")
    public  Result<List<CartItem>> getCheckItem(){
       List<CartItem> items = cartService.getCheckItem();
        return Result.ok(items);
    }
}
