package com.mall.web.controller;

import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.common.service.util.Result;
import com.mall.common.service.util.ResultGenerator;
import com.mall.web.common.Constants;
import com.mall.web.manager.MallShoppingCartManager;
import com.mall.web.request.MallShoppingCartItem;
import com.mall.web.response.MallShoppingCartItemVO;
import com.mall.web.response.MallUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private MallShoppingCartManager mallShoppingCartManager;

    @RequestMapping(value = "/shop-cart", method = RequestMethod.GET)
    public String cartListPage(HttpServletRequest request, HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<MallShoppingCartItemVO> myShoppingCartItems = mallShoppingCartManager.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(MallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (MallShoppingCartItemVO newBeeMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += newBeeMallShoppingCartItemVO.getGoodsCount() * newBeeMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @RequestMapping(value = "/shop-cart", method = RequestMethod.POST)
    @ResponseBody
    public Result saveShoppingCartItem(@RequestBody MallShoppingCartItem mallShoppingCartItem,
                                                 HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        mallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = mallShoppingCartManager.saveMallCartItem(mallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @RequestMapping(value = "/shop-cart", method = RequestMethod.PUT)
    @ResponseBody
    public Result updateMallShoppingCartItem(@RequestBody MallShoppingCartItem mallShoppingCartItem,
                                                   HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        mallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = mallShoppingCartManager.updateMallCartItem(mallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @RequestMapping(value = "/shop-cart/{newBeeMallShoppingCartItemId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result updateMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Integer newBeeMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = mallShoppingCartManager.deleteById(newBeeMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        MallUserVO user = (MallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<MallShoppingCartItemVO> myShoppingCartItems = mallShoppingCartManager.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }

}
