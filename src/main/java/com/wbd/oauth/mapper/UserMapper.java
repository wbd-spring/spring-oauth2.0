package com.wbd.oauth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.wbd.oauth.dto.User;

@Mapper
public interface UserMapper {

	@Select("select id,username,password from user where username=#{username}")
	User loadUserByUsername(String username);

}
