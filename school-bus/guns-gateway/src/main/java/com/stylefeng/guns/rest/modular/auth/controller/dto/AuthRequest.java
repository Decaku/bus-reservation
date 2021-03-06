package com.stylefeng.guns.rest.modular.auth.controller.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "Auth请求实体", description = "Auth请求参数")
@Data
public class AuthRequest {

    @NotNull(message = "用户名不能为空")
    @ApiModelProperty(value = "用户名", example = "ferrari", required = true)
    private String username;

    @NotNull(message = "密码不能为空")
    @ApiModelProperty(value = "密码", example = "123", required = true)
    private String password;
}
