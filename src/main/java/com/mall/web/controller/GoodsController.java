package com.mall.web.controller;


import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.goodscenter.client.dto.GoodsCategoryDTO;
import com.mall.goodscenter.client.dto.MallGoodsInfoDTO;
import com.mall.goodscenter.client.dto.SearchPageCategoryDTO;
import com.mall.goodscenter.client.service.MallCategoryService;
import com.mall.goodscenter.client.service.MallGoodsInfoService;
import com.mall.web.common.Constants;
import com.mall.web.common.MallException;
import com.mall.web.response.GoodsCategoryVO;
import com.mall.web.response.MallGoodsDetailVO;
import com.mall.web.response.MallSearchGoodsVO;
import com.mall.web.response.searchPageCategoryVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class GoodsController {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallGoodsInfoService goodsInfoService;


    @RequestMapping(value = {"/search", "/search.html"}, method = RequestMethod.GET)
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Integer categoryId = Integer.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryDTO searchPageCategoryDTO = mallCategoryService.getCategoriesForSearch(categoryId);
            searchPageCategoryVO searchPageCategoryVO = GoodsController.dto2vo(searchPageCategoryDTO);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        Integer page = Integer.valueOf(params.get("page") + "");
        Integer limit = Constants.GOODS_SEARCH_PAGE_LIMIT;
        Integer goodsCategoryId = Integer.valueOf(params.get("goodsCategoryId") + "");
        String orderBy = params.get("orderBy") + "";
        PageQueryUtil pageUtil = new PageQueryUtil(page, limit);
        PageResult pageResult = goodsInfoService.searchMallGoods(pageUtil, goodsCategoryId, orderBy, keyword);
        List<MallGoodsInfoDTO> mallGoodsInfoDTOS = (List<MallGoodsInfoDTO>) pageResult.getList();
        List<MallSearchGoodsVO> mallSearchGoodsVOS = mallGoodsInfoDTOS.stream().map(GoodsController::searchGoodsDTO2VO).collect(Collectors.toList());
        pageResult.setList(mallSearchGoodsVOS);
        request.setAttribute("pageResult", pageResult);
        return "mall/search";
    }

    @RequestMapping(value = "/goods/detail/{goodsId}", method = RequestMethod.GET)
    public String detailPage(@PathVariable("goodsId") Integer goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        MallGoodsInfoDTO goods = goodsInfoService.getById(goodsId);
        if (goods == null) {
            MallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()){
            MallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        MallGoodsDetailVO goodsDetailVO = mallGoodsInfoDTO2VO(goods);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

    public static MallGoodsDetailVO mallGoodsInfoDTO2VO(MallGoodsInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        MallGoodsDetailVO mallGoodsDetailVO = new MallGoodsDetailVO();
        mallGoodsDetailVO.setGoodsName(dto.getGoodsName());
        mallGoodsDetailVO.setGoodsIntro(dto.getGoodsIntro());
        mallGoodsDetailVO.setGoodsCoverImg(dto.getGoodsCoverImg());
        mallGoodsDetailVO.setOriginalPrice(dto.getOriginalPrice());
        mallGoodsDetailVO.setSellingPrice(dto.getSellingPrice());
        mallGoodsDetailVO.setGoodsDetailContent(dto.getGoodsDetailContent());
        mallGoodsDetailVO.setGoodsId(dto.getId());

        return mallGoodsDetailVO;
    }

    private static MallSearchGoodsVO searchGoodsDTO2VO(MallGoodsInfoDTO dto) {
        if (dto == null) {
            return null;
        }
        MallSearchGoodsVO mallSearchGoodsVO = new MallSearchGoodsVO();
        mallSearchGoodsVO.setGoodsId(dto.getId());
        mallSearchGoodsVO.setGoodsName(dto.getGoodsName());
        mallSearchGoodsVO.setGoodsIntro(dto.getGoodsIntro());
        mallSearchGoodsVO.setGoodsCoverImg(dto.getGoodsCoverImg());
        mallSearchGoodsVO.setSellingPrice(dto.getSellingPrice());
        return mallSearchGoodsVO;
    }

    private static searchPageCategoryVO dto2vo(SearchPageCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        searchPageCategoryVO searchPageCategoryVO = new searchPageCategoryVO();
        searchPageCategoryVO.setFirstLevelCategoryName(dto.getFirstLevelCategoryName());
        searchPageCategoryVO.setSecondLevelCategoryList(dto.getSecondLevelCategoryList().stream().map(GoodsController::goodsCategoryDTO2VO).collect(Collectors.toList()));
        searchPageCategoryVO.setSecondLevelCategoryName(dto.getSecondLevelCategoryName());
        searchPageCategoryVO.setThirdLevelCategoryList(dto.getSecondLevelCategoryList().stream().map(GoodsController::goodsCategoryDTO2VO).collect(Collectors.toList()));
        searchPageCategoryVO.setCurrentCategoryName(dto.getCurrentCategoryName());
        return searchPageCategoryVO;
    }

    private static GoodsCategoryVO goodsCategoryDTO2VO(GoodsCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        GoodsCategoryVO goodsCategoryVO = new GoodsCategoryVO();
        goodsCategoryVO.setCategoryLevel(dto.getCategoryLevel());
        goodsCategoryVO.setParentId(dto.getParentId());
        goodsCategoryVO.setCategoryName(dto.getCategoryName());
        goodsCategoryVO.setCategoryRank(dto.getCategoryRank());
        goodsCategoryVO.setIsDeleted(dto.getDeleted());
        goodsCategoryVO.setCategoryId(dto.getId());
        goodsCategoryVO.setCreateTime(dto.getCreateTime());
        goodsCategoryVO.setUpdateTime(dto.getUpdateTime());
        return goodsCategoryVO;
    }

}
