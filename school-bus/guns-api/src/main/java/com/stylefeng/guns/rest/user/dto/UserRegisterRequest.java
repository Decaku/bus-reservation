package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class UserRegisterRequest extends AbstractRequest {
    private String username;

    private String password;

    private String email;

    private String phone;
}
