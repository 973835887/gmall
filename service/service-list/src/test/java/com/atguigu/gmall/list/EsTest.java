package com.atguigu.gmall.list;

import com.atguigu.gmall.common.util.JSONs;
import org.elasticsearch.index.mapper.ParseContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
public class EsTest {

    @Autowired
    ElasticsearchRestTemplate restTemplate;

    @Test
    void EsQuery(){
        //简单查询
        Hello hello = restTemplate.get("1", Hello.class, IndexCoordinates.of("hello"));
        System.out.println("hello = " + hello);

        //Query query, Class<T> clazz, IndexCoordinates index  复杂查询
        SearchHits<Hello> search = restTemplate.search(Query.findAll(), Hello.class, IndexCoordinates.of("hello"));
        //获取所有命中的记录
        List<SearchHit<Hello>> searchHits = search.getSearchHits();
        for (SearchHit<Hello> searchHit : searchHits) {
            System.out.println(searchHit.getId());
            System.out.println(searchHit.getScore());//得分
            System.out.println(searchHit.getContent());//具体数据
        }

    }

    @Test
    void EsUpdate(){
        //更新操作
        Hello hello = new Hello(2, "飞龙同学", 29);
        String toStr = JSONs.toStr(hello);
//        Document document = Document.parse(toStr);
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId",2);
        map.put("address","湖北武汉");
        Document document = Document.from(map);

        UpdateQuery build = UpdateQuery.builder("1")
                .withDocAsUpsert(true) //增量更新,用map操作新增一条字段
                .withDocument(document).build();
        restTemplate.update(build,IndexCoordinates.of("hello"));
    }

    @Test
    void EsDelete(){
        //删除数据.根据ID删除
        restTemplate.delete("hello",IndexCoordinates.of("hello"));
    }

    @Test
    void EsIndex(){
        //es 添加数据
        //IndexQuery query, IndexCoordinates index
        IndexQuery query = new IndexQuery();
        Hello hello = new Hello(1, "文超", 19);
        query.setId(hello.getUserId().toString());//指定用的id
        query.setObject(hello);//指定用的对象
        IndexCoordinates index = IndexCoordinates.of("hello");
        restTemplate.index(query,index);

    }
}
