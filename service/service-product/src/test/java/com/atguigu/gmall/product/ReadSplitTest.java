package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadSplitTest {

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Test
    public void writeTest(){
        BaseTrademark baseTrademark = new BaseTrademark();
        baseTrademark.setTmName("飞龙");
        baseTrademarkMapper.insert(baseTrademark);
    }

    @Test
    public void readTest(){
        BaseTrademark byId1 = baseTrademarkMapper.selectById(13);
        System.out.println("byId1 = " + byId1);

        BaseTrademark byId2 = baseTrademarkMapper.selectById(13);
        System.out.println("byId2 = " + byId2);

        BaseTrademark byId3 = baseTrademarkMapper.selectById(13);
        System.out.println("byId3 = " + byId3);

        BaseTrademark byId4 = baseTrademarkMapper.selectById(13);
        System.out.println("byId4 = " + byId4);

    }
}
