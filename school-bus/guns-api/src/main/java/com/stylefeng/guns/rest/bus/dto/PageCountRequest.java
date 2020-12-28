package com.stylefeng.guns.rest.bus.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class PageCountRequest extends AbstractRequest {
    // 当前页
    private Long currentPage;
    // 每页数量
    private Long pageSize;
    // 地区状态
    private String busStatus;
}
