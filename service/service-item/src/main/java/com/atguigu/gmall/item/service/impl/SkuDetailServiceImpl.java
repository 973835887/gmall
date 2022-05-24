package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.cache.service.CacheService;
import com.atguigu.gmall.common.constants.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SkuDetailServiceImpl implements SkuDetailService {

    @Autowired
    ProductFeignClient productFeignClient;

    @Autowired
    ThreadPoolExecutor corePool;

    @Autowired
    CacheService cacheService;

    @Autowired
    RBloomFilter<Object> skuIdBloom;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public SkuDetailTo getDetail(Long skuId) {
        String cacheKey = RedisConst.SKU_CACHE_KEY_PREFIX + skuId;
        //1.查询缓存
        SkuDetailTo cacheData = cacheService.getCacheData(cacheKey, new TypeReference<SkuDetailTo>() {
        });

        //2 .判断;布隆中没有数据
        if (cacheData == null){

            //3. 回源之前问问bloom中有无数据
            // 4 .bloom中有, 缓存中没有
            if (skuIdBloom.contains(skuId)) {
                log.info("skuDetail缓存未命中,正在回源{}",skuId);

                //6 枷锁,否则会被缓存击穿[占坑+自动过期时间(原子)]
                String token = UUID.randomUUID().toString();
                Boolean lock = redisTemplate.opsForValue().setIfAbsent(RedisConst.LOCK_PREFIX+skuId, token,10, TimeUnit.SECONDS);
                SkuDetailTo db = null;
                if (lock){
                    try{
                        log.info("分布式枷锁成功:skuDetail真的回源数据库{}",skuId);
                        //4. 1 操作数据库[回源],
                        db = getDetailFromDb(skuId);
                        //4.2 ,保存数据到缓存中
                        cacheService.save(cacheKey,db);
                    }finally {
                        //释放锁
//                        String s = redisTemplate.opsForValue().get("lock");
                        //删锁:[对比锁值+删除(合并起来是原子的)]
                        String deleteScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                        Long result = redisTemplate.execute(new DefaultRedisScript<>(deleteScript,Long.class), Arrays.asList(RedisConst.LOCK_PREFIX), token);
                        if (result == 1){
                            log.info("分布式解锁完成");
                        }else {
                            //别人的锁
                            log.info("别人的锁");
                        }

                    }
                }else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        cacheData = cacheService.getCacheData(cacheKey, new TypeReference<SkuDetailTo>() {
                        });
                        return cacheData;
                    }
                }
                return db;
            }

            //5 存入不存在的数据,即1 -999 查的是1000
            log.info("skuDetail缓存未命中,bloom防火墙拦截,打回{}"+skuId);
            return null;

        }


        //返回数据
        log.info("skuDetail缓存命中",skuId);
        return cacheData;
    }

    //查询skuId指定的详情数据
//    @Override
    public SkuDetailTo getDetailFromDb(Long skuId) {
        SkuDetailTo skuDetailTo = new SkuDetailTo();

        CompletableFuture<Void> categoryTask = CompletableFuture.runAsync(() -> {
            //查询分类信息
            Result<BaseCategoryView> cagetgoryView = productFeignClient.getCagetgoryView(skuId);
            if (cagetgoryView.isOk()) {
                skuDetailTo.setCategoryView(cagetgoryView.getData());
            }
        }, corePool);


        CompletableFuture<Void> skuInfoTask = CompletableFuture.runAsync(() -> {
            //查询sku信息 + 查询sku的图片列表
            Result<SkuInfo> skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo.isOk()) {
                skuDetailTo.setSkuInfo(skuInfo.getData());
            }
        }, corePool);


        CompletableFuture<Void> priceTask = CompletableFuture.runAsync(() -> {
            //查询价格price信息
            Result<BigDecimal> skuPrice = productFeignClient.getSkuPrice(skuId);
            if (skuPrice.isOk()) {
                skuDetailTo.setPrice(skuPrice.getData());
            }
        }, corePool);


        CompletableFuture<Void> spuSaleAttrTak = CompletableFuture.runAsync(() -> {
            //查询所有对应的销售属性集合
            Result<List<SpuSaleAttr>> result = productFeignClient.getSpuSaleAttrAndValueBySkuId(skuId);
            if (result.isOk()) {
                skuDetailTo.setSpuSaleAttrList(result.getData());
            }
        }, corePool);


        CompletableFuture<Void> valueJsonTak = CompletableFuture.runAsync(() -> {
            //查询sku对应的valuejson数据
            Result<Map<String, String>> valueJson = productFeignClient.getSkuValueJson(skuId);
            if (valueJson.isOk()) {
                Map<String, String> data = valueJson.getData();
                System.out.println("data = " + data);
                String str = JSONs.toStr(data);
                skuDetailTo.setValuesSkuJson(str);
            }
        }, corePool);

        CompletableFuture.allOf(categoryTask,skuInfoTask,priceTask,spuSaleAttrTak,valueJsonTak)
                .join();

        return skuDetailTo;
    }


}
