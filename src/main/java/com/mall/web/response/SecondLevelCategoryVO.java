package com.mall.web.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 16:07
 * @description 首页分类数据VO(第二级)
 */
public class SecondLevelCategoryVO implements Serializable {

    private static final long serialVersionUID = 6175101884172485915L;

    private Integer categoryId;

    private Integer parentId;

    private Integer categoryLevel;

    private String categoryName;

    private List<ThirdLevelCategoryVO> thirdLevelCategoryVOS;

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

    public List<ThirdLevelCategoryVO> getThirdLevelCategoryVOS() {
        return thirdLevelCategoryVOS;
    }

    public void setThirdLevelCategoryVOS(List<ThirdLevelCategoryVO> thirdLevelCategoryVOS) {
        this.thirdLevelCategoryVOS = thirdLevelCategoryVOS;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

}
