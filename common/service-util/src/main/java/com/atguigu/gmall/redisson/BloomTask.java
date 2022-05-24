package com.atguigu.gmall.redisson;

import org.redisson.api.RBloomFilter;

public interface BloomTask {
    void initData(RBloomFilter<Object> skuIdBloom);
}
