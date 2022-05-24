package com.atguigu.gmall.item;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


public class GuavaTest {
    @Test
    public void guavaTest(){
        //Funnel<? super T> funnel,数据存储规则通道
        // int expectedInsertions, 数据量
        // double fpp  误判率

        //1.准备一个数据存储规则通道
        Funnel<Integer> funnel = Funnels.integerFunnel();

        //2.创建布隆过滤器
        BloomFilter<Integer> filter = BloomFilter.create(funnel, 1000000, 0.000001);

        filter.put(99);
        filter.put(199);
        filter.put(299);
        filter.put(399);

        //布隆过滤器是否存在该数据 mightContain
        System.out.println("filter.mightContain(299) = " + filter.mightContain(299));
        System.out.println("filter.mightContain(199) = " + filter.mightContain(199));
        System.out.println("filter.mightContain(288) = " + filter.mightContain(288));
    }
}
