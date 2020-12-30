package com.stylefeng.guns.rest.modular.user;


import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
import com.stylefeng.guns.rest.modular.user.converter.UserConverter;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.tools.javac.util.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import com.stylefeng.guns.core.constants.SbCode;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserConverter userConverter;

    /**
     * 检查用户名是否已存在
     *
     * @param request
     * @return
     */
    @Override
    public UserCheckResponse checkUsername(UserCheckRequest request) {
        UserCheckResponse res = new UserCheckResponse();
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", request.getUsername()); // 拼接SQL "user_name=${request.getUsername()}"
            User user = userMapper.selectOne(queryWrapper); // 返回一条查询记录
            if (user != null) {
                res.setCheckUsername(0);
                res.setCode(SbCode.USERNAME_ALREADY_EXISTS.getCode());
                res.setMsg(SbCode.USERNAME_ALREADY_EXISTS.getMsg());
            } else {
                res.setCheckUsername(1);
                res.setCode(SbCode.USERNAME_ALREADY_NO_EXISTS.getCode());
                res.setMsg(SbCode.USERNAME_ALREADY_NO_EXISTS.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("checkUsername failed, err = " + e.toString());
        }
        return res;
    }

    /***
     * 注册业务逻辑
     * @param request
     * @return
     */
    @Override
    public UserRegisterResponse register(UserRegisterRequest request) {
        UserRegisterResponse res = new UserRegisterResponse();
        User user = userConverter.res2User(request);
        Date date = new Date();
        Instant instant = date.toInstant();

        // 系统默认的时区
        ZoneId zoneId = ZoneId.systemDefault();

        user.setBeginTime(LocalDateTime.ofInstant(instant, zoneId));
        user.setMoney(0.0);
        user.setPayPassword("");

        // 加密
        String md5Password = MD5Util.encrypt(user.getUserPwd());
        user.setUserPwd(md5Password);

        try {
            userMapper.insert(user);
            res.setRegister(true);
            res.setCode(SbCode.SUCCESS.getCode());
            res.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            res.setRegister(false);
            res.setCode(SbCode.USER_REGISTER_VERIFY_FAILED.getCode());
            res.setMsg(SbCode.USER_REGISTER_VERIFY_FAILED.getMsg());
            log.error("register failed, err = " + e.toString());
            return res;
        }
        return res;
    }

    /**
     * 登陆业务逻辑
     *
     * @param request
     * @return
     */
    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        UserLoginResponse res = new UserLoginResponse();
        res.setUserId(0L);
        res.setCode(SbCode.USERORPASSWORD_ERRROR.getCode());
        res.setMsg(SbCode.USERORPASSWORD_ERRROR.getMsg());
        try {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", request.getUsername());
            User user = userMapper.selectOne(queryWrapper);
            if (user != null && user.getUuid() > 0) {
                String md5Password = MD5Util.encrypt(request.getPassword());
                if (user.getUserPwd().equals(md5Password)) {
                    res.setUserId(user.getUuid());
                    res.setCode(SbCode.SUCCESS.getCode());
                    res.setMsg(SbCode.SUCCESS.getMsg());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("login failed, err = " + e.toString());
            return res;
            // 在auth那里设置了异常
        }
        return res;
    }

    /**
     * 通过id获取用户信息
     *
     * @param request
     * @return
     */
    @Override
    public UserResponse getUserById(UserRequest request) {
        UserResponse res = new UserResponse();
        try {
            User user = userMapper.selectById(request.getId());
            UserDto userDto = userConverter.User2Res(user);
            System.out.println(userDto);
            res.setUserDto(userDto);
            res.setCode(SbCode.SUCCESS.getCode());
            res.setMsg(SbCode.SUCCESS.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(SbCode.DB_EXCEPTION.getCode());
            res.setMsg(SbCode.DB_EXCEPTION.getMsg());
            log.error("getUserById failed, err = " + e.toString());
            return res;
        }
        return res;
    }

    /**
     * 更新用户信息
     *
     * @param request
     * @return
     */
    @Override
    public UserResponse updateUserInfo(UserUpdateInfoRequest request) {
        UserResponse res = new UserResponse();
        User user = userConverter.res2User(request);

        try {
            // 不改变密码
            // 支付密码暂时用明文
            Integer integer = userMapper.updateById(user);

            if (integer == 0) {
                res.setCode(SbCode.USER_INFOR_INVALID.getCode());
                res.setMsg(SbCode.USER_INFOR_INVALID.getMsg());
            } else {
                User user1 = userMapper.selectById(user.getUuid());
                res.setUserDto(userConverter.User2Res(user1));
                res.setCode(SbCode.SUCCESS.getCode());
                res.setMsg(SbCode.SUCCESS.getMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(SbCode.DB_EXCEPTION.getCode());
            res.setMsg(SbCode.DB_EXCEPTION.getMsg());
            log.error("updateUserInfo failed, err = " + e.toString());
            return res;
        }
        return res; //
    }
}
