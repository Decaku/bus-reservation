package com.stylefeng.guns.rest.alipay;

import com.stylefeng.guns.rest.alipay.dto.PayBackRequest;
import com.stylefeng.guns.rest.alipay.dto.PayRequest;
import com.stylefeng.guns.rest.alipay.dto.PayResponse;

public interface IPayService {
    /**
     * 支付业务逻辑
     * @param requset
     * @return
     */
    PayResponse pay(PayRequest requset);

    /**
     * 退款业务逻辑
     * @param request
     * @return
     */
    PayResponse payBack(PayBackRequest request);
}
