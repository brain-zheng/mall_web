package com.mall.web.manager;

import com.mall.common.service.enums.ServiceResultEnum;
import com.mall.common.service.util.BeanUtil;
import com.mall.common.service.util.NumberUtil;
import com.mall.common.service.util.PageQueryUtil;
import com.mall.common.service.util.PageResult;
import com.mall.goodscenter.client.dto.MallGoodsInfoDTO;
import com.mall.goodscenter.client.dto.StockNumDTO;
import com.mall.goodscenter.client.service.MallGoodsInfoService;
import com.mall.ordercenter.client.dto.MallOrderDTO;
import com.mall.ordercenter.client.dto.MallOrderItemDTO;
import com.mall.ordercenter.client.dto.QueryOrderDTO;
import com.mall.ordercenter.client.service.MallOrderItemService;
import com.mall.ordercenter.client.service.MallOrderService;
import com.mall.ordercenter.client.service.ShoppingCartService;
import com.mall.web.common.Constants;
import com.mall.web.common.MallException;
import com.mall.web.enums.MallOrderStatusEnum;
import com.mall.web.enums.PayStatusEnum;
import com.mall.web.enums.PayTypeEnum;
import com.mall.web.response.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Component
public class MallOrderManager {

    @Resource
    private MallGoodsInfoService goodsInfoService;

    @Resource
    private ShoppingCartService shoppingCartService;

    @Resource
    private MallOrderService mallOrderService;

    @Resource
    private MallOrderItemService mallOrderItemService;


