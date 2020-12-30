package com.stylefeng.guns.rest.order.dto;

import com.stylefeng.guns.rest.common.AbstractResponse;
import lombok.Data;

@Data
public class OrderResponse extends AbstractResponse {
    private OrderDto orderDto;
}
