package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

public interface SkuDetailService {
    SkuDetailTo getDetail(Long skuId);

    void incrHotScore(Long skuId);
}
