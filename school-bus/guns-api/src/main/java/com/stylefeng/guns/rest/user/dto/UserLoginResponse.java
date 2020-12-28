package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractResponse;
import lombok.Data;

@Data
public class UserLoginResponse extends AbstractResponse {
    private String randomKey;

    private String token;

    private Long userId;
}
