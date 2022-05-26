package com.atguigu.gmall.starter.redisson;

import org.redisson.api.RBloomFilter;

public interface BloomTask {
    void initData(RBloomFilter<Object> skuIdBloom);
}
