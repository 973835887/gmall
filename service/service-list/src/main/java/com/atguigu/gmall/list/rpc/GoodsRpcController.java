package com.atguigu.gmall.list.rpc;


import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/rpc/inner/es")
public class GoodsRpcController {

    @Autowired
    GoodsSearchService goodsSearchService;

    //上架商品,保存数据到es
    @PostMapping("/goods/save")
    public Result saveGoods(@RequestBody Goods goods){
        goodsSearchService.savegoods(goods);
        return Result.ok();
    }

    //下架商品,从es中删除数据
    @GetMapping("/goods/delete/{skuId}")
    public Result deleteGoods(@PathVariable("skuId") Long skuId){
        goodsSearchService.deleteGoods(skuId);
        return Result.ok();
    }

    //检索商品数据
    @PostMapping("/goods/search")
    public Result<GoodsSearchResultVo> searchGoods(@RequestBody SearchParam param, HttpServletRequest request){
        GoodsSearchResultVo vo = goodsSearchService.search(param);
        return Result.ok(vo);
    }

    /**
     * 更新某个skuId对应商品的热度
     * @param skuId
     * @return
     */
    @GetMapping("/goods/incrHotScore/{skuId}")
    public Result updateHotScore(@PathVariable("skuId") Long skuId,
                                 @RequestParam("hotScore") Long score){
        goodsSearchService.updateHotScore(skuId,score);

        return Result.ok();
    }

}
