package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {
    @Autowired
    GoodsDao goodsDao;

    @Autowired
    ElasticsearchRestTemplate restTemplate;

    @Override
    public void savegoods(Goods goods) {
        goodsDao.save(goods);
    }

    @Override
    public void deleteGoods(Long skuId) {
        goodsDao.deleteById(skuId);
    }

    @Override
    public GoodsSearchResultVo search(SearchParam param) {
        //0.根据前端传递来的参数,构造复杂的检索条件
        Query query = buildQueryBySearchParam(param);
        //1.检索
        SearchHits<Goods> hits = restTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        //2.数据提取
        GoodsSearchResultVo resultVo = buildResponse(hits);
        return resultVo;
    }

    //根据前端传递的复杂检索条件,构造自己的query条件
    //打开追踪器.看下这个query对应的DSL到底是什么东西
    private Query buildQueryBySearchParam(SearchParam param) {
        //1.构建一个bool query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //2.bool-must
        if (param.getCategory3Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category3Id",param.getCategory3Id()));
        }

        //TODO 根据param完善 Query中的其他

        //代表完整的检索条件
        NativeSearchQuery query = new NativeSearchQuery(boolQuery);

        return query;
    }

    //根据检索结果构造响应数据
    private GoodsSearchResultVo buildResponse(SearchHits<Goods> hits) {
        return null;
    }
}
