package com.atguigui.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.list.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchParam;
import com.atguigu.gmall.model.vo.GoodsSearchResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Controller
public class ListController {

    @Autowired
    SearchFeignClient searchFeignClient;

    //@RequestParam(value = "category1Id",required = false) Long category1Id,
    //@RequestParam(value = "category2Id",required = false) Long category2Id,
    //@RequestParam(value = "category3Id",required = false) Long category3Id,
    //@RequestParam(value = "trademark",required = false) String trademark,
    //@RequestParam(value = "props",required = false) String props[],
    //@RequestParam(value = "order",required = false) String order,
    //@RequestParam(value = "pageNo",required = false) Long pageNo,
    //@RequestParam(value = "keyword",required = false) String keyword
    @GetMapping("/list.html")
    public String searchPage(SearchParam param, Model model, HttpServletRequest request){

        //todo 远程调用检索服务去检索
        Result<GoodsSearchResultVo> searchGoods = searchFeignClient.searchGoods(param);

        if (searchGoods.isOk()){
            GoodsSearchResultVo data = searchGoods.getData();
            //1.展示到页面,原来参数原封不动给页面
            model.addAttribute("searchParam",data.getSearchParam());

            //2.品牌面包屑:  例 品牌:VIVO
            model.addAttribute("trademarkParam",data.getTrademarkParam());

            //3.url参数
            model.addAttribute("urlParam",data.getUrlParam());

            //4.平台属性面包屑 :propParamsList: 集合里的每个元素(attrName/attrValue)
            model.addAttribute("propsParamList", data.getPropsParamList());

            //5.检索条件区  品牌列表 集合里面的每个元素(tmId,tmLogourl,tmName)
            model.addAttribute("trademarkList", data.getTrademarkList());

            //6.检索条件区 : 平台属性列表: attrList
            model.addAttribute("attrsList", data.getAttrsList());

            //7.排序信息: Bean (type.sort)
            model.addAttribute("orderMap", data.getOrderMap());

            //8.查到的商品列表: goodsList 集合中的每个元素(id,defaultImg,price,title)
            model.addAttribute("goodsList", data.getGoodsList());

            //9.分页信息 :当前页 .总页数
            model.addAttribute("pageNo", data.getPageNo());
            model.addAttribute("totalPages", data.getTotalPages());
        }



        return "list/index";
    }
}
