package com.atguigu.gmall.list;

import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.SearchParam;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SearchTest {

    @Autowired
    GoodsSearchService goodsSearchService;

    @Test
    public void searchTest(){
        SearchParam param = new SearchParam();
        param.setCategory3Id(61L);
        param.setPageNo(2);
        param.setPageSize(3);
        param.setKeyword("手机");
        param.setTrademark("4:小米");
        String[] props = new String[]{"4:64GB:机身存储"};
        param.setProps(props);
        param.setOrder("2:desc");


        goodsSearchService.search(param);
    }
}
