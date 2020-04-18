package com.mall.web.manager;

import com.mall.account.client.dto.MallUserDTO;
import com.mall.account.client.service.MallUserService;
import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.web.common.Constants;
import com.mall.web.response.MallUserVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class LoginManager {

    @Resource
    private MallUserService mallUserService;

    public String login(String loginName, String passwordMD5, HttpSession httpSession){
        MallUserDTO user = mallUserService.queryUserByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            MallUserVO newBeeMallUserVO = LoginManager.dto2vo(user);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, newBeeMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    public static MallUserVO dto2vo(MallUserDTO dto) {
        if (dto == null) {
            return null;
        }
        MallUserVO mallUserVO = new MallUserVO();
        mallUserVO.setUserId(dto.getUserId());
        mallUserVO.setNickName(dto.getNickName());
        mallUserVO.setLoginName(dto.getLoginName());
        mallUserVO.setIntroduceSign(dto.getIntroduceSign());
        mallUserVO.setAddress(dto.getAddress());
        return mallUserVO;
    }


}
