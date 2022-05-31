package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsSearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;
import com.atguigu.gmall.model.vo.SearchAttrListVo;
import com.atguigu.gmall.model.vo.SearchOrderMapVo;
import com.atguigu.gmall.model.vo.SearchTrademarkVo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
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
        GoodsSearchResultVo resultVo = buildResponse(hits,param);
        return resultVo;
    }

    @Override
    public void updateHotScore(Long skuId, Long score) {
        log.info("{} 商品的热度 被更新为 {}",skuId,score);
        //1、先查到sku的信息
        Optional<Goods> byId = goodsDao.findById(skuId);
        Goods goods = byId.get();
        goods.setHotScore(score);

        //2、再保存更新一下
        goodsDao.save(goods);


        //请自己写一个增量更新query
        //        restTemplate.update()

    }


    //根据前端传递的复杂检索条件,构造自己的query条件
    //打开追踪器.看下这个query对应的DSL到底是什么东西
    private Query buildQueryBySearchParam(SearchParam param) {
        NativeSearchQuery dsl = null;

                //构建一个bool query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        //================1.构造查询开始===============
        //1.按照三级分类进行查询 param.getCategory3Id()
        if (param.getCategory3Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category3Id",param.getCategory3Id()));
        }
        if (param.getCategory2Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category2Id",param.getCategory2Id()));
        }
        if (param.getCategory1Id() != null){
            boolQuery.must(QueryBuilders.termQuery("category1Id",param.getCategory1Id()));
        }
        //2.按照品牌进行查询 param.getTrademark()   trademark=2:苹果
        if (!StringUtils.isEmpty(param.getTrademark())){
            String[] split = param.getTrademark().split(":");
            boolQuery.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        //3.按照商品名称进行模糊查询. 全文匹配
        if (!StringUtils.isEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("title",param.getKeyword()));
        }
        //4.按照平台属性进行检索 props = 24:128G:机身内存
        if (param.getProps() != null && param.getProps().length >0){
            for (String prop : param.getProps()) {
                String[] split  = prop.split(":");
                //拼装一个属性  props = 24:128G:机身内存

                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));


                NestedQueryBuilder attrs = QueryBuilders.nestedQuery("attrs",nestedBoolQuery, ScoreMode.None);
                boolQuery.must(attrs);
            }
        }
        //================构造查询结束===============
        //代表完整的检索条件
        dsl= new NativeSearchQuery(boolQuery);
        //================2.构造排序开始===============


        //order=1:desc 2:asc 1 综合排序 2 价格排序
        //   order=1:asc
        if (!StringUtils.isEmpty(param.getOrder())){
            String[] split = param.getOrder().split(":");
            //排序需要的字段
            String sortField = "";

            switch (split[0]){
                case "1" : sortField = "hotScore"; break;
                case "2" : sortField = "price";break;
                default: sortField = "hotScore";
            }

            Sort sort = Sort.by(Sort.Direction.fromString(split[1]), sortField);
            dsl.addSort(sort);
        }
        //================构造排序结束===============

        //================3.构造分页开始===============
        Pageable pageable = PageRequest.of(param.getPageNo() -1, param.getPageSize());
        dsl.setPageable(pageable);
        //================构造分页结束===============

        //================4.构造高亮开始===============
        if (!StringUtils.isEmpty(param.getKeyword())){
            //模糊条件的结果加上高亮
            HighlightBuilder builder = new HighlightBuilder();

            builder.field("title").preTags("<span style='color:red'>").postTags("</span>");

            HighlightQuery query = new HighlightQuery(builder);

            dsl.setHighlightQuery(query);
        }
        //================构造高亮结束===============

        //================5.构造聚合分析开始===============
        //1.分析品牌
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg").field("tmId").size(100);

        //子聚合 - 聚合品牌名
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmNameAgg").field("tmName").size(1));
        //子聚合 - 聚合品牌logo
        tmIdAgg.subAggregation(AggregationBuilders.terms("tmLogoUrlAgg").field("tmLogoUrl").size(1));

        dsl.addAggregation(tmIdAgg);


        //2.分析平台属性  - 聚合平台属性
        NestedAggregationBuilder attrsAgg = AggregationBuilders.nested("attrsAgg", "attrs");
        //构造attrs子聚合attrIdAgg
        TermsAggregationBuilder attrsIdAgg = AggregationBuilders.terms("attrsIdAgg").field("attrs.attrId").size(100);
        //构造attrIdAgg子聚合

        attrsIdAgg.subAggregation(AggregationBuilders.terms("attrsNameAgg").field("attrs.attrName").size(1));
        attrsIdAgg.subAggregation(AggregationBuilders.terms("attrsValueAgg").field("attrs.attrValue").size(100));

        //将attrIdAgg放入整个子聚合中
        attrsAgg.subAggregation(attrsIdAgg);

        dsl.addAggregation(attrsAgg);


        return dsl;
    }

    //根据检索结果构造响应数据
    private GoodsSearchResultVo buildResponse(SearchHits<Goods> hits,SearchParam param) {
        GoodsSearchResultVo resultVo = new GoodsSearchResultVo();

        resultVo.setSearchParam(param);
        //面包屑
        String trademark = param.getTrademark();
        if (!StringUtils.isEmpty(trademark)){
            String[] split = trademark.split(":");
            resultVo.setTrademarkParam("品牌:"+split[1]);
        }
        //url参数
        String urlParam = buildUrlParam(param);
        resultVo.setUrlParam(urlParam);

        //4、平台属性面包屑
        List<SearchAttr> searchAttrs = new ArrayList<>();
        if(param.getProps()!=null && param.getProps().length > 0){
            //前端检索的时候带了平台属性检索，拼装出属性面包屑
            for (String prop : param.getProps()) {
                //props=1:1700-2799:价格
                SearchAttr attr = new SearchAttr();

                String[] split = prop.split(":");
                attr.setAttrId(Long.parseLong(split[0]));
                attr.setAttrValue(split[1]);
                attr.setAttrName(split[2]);

                searchAttrs.add(attr);
            }
        }
        resultVo.setPropsParamList(searchAttrs);


        //5.品牌列表信息  根据查询的响应json,得到品牌列表信息
        List<SearchTrademarkVo> trademarkList = analyseTrademarkList(hits);
        resultVo.setTrademarkList(trademarkList);

        //6. 平台属性列表信息 根据查询的响应json,分析平台属性列表信息
        List<SearchAttrListVo> attrsList = analyseAttrsList(hits);
        resultVo.setAttrsList(attrsList);

        //7、排序规则
        String order = param.getOrder();
        if(!StringUtils.isEmpty(order)){
            //order=1:desc 2:asc
            String[] split = order.split(":");
            SearchOrderMapVo orderMapVo = new SearchOrderMapVo();
            orderMapVo.setType(split[0]);
            orderMapVo.setSort(split[1]);
            resultVo.setOrderMap(orderMapVo);
        }

        //8.商品列表
        ArrayList<Goods> goods = new ArrayList<>();
        //提取所有查询到的商品信息
        for (SearchHit<Goods> hit : hits) {
            //获取命中记录商品的真正数据
            Goods content = hit.getContent();
            //如果是模糊检索需要用高亮的标题替换原标题
            if (!StringUtils.isEmpty(param.getKeyword())){
                content.setTitle(hit.getHighlightField("title").get(0));
            }
            goods.add(content);
        }
        resultVo.setGoodsList(goods);

        //9、页码
        resultVo.setPageNo(param.getPageNo());
        Long pages = hits.getTotalHits() % param.getPageSize() == 0 ? hits.getTotalHits() / param.getPageSize() : (hits.getTotalHits() / param.getPageSize() + 1);
        resultVo.setTotalPages(pages.intValue());

        //TODO 翻页溢出总是最后一页

        return resultVo;
    }

    //拼装出查询字符串,忽略order
    private String buildUrlParam(SearchParam param) {
        StringBuilder urlParamSb = new StringBuilder("list.html?"); //category1Id=1&
        if(param.getCategory1Id()!= null){
            urlParamSb.append("category1Id="+param.getCategory1Id()+"&");
        }

        if(param.getCategory2Id()!=null){
            urlParamSb.append("category2Id="+param.getCategory2Id()+"&");
        }

        if(param.getCategory3Id()!=null){
            urlParamSb.append("category3Id="+param.getCategory3Id()+"&");
        }

        if(!StringUtils.isEmpty(param.getTrademark())){
            urlParamSb.append("trademark="+param.getTrademark()+"&");
        }

        if(!StringUtils.isEmpty(param.getKeyword())){
            urlParamSb.append("keyword="+param.getKeyword()+"&");
        }

        if(param.getProps()!=null && param.getProps().length>0){
            //list.html?props=1:18GB:机身内存&props=2:18GB:运行内存
            for (String prop : param.getProps()) {
                urlParamSb.append("props="+prop+"&");
            }
        }

        //忽略了order、pageNo

//        urlParamSb.append("pageNo="+param.getPageNo()+"&");
//        urlParamSb.append("pageSize="+param.getPageSize()+"&");


        return urlParamSb.toString();
    }

    private List<SearchAttrListVo> analyseAttrsList(SearchHits<Goods> hits) {
        List<SearchAttrListVo> result = new ArrayList<>();

        Aggregations aggregations = hits.getAggregations();
        //1.拿到attrsAgg的聚合结果
        ParsedNested attrsAgg = aggregations.get("attrsAgg");
        //2.拿到attrsAgg的attrsIdAgg子聚合结果
        ParsedLongTerms attrsIdAgg = attrsAgg.getAggregations().get("attrsIdAgg");
        for (Terms.Bucket bucketItem : attrsIdAgg.getBuckets()) {
            SearchAttrListVo attrs = new SearchAttrListVo();
            //1.属性id
            Number number = bucketItem.getKeyAsNumber();

            attrs.setAttrId(number.longValue());
            //2.属性名
            ParsedStringTerms attrsNameAgg = bucketItem.getAggregations().get("attrsNameAgg");
            String attrName = attrsNameAgg.getBuckets().get(0).getKeyAsString();
            attrs.setAttrName(attrName);
            //3.属性值
            ParsedStringTerms attrsValueAgg = bucketItem.getAggregations().get("attrsValueAgg");
            List<String> attrsValueList = new ArrayList<>();
            for (Terms.Bucket bucket : attrsValueAgg.getBuckets()) {
                String attrValue = bucket.getKeyAsString();
                attrsValueList.add(attrValue);
            }
            attrs.setAttrValueList(attrsValueList);


            result.add(attrs);
        }

        return result;
    }

    //根据检索到的整个结果,分析品牌列表信息
    private List<SearchTrademarkVo> analyseTrademarkList(SearchHits<Goods> hits) {
        List<SearchTrademarkVo> result  = new ArrayList<>();

        //1.从hits中拿到所有的聚合结果
        Aggregations aggregations = hits.getAggregations();


        //2.拿到品牌聚合
        ParsedLongTerms tmIdAgg = aggregations.get("tmIdAgg");

        for (Terms.Bucket bucketItem : tmIdAgg.getBuckets()) {
            SearchTrademarkVo vo = new SearchTrademarkVo();
            //桶中的key就是品牌id
            Number tmId = bucketItem.getKeyAsNumber();
            vo.setTmId(tmId.longValue());

            //设置tmName
            ParsedStringTerms tmNameAgg = bucketItem.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            vo.setTmName(tmName);

            //设置tmLogoUrl
            ParsedStringTerms tmLogoUrlAgg = bucketItem.getAggregations().get("tmLogoUrlAgg");
            String tmLogoUrl = tmLogoUrlAgg.getBuckets().get(0).getKeyAsString();
            vo.setTmLogoUrl(tmLogoUrl);

            result.add(vo);
        }

        return result;
    }



}
