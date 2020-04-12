package com.mall.web.manager;

import com.mall.common.service.util.BeanUtil;
import com.mall.goodscenter.client.dto.CarouselDTO;
import com.mall.goodscenter.client.dto.GoodsCategoryDTO;
import com.mall.goodscenter.client.service.MallCarouselService;
import com.mall.goodscenter.client.service.MallCategoryService;
import com.mall.web.common.Constants;
import com.mall.web.enums.MallCategoryLevelEnum;
import com.mall.web.response.MallIndexCarouselVO;
import com.mall.web.response.MallIndexCategoryVO;
import com.mall.web.response.SecondLevelCategoryVO;
import com.mall.web.response.ThirdLevelCategoryVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 15:56
 * @description
 */
@Component
public class IndexManager {

    @Resource
    private MallCategoryService mallCategoryService;

    @Resource
    private MallCarouselService carouselService;

    public List<MallIndexCategoryVO> getCategoriesForIndex(){
        List<MallIndexCategoryVO> result = new ArrayList<>();
        //获取一级分类的固定数量的数据
        List<GoodsCategoryDTO> firstLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0), MallCategoryLevelEnum.LEVEL_ONE.getLevel(), Constants.INDEX_CATEGORY_NUMBER);
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            List<Integer> firstLevelCategoryIds = firstLevelCategories.stream().map(GoodsCategoryDTO::getId).collect(Collectors.toList());
            //获取二级分类的数据
            List<GoodsCategoryDTO> secondLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(firstLevelCategoryIds, MallCategoryLevelEnum.LEVEL_TWO.getLevel(), 0);
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                List<Integer> secondLevelCategoryIds = secondLevelCategories.stream().map(GoodsCategoryDTO::getId).collect(Collectors.toList());
                //获取三级分类的数据
                List<GoodsCategoryDTO> thirdLevelCategories = mallCategoryService.selectByLevelAndParentIdsAndNumber(secondLevelCategoryIds, MallCategoryLevelEnum.LEVEL_THREE.getLevel(), 0);
                if (!CollectionUtils.isEmpty(thirdLevelCategories)) {
                    //根据 parentId 将 thirdLevelCategories 分组
                    Map<Integer, List<GoodsCategoryDTO>> thirdLevelCategoryMap = thirdLevelCategories.stream().collect(groupingBy(GoodsCategoryDTO::getParentId));
                    List<SecondLevelCategoryVO> secondLevelCategoryVOS = new ArrayList<>();
                    //处理二级分类
                    for (GoodsCategoryDTO secondLevelCategory : secondLevelCategories) {
                        SecondLevelCategoryVO secondLevelCategoryVO = dto2SecondVO(secondLevelCategory);
                        //如果该二级分类下有数据则放入 secondLevelCategoryVOS 对象中
                        if (thirdLevelCategoryMap.containsKey(secondLevelCategory.getId())) {
                            //根据二级分类的id取出thirdLevelCategoryMap分组中的三级分类list
                            List<GoodsCategoryDTO> tempGoodsCategories = thirdLevelCategoryMap.get(secondLevelCategory.getId());
                            secondLevelCategoryVO.setThirdLevelCategoryVOS(tempGoodsCategories.stream().map(IndexManager::dto2ThirdVO).collect(Collectors.toList()));
                            secondLevelCategoryVOS.add(secondLevelCategoryVO);
                        }
                    }
                    //处理一级分类
                    if (!CollectionUtils.isEmpty(secondLevelCategoryVOS)) {
                        //根据 parentId 将 thirdLevelCategories 分组
                        Map<Integer, List<SecondLevelCategoryVO>> secondLevelCategoryVOMap = secondLevelCategoryVOS.stream().collect(groupingBy(SecondLevelCategoryVO::getParentId));
                        for (GoodsCategoryDTO firstCategory : firstLevelCategories) {
                            MallIndexCategoryVO newBeeMallIndexCategoryVO = dto2IndexVO(firstCategory);
                            //如果该一级分类下有数据则放入 newBeeMallIndexCategoryVOS 对象中
                            if (secondLevelCategoryVOMap.containsKey(firstCategory.getId())) {
                                //根据一级分类的id取出secondLevelCategoryVOMap分组中的二级级分类list
                                List<SecondLevelCategoryVO> tempGoodsCategories = secondLevelCategoryVOMap.get(firstCategory.getId());
                                newBeeMallIndexCategoryVO.setSecondLevelCategoryVOS(tempGoodsCategories);
                                result.add(newBeeMallIndexCategoryVO);
                            }
                        }
                    }
                }
            }
            return result;
        } else {
            return null;
        }
    }

    public List<MallIndexCarouselVO> getCarouselsForIndex(int number) {

        List<MallIndexCarouselVO> MallIndexCarouselVOS = new ArrayList<>(number);
        List<CarouselDTO> carousels = carouselService.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carousels)) {
            MallIndexCarouselVOS = carousels.stream().map(IndexManager::dto2CarouselVO).collect(Collectors.toList());
        }
        return MallIndexCarouselVOS;

    }


    public static MallIndexCarouselVO dto2CarouselVO(CarouselDTO dto) {
        if (dto == null) {
            return null;
        }
        MallIndexCarouselVO mallIndexCarouselVO = new MallIndexCarouselVO();
        mallIndexCarouselVO.setCarouselUrl(dto.getCarouselUrl());
        mallIndexCarouselVO.setRedirectUrl(dto.getRedirectUrl());
        return mallIndexCarouselVO;
    }

    public static ThirdLevelCategoryVO dto2ThirdVO(GoodsCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        ThirdLevelCategoryVO thirdLevelCategoryVO = new ThirdLevelCategoryVO();
        thirdLevelCategoryVO.setCategoryLevel(dto.getCategoryLevel());
        thirdLevelCategoryVO.setCategoryName(dto.getCategoryName());
        thirdLevelCategoryVO.setCategoryId(dto.getId());
        return thirdLevelCategoryVO;
    }

    public static SecondLevelCategoryVO dto2SecondVO(GoodsCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        SecondLevelCategoryVO secondLevelCategoryVO = new SecondLevelCategoryVO();
        secondLevelCategoryVO.setCategoryLevel(dto.getCategoryLevel());
        secondLevelCategoryVO.setParentId(dto.getParentId());
        secondLevelCategoryVO.setCategoryName(dto.getCategoryName());
        secondLevelCategoryVO.setCategoryId(dto.getId());
        return secondLevelCategoryVO;
    }

    public static MallIndexCategoryVO dto2IndexVO(GoodsCategoryDTO dto) {
        if (dto == null) {
            return null;
        }
        MallIndexCategoryVO mallIndexCategoryVO = new MallIndexCategoryVO();
        mallIndexCategoryVO.setCategoryLevel(dto.getCategoryLevel());
        mallIndexCategoryVO.setCategoryName(dto.getCategoryName());
        mallIndexCategoryVO.setCategoryId(dto.getId());
        return mallIndexCategoryVO;
    }


}
