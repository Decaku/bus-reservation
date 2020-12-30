package com.stylefeng.guns.rest.order.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class AddOrderRequest extends AbstractRequest {
    // 场次id
    private Long countId;

    // 用户id
    private Long userId;

    private String orderUser;

    private String busStatus;

    private String seatsIds;

    private Double countPrice;

    private Integer expireTime;
}
