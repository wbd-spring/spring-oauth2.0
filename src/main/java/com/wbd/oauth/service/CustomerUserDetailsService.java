package com.wbd.oauth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wbd.oauth.dto.Role;
import com.wbd.oauth.dto.User;
import com.wbd.oauth.mapper.RoleMapper;
import com.wbd.oauth.mapper.UserMapper;
@Service
public class CustomerUserDetailsService implements UserDetailsService{

	@Autowired
	private UserMapper um;
	
	@Autowired
	private RoleMapper rm;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//查询数据库
		
		User user = um.loadUserByUsername(username);
		
		if(user!=null) {
			
			List<Role> roles = 	rm.getRolesByUserId(user.getId());
		    user.setAuthorities(roles);
		}
		
		return user;
	}

}
