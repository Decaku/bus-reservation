package com.stylefeng.guns.rest.alipay.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class PayBackRequest extends AbstractRequest {
    private Long orderId;

    private Long coundId;

    private Long userId;

    private String seatsIds;

    private Double totalMoney;
}
