package com.stylefeng.guns.rest.user.dto;

import com.stylefeng.guns.rest.common.AbstractRequest;
import lombok.Data;

@Data
public class UserRequest extends AbstractRequest {

    private Long id; //通过id获取

    private String userName;

    private String userPwd;
}
