package com.stylefeng.guns.rest.modular.auth.controller;


import com.stylefeng.guns.core.constants.RedisConstants;
import com.stylefeng.guns.rest.common.CommonBindingResult;
import com.stylefeng.guns.rest.common.RedisUtils;
import com.stylefeng.guns.rest.common.ResponseData;
import com.stylefeng.guns.rest.common.ResponseUtil;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.UserLoginRequest;
import com.stylefeng.guns.rest.user.dto.UserLoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(value = "Auth服务", description = "获取权限相关接口")
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Reference(check = false)
    private IUserService userAPI;

    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "获取token接口", notes = "每调用一次，就会随机生成一串token", response = ResponseData.class)
    @RequestMapping(value = "${jwt.auth-path}")
    public ResponseData createAuthenticationToken(@Validated AuthRequest authRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error("参数：{}校验失败，原因：{}", fieldError.getField(), fieldError.getDefaultMessage());
            }
            return new ResponseUtil<>().setErrorMsg("用户参数设置错误:" + CommonBindingResult.getErrors(bindingResult));
        }
        UserLoginRequest req = new UserLoginRequest();
        req.setUsername(authRequest.getUsername());
        req.setPassword(authRequest.getPassword());
        UserLoginResponse res = userAPI.login(req);
        String userId = "" + res.getUserId();
        if (res.getUserId() != 0) {

            // 登陆的时候如果有缓存就删掉。（兜底逻辑）
            if (redisUtils.hasKey(userId)) {
                redisUtils.del(userId);
            }

            res.setRandomKey(jwtTokenUtil.getRandomKey());
            String token = jwtTokenUtil.generateToken(userId, res.getRandomKey());
            log.info("token = " + token);
            res.setToken(token);
            redisUtils.set(userId, token, RedisConstants.TOKEN_EXPIRE.getTime());

            return new ResponseUtil<>().setData(res);
        } else {
            return new ResponseUtil<>().setErrorMsg("账号密码错误");
        }
    }
}
