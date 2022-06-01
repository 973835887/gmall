package com.atguigui.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
     /*
     *
     * @param originUrl  登录之后的跳转页面
     * @return
     */
    @GetMapping("/login.html")
    public String loginUrl(@RequestParam("originUrl") String originUrl, Model model){
        model.addAttribute("originUrl",originUrl);
        return "login";
    }
}
