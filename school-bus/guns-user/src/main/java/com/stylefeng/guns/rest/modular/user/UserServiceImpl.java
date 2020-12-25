package com.stylefeng.guns.rest.modular.user;


import com.stylefeng.guns.rest.common.persistence.dao.UserMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
import com.stylefeng.guns.rest.user.IUserService;
import com.stylefeng.guns.rest.user.dto.UserCheckRequest;
import com.stylefeng.guns.rest.user.dto.UserCheckResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.stylefeng.guns.core.constants.SbCode;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     *  检查用户名是否已存在
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
             if(user != null) {
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



}
