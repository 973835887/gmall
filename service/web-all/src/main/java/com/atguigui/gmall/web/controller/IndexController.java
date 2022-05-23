package com.atguigui.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.to.CategoryAndChildsTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    ProductFeignClient productFeignClient;

    @GetMapping("/")
    public String indexImage(Model model){

        //远程调用其他微服务,查询页面需要的数据
        Result<List<CategoryAndChildsTo>> result = productFeignClient.getAllCategoryAndChilds();
        //如果远程正常
        if(result.isOk()){
            //拿到远程返回的真正数据
            List<CategoryAndChildsTo> data = result.getData();
            model.addAttribute("list",data);
            System.out.println("真是数据 = " + data);
        }

        return "index/index";
    }
}
