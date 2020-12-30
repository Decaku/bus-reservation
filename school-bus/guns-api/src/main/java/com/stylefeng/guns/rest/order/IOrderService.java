package com.stylefeng.guns.rest.order;

import com.stylefeng.guns.rest.order.dto.*;

public interface IOrderService {

    /**
     * 获取未乘坐订单
     * @param request
     * @return
     */
    NoTakeBusResponse getNoTakeOrdersById(NoTakeBusRequest request);


    /**
     * 根据评价状态获取订单
     * @param request
     * @return
     */
    EvaluateResponse getEvaluateOrdersById(EvaluateRequest request);


    /**
     * 获取待支付订单
     * @param request
     * @return
     */
    NoPayResponse getNoPayOrdersById(NoPayRequest request);


    /**
     * 添加订单
     * @param request
     * @return
     */
    AddOrderResponse addOrder(AddOrderRequest request);


}
