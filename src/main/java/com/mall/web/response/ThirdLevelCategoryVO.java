package com.mall.web.response;

import java.io.Serializable;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 16:07
 * @description 首页分类数据VO(第三级)
 */
public class ThirdLevelCategoryVO implements Serializable {

    private static final long serialVersionUID = -6460775009553209174L;

    private Integer categoryId;

    private Integer categoryLevel;

    private String categoryName;

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
}
