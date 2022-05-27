package com.atguigui.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ListController {

    @GetMapping("/list.html")
    public String searchPage(@RequestParam Long category3Id){

        return "list/index";
    }
}
