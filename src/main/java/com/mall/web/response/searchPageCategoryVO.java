package com.mall.web.response;

import java.io.Serializable;
import java.util.List;

public class searchPageCategoryVO implements Serializable {

    private static final long serialVersionUID = 5256019368469689830L;

    private String firstLevelCategoryName;

    private List<GoodsCategoryVO> secondLevelCategoryList;

    private String secondLevelCategoryName;

    private List<GoodsCategoryVO> thirdLevelCategoryList;

    private String currentCategoryName;

    public String getFirstLevelCategoryName() {
        return firstLevelCategoryName;
    }

    public void setFirstLevelCategoryName(String firstLevelCategoryName) {
        this.firstLevelCategoryName = firstLevelCategoryName;
    }

    public List<GoodsCategoryVO> getSecondLevelCategoryList() {
        return secondLevelCategoryList;
    }

    public void setSecondLevelCategoryList(List<GoodsCategoryVO> secondLevelCategoryList) {
        this.secondLevelCategoryList = secondLevelCategoryList;
    }

    public String getSecondLevelCategoryName() {
        return secondLevelCategoryName;
    }

    public void setSecondLevelCategoryName(String secondLevelCategoryName) {
        this.secondLevelCategoryName = secondLevelCategoryName;
    }

    public List<GoodsCategoryVO> getThirdLevelCategoryList() {
        return thirdLevelCategoryList;
    }

    public void setThirdLevelCategoryList(List<GoodsCategoryVO> thirdLevelCategoryList) {
        this.thirdLevelCategoryList = thirdLevelCategoryList;
    }

    public String getCurrentCategoryName() {
        return currentCategoryName;
    }

    public void setCurrentCategoryName(String currentCategoryName) {
        this.currentCategoryName = currentCategoryName;
    }
}
