package com.mall.web.manager;

import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.goodscenter.client.dto.MallGoodsInfoDTO;
import com.mall.goodscenter.client.service.MallGoodsInfoService;
import com.mall.ordercenter.client.dto.MallShoppingCartItemDTO;
import com.mall.ordercenter.client.service.ShoppingCartService;
import com.mall.web.common.Constants;
import com.mall.web.request.MallShoppingCartItem;
import com.mall.web.response.MallShoppingCartItemVO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MallShoppingCartManager {

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private MallGoodsInfoService mallGoodsInfoService;

    public List<MallShoppingCartItemVO> getMyShoppingCartItems(Integer mallUserId) {
        List<MallShoppingCartItemVO> mallShoppingCartItemVOS = new ArrayList<>();
        List<MallShoppingCartItemDTO> mallShoppingCartItems = shoppingCartService.selectByUserId(mallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(mallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Integer> mallGoodsIds = mallShoppingCartItems.stream().map(MallShoppingCartItemDTO::getGoodsId).collect(Collectors.toList());
            List<MallGoodsInfoDTO> mallGoodsInfoDTOS = mallGoodsIds.stream().map(mallGoodsInfoService::getById).collect(Collectors.toList());

            Map<Integer, MallGoodsInfoDTO> mallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(mallGoodsInfoDTOS)) {
                mallGoodsMap = mallGoodsInfoDTOS.stream().collect(Collectors.toMap(MallGoodsInfoDTO::getId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (MallShoppingCartItemDTO mallShoppingCartItem : mallShoppingCartItems) {
                MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
                MallShoppingCartItemVO itemVO = dto2vo(mallShoppingCartItem);
                if (mallGoodsMap.containsKey(mallShoppingCartItem.getGoodsId())) {
                    MallGoodsInfoDTO mallGoodsTemp = mallGoodsMap.get(mallShoppingCartItem.getGoodsId());
                    mallShoppingCartItemVO.setGoodsCoverImg(mallGoodsTemp.getGoodsCoverImg());
                    String goodsName = mallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    mallShoppingCartItemVO.setGoodsName(goodsName);
                    mallShoppingCartItemVO.setSellingPrice(mallGoodsTemp.getSellingPrice());
                    mallShoppingCartItemVOS.add(mallShoppingCartItemVO);
                }
            }
        }
        return mallShoppingCartItemVOS;
    }

    public String saveMallCartItem(MallShoppingCartItem mallShoppingCartItem) {
        MallShoppingCartItemDTO temp = shoppingCartService.selectByUserIdAndGoodsId(
                    mallShoppingCartItem.getUserId(), mallShoppingCartItem.getGoodsId());
        if (temp != null) {
            temp.setGoodsCount(temp.getGoodsCount() + mallShoppingCartItem.getGoodsCount());
            return updateMallCartItem(dto2request(temp));
        }
        MallShoppingCartItemDTO mallShoppmingCartItemUpdate = shoppingCartService.selectByPrimaryKey(mallShoppingCartItem.getCartItemId());
        if (mallShoppmingCartItemUpdate == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = shoppingCartService.selectCountByUserId(mallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (mallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (shoppingCartService.insertSelective(request2dto(mallShoppingCartItem)) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();

    }

    public String updateMallCartItem(MallShoppingCartItem mallShoppingCartItem) {
        MallShoppingCartItemDTO mallShoppmingCartItemUpdate = shoppingCartService.selectByPrimaryKey(mallShoppingCartItem.getCartItemId());
        if (mallShoppmingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (mallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        mallShoppmingCartItemUpdate.setGoodsCount(mallShoppingCartItem.getGoodsCount());
        mallShoppmingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (shoppingCartService.updateByPrimaryKeySelective(mallShoppmingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    public Boolean deleteById(Integer mallShoppingCartItemId) {
        return shoppingCartService.deleteByPrimaryKey(mallShoppingCartItemId) > 0;
    }

    private static MallShoppingCartItemVO dto2vo(MallShoppingCartItemDTO dto) {
        if (dto == null) {
            return null;
        }
        MallShoppingCartItemVO mallShoppingCartItemVO = new MallShoppingCartItemVO();
        mallShoppingCartItemVO.setCartItemId(dto.getCartItemId());
        mallShoppingCartItemVO.setGoodsId(dto.getGoodsId());
        mallShoppingCartItemVO.setGoodsCount(dto.getGoodsCount());
        return mallShoppingCartItemVO;
    }

    private static MallShoppingCartItemDTO request2dto(MallShoppingCartItem mallShoppingCartItem) {
        if (mallShoppingCartItem == null) {
            return null;
        }
        MallShoppingCartItemDTO mallShoppingCartItemDTO = new MallShoppingCartItemDTO();
        mallShoppingCartItemDTO.setCartItemId(mallShoppingCartItem.getCartItemId());
        mallShoppingCartItemDTO.setUserId(mallShoppingCartItem.getUserId());
        mallShoppingCartItemDTO.setGoodsId(mallShoppingCartItem.getGoodsId());
        mallShoppingCartItemDTO.setGoodsCount(mallShoppingCartItem.getGoodsCount());
        mallShoppingCartItemDTO.setIsDeleted(mallShoppingCartItem.getIsDeleted());
        mallShoppingCartItemDTO.setCreateTime(mallShoppingCartItem.getCreateTime());
        mallShoppingCartItemDTO.setUpdateTime(mallShoppingCartItem.getUpdateTime());
        return mallShoppingCartItemDTO;
    }

    private static MallShoppingCartItem dto2request(MallShoppingCartItemDTO dto) {
        if (dto == null) {
            return null;
        }
        MallShoppingCartItem mallShoppingCartItem = new MallShoppingCartItem();
        mallShoppingCartItem.setCartItemId(dto.getCartItemId());
        mallShoppingCartItem.setUserId(dto.getUserId());
        mallShoppingCartItem.setGoodsId(dto.getGoodsId());
        mallShoppingCartItem.setGoodsCount(dto.getGoodsCount());
        mallShoppingCartItem.setIsDeleted(dto.getIsDeleted());
        mallShoppingCartItem.setCreateTime(dto.getCreateTime());
        mallShoppingCartItem.setUpdateTime(dto.getUpdateTime());
        return mallShoppingCartItem;
    }

}
