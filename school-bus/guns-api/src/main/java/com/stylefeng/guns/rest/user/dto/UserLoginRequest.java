package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import com.stylefeng.guns.rest.common.AbstractResponse;
import lombok.Data;

@Data
public class UserLoginRequest extends AbstractRequest {
    private String username;

    private String password;
}
