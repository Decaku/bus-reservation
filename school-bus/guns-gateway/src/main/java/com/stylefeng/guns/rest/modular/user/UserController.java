package com.stylefeng.guns.rest.modular.user;

import com.stylefeng.guns.core.constants.SbCode;
import com.stylefeng.guns.rest.common.ResponseData;
import com.stylefeng.guns.rest.common.ResponseUtil;
import com.stylefeng.guns.rest.exception.CommonResponse;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.UserCheckRequest;
import com.stylefeng.guns.rest.user.dto.UserCheckResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Api(value = "用户服务", description = "用户服务相关接口")
@RestController
@RequestMapping("/user/")

public class UserController {

    @Reference(check = false)
    private IUserService userAPI;


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

}
