package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class UserCheckRequest extends AbstractRequest {

    private String username;
}
