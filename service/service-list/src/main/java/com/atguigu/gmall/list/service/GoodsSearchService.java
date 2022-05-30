package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;

public interface GoodsSearchService {
    //保存商品数据到es中
    void savegoods(Goods goods);

    //删除es中的数据
    void deleteGoods(Long skuId);

    //检索
    GoodsSearchResultVo search(SearchParam param);
}
