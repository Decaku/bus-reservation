package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class UserRegisterRequest extends AbstractRequest {
    private Long id;

    private String nickName;

    private Integer userSex;

    private String email;

    private String userPhone;

    private Double money;

    private String payPassword;
}
