package com.mall.web.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 16:05
 * @description 首页分类数据VO
 */
public class MallIndexCategoryVO implements Serializable {

    private static final long serialVersionUID = 8185910718792339699L;

    private Integer categoryId;

    private Integer categoryLevel;

    private String categoryName;

    private List<SecondLevelCategoryVO> secondLevelCategoryVOS;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<SecondLevelCategoryVO> getSecondLevelCategoryVOS() {
        return secondLevelCategoryVOS;
    }

    public void setSecondLevelCategoryVOS(List<SecondLevelCategoryVO> secondLevelCategoryVOS) {
        this.secondLevelCategoryVOS = secondLevelCategoryVOS;
    }

}
