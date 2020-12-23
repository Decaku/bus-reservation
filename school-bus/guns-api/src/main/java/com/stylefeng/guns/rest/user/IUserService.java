package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.dto.UserCheckRequest;
import com.stylefeng.guns.rest.user.dto.UserCheckResponse;

public interface IUserService {
    UserCheckResponse checkUsername(UserCheckRequest request);
}
