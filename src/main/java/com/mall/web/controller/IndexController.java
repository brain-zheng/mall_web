package com.mall.web.controller;

import com.mall.admincenter.client.dto.MallIndexConfigGoodsDTO;
import com.mall.admincenter.client.service.IndexConfigService;
import com.mall.web.common.Constants;
import com.mall.web.enums.IndexConfigTypeEnum;
import com.mall.web.manager.IndexManager;
import com.mall.web.response.MallIndexCarouselVO;
import com.mall.web.response.MallIndexCategoryVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 12:03
 * @description
 */
@Controller
public class IndexController {

    @Resource
    private IndexManager indexManager;

    @Resource
    private IndexConfigService indexConfigService;

    @RequestMapping(value = {"/", "/index","/index.html"}, method = RequestMethod.GET)
    public String indexPage(HttpServletRequest request){
        List<MallIndexCategoryVO> categories = indexManager.getCategoriesForIndex();
        if (categories == null || categories.size() == 0) {
            return "error/error_5xx";
        }
        List<MallIndexCarouselVO> carousels = indexManager.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<MallIndexConfigGoodsDTO> hotGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<MallIndexConfigGoodsDTO> newGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<MallIndexConfigGoodsDTO> recommendGoodses = indexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);

        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品

        return "mall/index";
    }


}
