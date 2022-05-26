package com.atguigu.gmall.starter.cache.service;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public interface CacheService {
//    List<CategoryAndChildsTo> getCategorys();
//
//    void saveCategoryData(List<CategoryAndChildsTo> categoryAndChildsToList);

    //从缓存获取数据
    <T> T  getCacheData(String cacheKey, TypeReference<T> typeReference);

    //保存
    void save(String categoryKey,Object data);
}
