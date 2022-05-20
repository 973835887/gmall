package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author Administrator
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-05-18 16:24:54
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;


    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long categoryId1, Long categoryId2, Long categoryId3) {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.getAttrInfoList(categoryId1,categoryId2,categoryId3);
        return baseAttrInfoList;
    }

    //新增平台属性跟值
    @Transactional
    @Override
    public void saveAttrAndValue(BaseAttrInfo baseAttrInfo) {
        //保存属性名
        baseAttrInfoMapper.insert(baseAttrInfo);
        //保存属性值
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue value : valueList) {
            //回填属性ID
            Long id = baseAttrInfo.getId();
            value.setAttrId(id);
            baseAttrValueMapper.insert(value);
        }
    }

    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_id",attrId);
        List<BaseAttrValue> list = baseAttrValueMapper.selectList(queryWrapper);
        return list;
    }

    //添加或者修改属性名的值
    @Transactional
    @Override
    public void saveOrUpdateAttrAndValue(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo.getId() == null){
            saveAttrAndValue(baseAttrInfo);
        }else {
            updateAttrAndValue(baseAttrInfo);
        }
    }

    @Transactional
    public void updateAttrAndValue(BaseAttrInfo baseAttrInfo) {
        // 1. 修改属性直接修改
        baseAttrInfoMapper.updateById(baseAttrInfo);

        //3.0 查询不在之前属性值的ID;把之前存在的id保存到集合中
        List<Long> ids = new ArrayList<>();
        List<BaseAttrValue> list = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue value : list) {
            if (value.getId() !=null){
                //之前存在的id
                Long id = value.getId();
                ids.add(id);
            }
        }

        if (ids.size() > 0){
            //3.1-1 有数据,对比提交数据,有不在之前数据的ID则删除,然后
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",baseAttrInfo.getId());
            queryWrapper.notIn("id",ids);
            baseAttrValueMapper.delete(queryWrapper);
        }else {
            //没数据,全部删除
            QueryWrapper<BaseAttrValue> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id",baseAttrInfo.getId());
            baseAttrValueMapper.delete(queryWrapper);
        }


        //2.0  属性值中没有属性ID的数据,则新增;
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue value : valueList) {
            if (value.getId() == null){
             //新增设置attr_id
             value.setAttrId(baseAttrInfo.getId());
             baseAttrValueMapper.insert(value);
            }
            //2.1 属性值中有ID的数据,则修改
            if (value.getId() != null){
                baseAttrValueMapper.updateById(value);
            }
        }
    }
}




