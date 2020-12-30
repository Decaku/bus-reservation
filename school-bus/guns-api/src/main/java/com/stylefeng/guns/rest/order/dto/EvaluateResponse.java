package com.stylefeng.guns.rest.order.dto;

import com.stylefeng.guns.rest.common.AbstractResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("订单评价列表Dto")
public class EvaluateResponse extends AbstractResponse {
    // 当前页
    @ApiModelProperty(notes = "当前页")
    private Long currentPage;

    // 每页数量
    @ApiModelProperty(notes = "每页数量")
    private Long pageSize;

    // 总量
    @ApiModelProperty(notes = "总量")
    private Long total;

    // 总页
    @ApiModelProperty(notes = "总页")
    private Long pages;

    @ApiModelProperty(notes = "订单列表")
    List<EvaluateDto> evaluateDtos;
}
