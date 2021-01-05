package com.stylefeng.guns.rest.modular.user;

import cn.hutool.core.convert.Convert;
import com.stylefeng.guns.core.constants.RedisConstants;
import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.common.RedisUtils;
import com.stylefeng.guns.rest.common.ResponseData;
import com.stylefeng.guns.rest.common.ResponseUtil;
import com.stylefeng.guns.rest.exception.CommonResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.form.UserUpdateForm;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;
import com.stylefeng.guns.rest.modular.form.UserRegisterForm;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;


@Slf4j
@Api(value = "用户服务", description = "用户服务相关接口")
@RestController
@RequestMapping("/user/")

public class UserController {

    @Reference(check = false)
    private IUserService userAPI;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    /**
     * 检查用户名接口
     * @param username
     * @return
     */
    @ApiOperation(value = "检查用户名接口", notes = "给定用户名 查询用户是否存在", response = UserCheckResponse.class)
    @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String", paramType = "query")
    @GetMapping("check")
    public ResponseData checkUsername(String username) {
        try {
            if(username.equals("")) {
                return new ResponseUtil<>().setErrorMsg("用户名不能为空");
            }
            UserCheckRequest req = new UserCheckRequest();
            req.setUsername(username);
            UserCheckResponse res = userAPI.checkUsername(req);
            log.info("ResponseData res = " + res.toString());
            return new ResponseUtil<>().setData(res);
        } catch (Exception e) {
            log.error("checkUsername failed, err = " + e.toString());
            CommonResponse response = new CommonResponse();
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return new ResponseUtil().setData(response);
        }
    }


    /**
     * 注册接口
     * @param form
     * @param bindingResult
     * @return
     */
    @ApiOperation(value = "注册接口", notes = "用户注册相关信息", response = UserRegisterResponse.class)
    @PostMapping("register")
    public ResponseData register(@Validated UserRegisterForm form, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                CommonResponse response = new CommonResponse();
                response.setCode(SbCode.REQUISITE_PARAMETER_NOT_EXIST.getCode());
                response.setCode(SbCode.REQUISITE_PARAMETER_NOT_EXIST.getMsg());
                return new ResponseUtil<>().setData(response);
            }
            UserRegisterRequest request = new UserRegisterRequest();
            request.setUsername(form.getUsername());
            request.setPassword(form.getPassword());
            request.setPhone(form.getPhone());
            request.setEmail(form.getEmail());
            // 不想写那么多了
            log.info("user register request = " + request);
            UserRegisterResponse res = userAPI.register(request);
            return new ResponseUtil<>().setData(res);
        } catch (Exception e) {
            log.error("register failed, err = " + e.toString());
            CommonResponse response = new CommonResponse();
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return new ResponseUtil().setData(response);
        }
    }

    @ApiOperation(value = "获取用户信息接口", notes = "获取用户相关信息，前提请获取用户token放在headers中", response = UserResponse.class)
    @GetMapping("getUserInfo")
    public ResponseData getUserById(HttpServletRequest req) {
        // 从本地缓存中取
        try {
            String token = CurrentUser.getToken(req);
            String userId = jwtTokenUtil.getUsernameFromToken(token);
            String key = RedisConstants.USER_INFO_EXPIRE.getKey()+userId;
            log.info("getUserById key = " + key);
            if (redisUtils.hasKey(key)) {
                Object obj = redisUtils.get(key);
                log.info("user info from redis is" + obj.toString());
                return new ResponseUtil<>().setData(obj);
            }
            UserRequest request = new UserRequest();
            request.setId(Convert.toLong(userId));
            UserResponse response = userAPI.getUserById(request);
            redisUtils.set(key, response, RedisConstants.USER_INFO_EXPIRE.getTime());
            log.info("getUserById\n");
            return new ResponseUtil<>().setData(response);
        } catch (Exception e) {
            log.error("getUserById\n", e);
            CommonResponse response = new CommonResponse();
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return new ResponseUtil().setData(response);
        }
    }

    /*
    永远不会过期的token...
    eyJhbGciOiJIUzUxMiJ9.eyJyYW5kb21LZXkiOiJ6cHNnMWMiLCJzdWIiOiIxMzQ2Mzc1ODgyNjkzNjc3MDU4IiwiZXhwIjoxNjEwNDQwOTQ1LCJpYXQiOjE2MDk4MzYxNDV9.3qOWb1oWdHTEc1xgXQV8ZIqKfo3BIOCch30KiqCu0XpaKD2JH15gx8MbMRiRfdCbE2qWEprEpuJW5m2K5Iusrg
     */


    /**
     * 更新接口
     * @param form
     * @param req
     * @return
     */
    @ApiOperation(value = "更新接口", notes = "更新用户相关信息", response = UserResponse.class)
    @PostMapping("updateInfo")
    @ApiImplicitParam(name = "form", value = "用户更新表", required = true)
    public ResponseData updateUserInfo(@RequestBody UserUpdateForm form, HttpServletRequest req) {
        // id 从本队缓存中取
        try {
            String token = CurrentUser.getToken(req);
            String userId = jwtTokenUtil.getUsernameFromToken(token);
            String key = RedisConstants.USER_INFO_EXPIRE.getKey()+userId;
            // 更新用户信息记得删缓存
            if (redisUtils.hasKey(key)) {
                redisUtils.del(key);
            }
            UserUpdateInfoRequest request = new UserUpdateInfoRequest();
            request.setUserSex(form.getUserSex());
            request.setNickName(form.getNickName());
            request.setEmail(form.getEmail());
            request.setUserPhone(form.getUserPhone());
            request.setMoney(form.getMoney());
            request.setPayPassword(form.getPayPassword());
            request.setId(Convert.toLong(userId));
            UserResponse response = userAPI.updateUserInfo(request);
            log.info("updateUserInfo\n");
            return new ResponseUtil<>().setData(response);
        } catch (Exception e) {
            log.error("updateUserInfo\n", e);
            CommonResponse response = new CommonResponse();
            response.setCode(SbCode.SYSTEM_ERROR.getCode());
            response.setMsg(SbCode.SYSTEM_ERROR.getMsg());
            return new ResponseUtil().setData(response);
        }
    }


    /**
     * 登出接口
     * @param req
     * @return
     */
    @ApiOperation(value = "登出接口", notes = "用户登出，暂时是前端删除token", response = CommonResponse.class)
    @GetMapping("logout")
    public ResponseData logout(HttpServletRequest req) {
        /*
            应用：
                1、前端存储JWT 【七天】 ： JWT的刷新
                2、服务器端会存储活动用户信息【30分钟】
                3、JWT里的userId为key，查找活跃用户
            退出：
                1、前端删除掉JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉JWT
         */
        String token = CurrentUser.getToken(req);
        String userId = jwtTokenUtil.getUsernameFromToken(token);
//        CurrentUser.deleteUserId(userId);
        // 登出删除缓存即可
        redisUtils.del(userId);
        CommonResponse response = new CommonResponse();
        response.setCode(SbCode.SUCCESS.getCode());
        response.setMsg(SbCode.SUCCESS.getMsg());
        log.info("logout", response.toString());
        return new ResponseUtil<>().setData(response);
    }
}
