package com.atguigu.gmall.starter.cache.service.impl;

import com.atguigu.gmall.starter.cache.service.CacheService;
import com.atguigu.gmall.starter.utils.JSONs;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;

//    @Override
//    public List<CategoryAndChildsTo> getCategorys() {
//        //1.远程查询redis的categorys缓存数据
//        String catregorys = redisTemplate.opsForValue().get("catregorys");
//
//        //2.redis 没有缓存这个key的数据
//        if (StringUtils.isEmpty(catregorys)){
//        return null;
//        }
//
//        //3返回数据   反序列化
//        List<CategoryAndChildsTo> data = JSONs.strToCategoryObj(catregorys);
//        return data;
//    }
//
//
//    @Override
//    public void saveCategoryData(List<CategoryAndChildsTo> categorys) {
//        String str = JSONs.toStr(categorys);
//        redisTemplate.opsForValue().set("catregorys",str);
//    }

    @Override
    public <T extends Object> T getCacheData(String key, TypeReference<T> typeReference) {
        //1.远程查询redis的categorys缓存数据
        String catregorys = redisTemplate.opsForValue().get(key);

        //2.判断
        if (!StringUtils.isEmpty(catregorys)){
            if ("no".equals(catregorys)){
               T t =  JSONs.nullInstance(typeReference);
                return t;
            }
            //3. 转换成指定的格式
            T t = JSONs.strToObj(catregorys,typeReference);
            return t;
        }

        //缓存中真没有数据
        return null;
    }

    @Override
    public void save(String categoryKey,Object data) {
        if (data == null){
            redisTemplate.opsForValue().set(categoryKey,"no",30, TimeUnit.MINUTES);
        }else {
            //为了防止大量key同时过期,加上一个时间的随机值.防止缓存雪崩
            Double result = Math.random() * 1000000000;
            long mill = 1000 * 60 * 60 * 24 * 3 + result.intValue();
            redisTemplate.opsForValue().set(categoryKey,JSONs.toStr(data),mill,TimeUnit.MILLISECONDS);
        }

    }


}
