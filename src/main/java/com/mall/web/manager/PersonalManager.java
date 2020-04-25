package com.mall.web.manager;

import com.mall.account.client.dto.MallUserDTO;
import com.mall.account.client.service.MallUserService;
import com.mall.common.service.util.BeanUtil;
import com.mall.web.common.Constants;
import com.mall.web.request.MallUserRequest;
import com.mall.web.response.MallUserVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Component
public class PersonalManager {

    @Resource
    private MallUserService mallUserService;

    public MallUserVO updateUserInfo(MallUserRequest mallUser, HttpSession httpSession) {
        MallUserDTO user = mallUserService.selectByPrimaryKey(mallUser.getUserId());
        if (user != null) {
            user.setNickName(mallUser.getNickName());
            user.setAddress(mallUser.getAddress());
            user.setIntroduceSign(mallUser.getIntroduceSign());
            if (mallUserService.updateByPrimaryKeySelective(user) > 0) {
                MallUserVO mallUserVO = new MallUserVO();
                user = mallUserService.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, mallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, mallUserVO);
                return mallUserVO;
            }
        }
        return null;
    }
}
