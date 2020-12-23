package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCheckResponse extends AbstractResponse {

    @ApiModelProperty (notes = "0表示用户已存在， 1表示用户不存在")
    private int checkUsername; // 0表示存在，1表示不存在
}
