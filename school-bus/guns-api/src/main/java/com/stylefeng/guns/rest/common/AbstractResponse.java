package com.stylefeng.guns.rest.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AbstractResponse implements Serializable {

    private static final long serivalVersionUID = 7642323132212L;

    @ApiModelProperty(notes = "状态码")
    private String code;

    @ApiModelProperty(notes = "状态消息")
    private String msg;
}
