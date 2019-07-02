package com.wbd.oauth.dto;

import java.io.Serializable;

/**
 * 角色对应的权限
 * 
 * @author jwh
 *
 */
public class RolePermission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;

	private String roleName;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}



}
