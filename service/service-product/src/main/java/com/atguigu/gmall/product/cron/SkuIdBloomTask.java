package com.atguigu.gmall.product.cron;

import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.redisson.SkuBloomTask;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SkuIdBloomTask implements SkuBloomTask {

    @Autowired
    SkuInfoService skuInfoService;

    @Qualifier("skuIdBloom")
    @Autowired
    RBloomFilter<Object> skuIdBloom;


    //                秒分时日月周
    @Scheduled(cron = "0 0 3 * * 3")
    public void rebuildBloom(){
        //重建布隆
        log.info("系统正在重建sku布隆");
        skuIdBloom.delete();

        skuIdBloom.tryInit(1000000,0.00001);

        initData(skuIdBloom);
    }

    //重建布隆,并把shuId保存到布隆中
    @Override
    public void initData(RBloomFilter<Object> skuIdBloom) {
        log.info("系统正在初始化sku布隆");
        List<Long> ids = skuInfoService.getSkuIds();

        for (Long id : ids) {
            skuIdBloom.add(id);
        }

    }
}
