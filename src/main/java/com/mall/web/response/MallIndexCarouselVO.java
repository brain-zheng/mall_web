package com.mall.web.response;

import java.io.Serializable;

/**
 * @author zheng haijain
 * @createTime 2020-04-12 17:06
 * @description 首页轮播图VO
 */
public class MallIndexCarouselVO implements Serializable {

    private static final long serialVersionUID = 2514290047166310879L;

    private String carouselUrl;

    private String redirectUrl;

    public String getCarouselUrl() {
        return carouselUrl;
    }

    public void setCarouselUrl(String carouselUrl) {
        this.carouselUrl = carouselUrl;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

}
