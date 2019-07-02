package com.wbd.oauth.config;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * 决策管理器
 * @author zgh
 *
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager{

	/**
	 * decide 方法是判定是否拥有权限的决策方法，
	 * 通过传递参数来决定用户是否有访问对应受保护对象的权限
	 * @param authentication 包含了当前用户信息，包括拥有的权限，这里的权限来自自定义登录时UserDetailsService实现类中设置的authorities
	 * 即是CustomerUserDetailsService中添加到GrantedAuthority对象中权限信息的集合
	 * @param object 包含客户端发起请求的request信息等web资源
	 * @param configAttributes 本次访问需要的权限，为FilterInvocationSecurityMetadataSource实现类getAttributes这个方法返回的结果，
	 * getAttributes方法是为了判断用户请求的url是否在权限列表中，如果在则返回给decide方法，用来判断用户是否有此权限
	 * @throws AccessDeniedException
	 * @throws InsufficientAuthenticationException
	 */
	@Override
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		
		//如果为空， 表示此次访问不需要任何权限， 过滤器放行，继续往下执行
		if(configAttributes==null || 0>=configAttributes.size()) {
		
			return ;
		}else {
			
			String needRole;
			for(Iterator<ConfigAttribute> it =configAttributes.iterator();it.hasNext();) {
				
				needRole = it.next().getAttribute();
				
				for(GrantedAuthority ga:authentication.getAuthorities()) {
					
					//如果存在， 就放行，
					if(needRole.trim().equals(ga.getAuthority().trim())) {
						return ;
					}
				}
			}
		}
		
		throw new AccessDeniedException("当前访问没有权限");
		
	}

	/**
	 * 表示accessDecisionManager是否能够处理
	 * 传递的ConfigAttribute呈现的授权请求
	 * @param attribute
	 * @return
	 */
	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	/**
	 * 表示当前AccessDecisionMananger实现是否能够为指定的安全对象
	 * 方法调用或者web请求提供访问控制决策
	 * @param clazz
	 * @return
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
