package com.wbd.oauth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.wbd.oauth.dto.RolePermission;
@Mapper
public interface PermissionMapper {

	@Select("select r.name as roleName,p.url from role r,role_permission rp,permission p where r.id=rp.role_id and rp.permission_id=p.id")
	List<RolePermission> getRolePermissions();
}
