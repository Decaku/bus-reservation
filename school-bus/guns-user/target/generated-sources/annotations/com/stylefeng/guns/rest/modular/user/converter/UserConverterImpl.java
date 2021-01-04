package com.stylefeng.guns.rest.modular.user.converter;

import com.stylefeng.guns.rest.common.convert.DateMapper;
import com.stylefeng.guns.rest.common.persistence.model.User;
import com.stylefeng.guns.rest.user.dto.UserDto;
import com.stylefeng.guns.rest.user.dto.UserRegisterRequest;
import com.stylefeng.guns.rest.user.dto.UserUpdateInfoRequest;
import javax.annotation.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-01-04T16:10:05+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_275 (AdoptOpenJDK)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Autowired
    private DateMapper dateMapper;

    @Override
    public User res2User(UserRegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUserPwd( request.getPassword() );
        user.setUserName( request.getUsername() );
        user.setUserPhone( request.getPhone() );
        user.setEmail( request.getEmail() );

        return user;
    }

    @Override
    public UserDto User2Res(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setMoney( user.getMoney() );
        userDto.setUuid( user.getUuid() );
        userDto.setUserName( user.getUserName() );
        userDto.setNickName( user.getNickName() );
        userDto.setUserSex( user.getUserSex() );
        userDto.setEmail( user.getEmail() );
        userDto.setUserPhone( user.getUserPhone() );
        userDto.setPayPassword( user.getPayPassword() );
        userDto.setBeginTime( dateMapper.asString( user.getBeginTime() ) );
        userDto.setUpdateTime( dateMapper.asString( user.getUpdateTime() ) );

        return userDto;
    }

    @Override
    public User res2User(UserUpdateInfoRequest request) {
        if ( request == null ) {
            return null;
        }

        User user = new User();

        user.setUuid( request.getId() );
        user.setNickName( request.getNickName() );
        user.setUserSex( request.getUserSex() );
        user.setEmail( request.getEmail() );
        user.setUserPhone( request.getUserPhone() );
        user.setMoney( request.getMoney() );
        user.setPayPassword( request.getPayPassword() );

        return user;
    }
}
