package com.mall.web.response;


import java.io.Serializable;

public class MallUserVO implements Serializable {

    private static final long serialVersionUID = 2266980539085387939L;

    private Integer userId;

    private String nickName;

    private String loginName;

    private String introduceSign;

    private String address;

    private Integer shopCartItemCount;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getIntroduceSign() {
        return introduceSign;
    }

    public void setIntroduceSign(String introduceSign) {
        this.introduceSign = introduceSign;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getShopCartItemCount() {
        return shopCartItemCount;
    }

    public void setShopCartItemCount(Integer shopCartItemCount) {
        this.shopCartItemCount = shopCartItemCount;
    }
}
