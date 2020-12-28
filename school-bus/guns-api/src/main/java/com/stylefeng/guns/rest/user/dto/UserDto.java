package com.stylefeng.guns.rest.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "用户表现实体")
public class UserDto implements Serializable {
    @ApiModelProperty(notes = "用户id 自增")
    private Long uuid;

    @ApiModelProperty(notes = "用户名")
    private String userName;

    @ApiModelProperty(notes = "用户昵称")
    private String nickName;

    @ApiModelProperty(notes = "用户性别 0表示男 1表示女")
    private Integer UserSex;

    @ApiModelProperty(notes = "用户邮件")
    private String email;

    @ApiModelProperty(notes = "用户手机号码")
    private String userPhone;

    @ApiModelProperty(notes = "用户余额")
    private Double money;

    @ApiModelProperty(notes = "支付密码")
    private String payPassword; //没有加密

    @ApiModelProperty(notes = "记录创建时间")
    private String beginTime;

    @ApiModelProperty(notes = "记录更新时间")
    private String updateTime;
}
