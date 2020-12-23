package com.stylefeng.guns.rest.modular.user;


import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.UserCheckRequest;
import com.stylefeng.guns.rest.user.dto.UserCheckResponse;

public class UserServiceImpl implements IUserService {

    /**
     *  检查用户名是否已存在
     * @param request
     * @return
     */
    @Override
    public UserCheckResponse checkUsername(UserCheckRequest request) {
         UserCheckResponse res = new UserCheckResponse();
         return res;
    }

}
