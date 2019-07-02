package com.wbd.oauth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.wbd.oauth.dto.Role;


@Mapper
public interface RoleMapper {

	@Select("select id,name from role r,user_role ur where r.id=ur.role_id and ur.user_id=#{userId}")
	List<Role> getRolesByUserId(Long userId);
}
