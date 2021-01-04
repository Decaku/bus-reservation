package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
//import org.mapstruct.Mapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表的mapper接口
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
