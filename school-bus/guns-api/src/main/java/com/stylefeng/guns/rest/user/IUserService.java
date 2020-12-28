package com.stylefeng.guns.rest.user;

import com.stylefeng.guns.rest.user.dto.*;

public interface IUserService {
    /**
     * 检查用户名是否存在
     * @param request：username
     * @return
     */
    UserCheckResponse checkUsername(UserCheckRequest request);

    /**
     * 注册
     * @param request
     * @return
     */
    UserRegisterResponse register(UserRegisterRequest request);

    /**
     * 用户登陆
     * @param request
     * @return
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 通过id获取用户信息
     * @param request
     * @return
     */
    UserResponse getUserById(UserRequest request);

    /**
     * 更新用户信息
     * @param request
     * @return
     */
    UserResponse updateUserInfo(UserUpdateInfoRequest request);

}
