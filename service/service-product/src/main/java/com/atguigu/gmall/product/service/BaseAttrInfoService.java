package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service
* @createDate 2022-05-18 16:24:54
*/
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    List<BaseAttrInfo> getAttrInfoList(Long categoryId1, Long categoryId2, Long categoryId3);

    void saveAttrAndValue(BaseAttrInfo baseAttrInfo);

    void updateAttrAndValue(BaseAttrInfo baseAttrInfo);

    List<BaseAttrValue> getAttrValueList(Long attrId);

    //添加或修改属性以及属性值
    void saveOrUpdateAttrAndValue(BaseAttrInfo baseAttrInfo);
}
