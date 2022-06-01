package com.atguigui.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartPage(){

        return "cart/index";
    }
}
