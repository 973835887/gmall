package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.cron.SkuIdBloomTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/product")
public class BloomController {

    @Autowired
    SkuIdBloomTask skuIdBloomTask;

    @GetMapping("/rebuild")
    public Result rebuildBloom(){
        try {
            skuIdBloomTask.rebuildBloom();
            System.out.println("布隆重建完成  " );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("布隆重建失败 " + e);
        }
        return Result.ok();
    }
}
