package com.mall.web.controller;

import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.web.manager.PersonalManager;
import com.mall.web.request.MallUserRequest;
import com.mall.web.response.MallUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class PersonalController {

    @Resource
    private PersonalManager personalManager;

    @RequestMapping(value = "/personal", method = RequestMethod.GET)
    public String personalPage(HttpServletRequest request,
                               HttpSession httpSession) {
        request.setAttribute("path", "personal");
        return "mall/personal";
    }

    @RequestMapping(value = "/personal/addresses", method = RequestMethod.GET)
    public String addressesPage() {
        return "mall/addresses";
    }

    @RequestMapping(value = "/personal/updateInfo", method = RequestMethod.POST)
    @ResponseBody
    public Result updateInfo(@RequestBody MallUserRequest mallUser, HttpSession httpSession) {
        MallUserVO mallUserTemp = personalManager.updateUserInfo(mallUser,httpSession);
        if (mallUserTemp == null) {
            Result result = ResultGenerator.genFailResult("修改失败");
            return result;
        } else {
            //返回成功
            Result result = ResultGenerator.genSuccessResult();
            return result;
        }
    }

}
