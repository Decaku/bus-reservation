package com.stylefeng.guns.rest.alipay.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class PayRequest extends AbstractRequest {
    private Long userId;

    private String payPassword;

    private Double totalMoney; // 直接传钱， 感觉不安全
}
