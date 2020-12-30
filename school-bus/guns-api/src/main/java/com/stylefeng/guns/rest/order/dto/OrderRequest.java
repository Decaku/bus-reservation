package com.stylefeng.guns.rest.order.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class OrderRequest extends AbstractRequest {
    private Long uuid;

    private String orderStatus;
}
