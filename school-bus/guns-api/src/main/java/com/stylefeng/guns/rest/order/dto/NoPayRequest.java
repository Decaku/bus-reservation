package com.stylefeng.guns.rest.order.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class NoPayRequest extends AbstractRequest {
    private Long userId;

    // 当前页
    private Long currentPage;

    // 每页数量
    private Long pageSize;
}
