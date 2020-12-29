package com.stylefeng.guns.rest.bus.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class CountDetailRequest extends AbstractRequest {
    // 场次id
    private Long countId;
}