    public MallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Integer userId) {
        MallOrderDTO mallOrder = mallOrderService.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<MallOrderItemDTO> orderItems = mallOrderItemService.selectByOrderId(mallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<MallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItems, MallOrderItemVO.class);
                MallOrderDetailVO mallOrderDetailVO = new MallOrderDetailVO();
                BeanUtil.copyProperties(mallOrder, mallOrderDetailVO);
                mallOrderDetailVO.setOrderStatusString(MallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(mallOrderDetailVO.getOrderStatus()).getName());
                mallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(mallOrderDetailVO.getPayType()).getName());
                mallOrderDetailVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                return mallOrderDetailVO;
            }
        }
        return null;
    }

    public PageResult getMyOrders(PageQueryUtil pageUtil, Map<String, Object> params) {
        QueryOrderDTO queryOrderDTO = params2queryDTO(pageUtil, params);
        int total = mallOrderService.getTotalNewBeeMallOrders(queryOrderDTO);
        List<MallOrderDTO> mallOrders = mallOrderService.findNewBeeMallOrderList(queryOrderDTO);
        List<MallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(mallOrders, MallOrderListVO.class);
            //设置订单状态中文显示值
            for (MallOrderListVO mallOrderListVO : orderListVOS) {
                mallOrderListVO.setOrderStatusString(MallOrderStatusEnum.getNewBeeMallOrderStatusEnumByStatus(mallOrderListVO.getOrderStatus()).getName());
            }
            List<Integer> orderIds = mallOrders.stream().map(MallOrderDTO::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<MallOrderItemDTO> orderItems = mallOrderItemService.selectByOrderIds(orderIds);
                Map<Integer, List<MallOrderItemDTO>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(MallOrderItemDTO::getOrderId));
                for (MallOrderListVO newBeeMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(newBeeMallOrderListVO.getOrderId())) {
                        List<MallOrderItemDTO> orderItemListTemp = itemByOrderIdMap.get(newBeeMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<MallOrderItemVO> newBeeMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, MallOrderItemVO.class);
                        newBeeMallOrderListVO.setNewBeeMallOrderItemVOS(newBeeMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    private QueryOrderDTO params2queryDTO(PageQueryUtil pageUtil, Map<String, Object> params) {
        QueryOrderDTO queryOrderDTO = new QueryOrderDTO();
        queryOrderDTO.setLimit(pageUtil.getLimit());
        queryOrderDTO.setStart(pageUtil.getStart());
        if (params.containsKey("orderNo") && !StringUtils.isEmpty(params.get("orderNo") + "")) {
            queryOrderDTO.setOrderNo(params.get("orderNo") + "");
        }
        if (params.containsKey("userId")) {
            queryOrderDTO.setUserId(Integer.valueOf(params.get("userId") + ""));
        }
        if (params.containsKey("payType")) {
            queryOrderDTO.setPayType(Integer.valueOf(params.get("payType") + ""));
        }
        if (params.containsKey("orderStatus")) {
            queryOrderDTO.setOrderStatus(Integer.valueOf(params.get("orderStatus") + ""));
        }
        if (params.containsKey("isDeleted")) {
            queryOrderDTO.setIsDeleted(Integer.valueOf(params.get("isDeleted") + ""));
        }
        if (params.containsKey("startTime")) {
            queryOrderDTO.setStartTime(Date.valueOf(params.get("startTime") + ""));
        }
        if (params.containsKey("endTime")) {
            queryOrderDTO.setEndTime(Date.valueOf(params.get("endTime") + ""));
        }
        return queryOrderDTO;
    }

    @Transactional
    public String saveOrder(MallUserVO user, List<MallShoppingCartItemVO> myShoppingCartItems) {
        List<Integer> itemIdList = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Integer> goodsIds = myShoppingCartItems.stream().map(MallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<MallGoodsInfoDTO> mallGoods = goodsIds.stream().map(goodsInfoService::getById).collect(Collectors.toList());
        //检查是否包含已下架商品
        List<MallGoodsInfoDTO> goodsListNotSelling = mallGoods.stream()
                .filter(mallGoodsTemp -> mallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            MallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Integer, MallGoodsInfoDTO> mallGoodsMap = mallGoods.stream().collect(Collectors.toMap(MallGoodsInfoDTO::getId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (MallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!mallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > mallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(mallGoods)) {
            if (shoppingCartService.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = goodsInfoService.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    MallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                MallOrderDTO mallOrder = new MallOrderDTO();
                mallOrder.setOrderNo(orderNo);
                mallOrder.setUserId(user.getUserId());
                mallOrder.setUserAddress(user.getAddress());
                //总价
                for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += mallShoppingCartItemVO.getGoodsCount() * mallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                mallOrder.setTotalPrice(priceTotal);
                String extraInfo = "";
                mallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (mallOrderService.insertSelective(mallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<MallOrderItemDTO> mallOrderItems = new ArrayList<>();
                    for (MallShoppingCartItemVO mallShoppingCartItemVO : myShoppingCartItems) {
                        MallOrderItemDTO mallOrderItem = new MallOrderItemDTO();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(mallShoppingCartItemVO, mallOrderItem);
                        //mallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        mallOrderItem.setOrderId(mallOrder.getOrderId());
                        mallOrderItems.add(mallOrderItem);
                    }
                    //保存至数据库
                    if (mallOrderItemService.insertBatch(mallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    MallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            MallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        MallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    public String cancelOrder(String orderNo, Integer userId){
        MallOrderDTO mallOrder = mallOrderService.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (mallOrderService.closeOrder(Collections.singletonList(mallOrder.getOrderId()), MallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    public String finishOrder(String orderNo, Integer userId){
        MallOrderDTO mallOrder = mallOrderService.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            mallOrder.setOrderStatus(MallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            mallOrder.setUpdateTime(new java.util.Date());
            if (mallOrderService.updateByPrimaryKeySelective(mallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    public MallOrderDTO getMallOrderByOrderNo(String orderNo){
        return mallOrderService.selectByOrderNo(orderNo);
    }

    public String paySuccess(String orderNo, int payType){
        MallOrderDTO mallOrder = mallOrderService.selectByOrderNo(orderNo);
        if (mallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            mallOrder.setOrderStatus(MallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            mallOrder.setPayType(payType);
            mallOrder.setPayStatus(PayStatusEnum.PAY_SUCCESS.getPayStatus());
            mallOrder.setPayTime(new java.util.Date());
            mallOrder.setUpdateTime(new java.util.Date());
            if (mallOrderService.updateByPrimaryKeySelective(mallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }


}
