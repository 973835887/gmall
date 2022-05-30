package com.atguigu.gmall.product.service.impl;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.product.domain.BaseCategoryView;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import java.util.Date;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.list.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
* @author Administrator
* @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
* @createDate 2022-05-20 15:42:58
*/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    @Autowired
    SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    SearchFeignClient searchFeignClient;

    @Qualifier("skuIdBloom")
    @Autowired
    RBloomFilter<Object> skuIdBloom;

    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Autowired
    BaseCategoryViewMapper baseCategoryViewMapper;

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        skuInfoMapper.insert(skuInfo);
        Long skuInfoId = skuInfo.getId();

        skuIdBloom.add(skuInfoId);


        System.out.println("skuInfoId = " + skuInfoId);
        Long spuId = skuInfo.getSpuId();

        //保存Sku平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuInfoId);
            skuAttrValueMapper.insert(skuAttrValue);
        }


        //保存Sku销售属性值
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValue.setSpuId(spuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        //保存库存单元图片列表
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuInfoId);
            skuImageMapper.insert(skuImage);
        }

    }

    //下架
    @Override
    public void updateStatus(Long skuId, int status) {
        //1.  保存 is_sale 状态
        skuInfoMapper.updateStatus(skuId,status);

        if (status == 1){
            //2. 给ES 保存/删除
            Goods goods = this.getSkuInfoForSearch(skuId);

            //3. 保存到es
            searchFeignClient.saveGoods(goods);
        }else {
            //4. 去es中删除数据
            if (status == 0){
                searchFeignClient.deleteGoods(skuId);
            }
        }
    }

    @Override
    public Goods getSkuInfoForSearch(Long skuId) {
        Goods goods = new Goods();

        //1.查询skuinfo信息并封装
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setTitle(skuInfo.getSkuName());
        goods.setPrice(skuInfo.getPrice().doubleValue());
        goods.setCreateTime(new Date());//上架时间
        //2.封装品牌信息
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(skuInfo.getTmId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //3.封装分类信息
        QueryWrapper<BaseCategoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category3_id",skuInfo.getCategory3Id());
        BaseCategoryView baseCategoryView = baseCategoryViewMapper.selectOne(queryWrapper);
        goods.setCategory1Id(baseCategoryView.getCategory1Id());
        goods.setCategory1Name(baseCategoryView.getCategory1Name());
        goods.setCategory2Id(baseCategoryView.getCategory2Id());
        goods.setCategory2Name(baseCategoryView.getCategory2Name());
        goods.setCategory3Id(baseCategoryView.getCategory3Id());
        goods.setCategory3Name(baseCategoryView.getCategory3Name());
        //4.热度分
        goods.setHotScore(0L);
        //5.当前sku的所有平台属性名和值 attrId attrValue attrName
        List<SearchAttr> searchAttrs = baseAttrInfoMapper.getSkuBaseAttrNameAndValue(skuId);
        goods.setAttrs(searchAttrs);

        return goods;
    }


    //查询商品价格
    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getSkuPrice(skuId);
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrAndValueBySkuId(Long skuId) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.getSpuSaleAttrAndValueBySkuId(skuId);
        return spuSaleAttrs;
    }

    @Override
    public List<Long> getSkuIds() {
        List<Long>  ids = skuInfoMapper.getSkuIds();
        return ids;
    }




}




