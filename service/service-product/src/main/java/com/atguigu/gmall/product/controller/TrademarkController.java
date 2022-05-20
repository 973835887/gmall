package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/product")
@RestController
public class TrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

    //分页查询品牌信息
    @GetMapping("/baseTrademark/{pageNum}/{pageSize}")
    public Result baseTrademarkPage(@PathVariable Long pageNum,
                                    @PathVariable Long pageSize){

        Page<BaseTrademark> page = new Page<>(pageNum,pageSize);
        Page<BaseTrademark> result = baseTrademarkService.page(page);

        return Result.ok(result);
    }

    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    //根据ID查询品牌信息
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    //修改品牌信息
    @PutMapping("/baseTrademark/update")
    public Result updateTrademark(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    //根据品牌ID删除品牌信息
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeById(@PathVariable Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    //获取所有的品牌属性信息
    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list(null);
        return Result.ok(list);
    }
}
