package com.wbd.oauth.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import com.wbd.oauth.dto.RolePermission;
import com.wbd.oauth.mapper.PermissionMapper;

/**
 * 获取请求的url对应的权限
 * 
 * @author jwh
 *
 */
@Component
public class CustomFilterInvocaitonSecurityMetedaSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	private PermissionMapper pm;

	// 每一个资源(url)所需要的角色 ,该属性提供给决策器使用

	private static Map<String, Collection<ConfigAttribute>> map;

	/**
	 * 返回请求资源需要的角色
	 */
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		// object中包含request的信息

		
		
		HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();

		for (Iterator<String> it = map.keySet().iterator() ; it.hasNext();) {
            String url = it.next();
        
            if (new AntPathRequestMatcher( url ).matches( request )) {
                return map.get( url );
            }
            
		}

		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		loadResourceDefine();
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

	// 初始化数据库中的所有资源(url)对应的角色
	// 存入map中，形式如：key=url value=list(role1,role2,role3)

	private void loadResourceDefine() {

		map = new HashMap<String, Collection<ConfigAttribute>>();

		// 查询数据库
		List<RolePermission> rpList = pm.getRolePermissions();
		
		System.out.println("rpList==="+rpList.size());
		
		for (RolePermission rolePermisson : rpList) {

            String url = rolePermisson.getUrl();
            String roleName = rolePermisson.getRoleName();
            System.out.println("roleName=="+roleName);
            ConfigAttribute role = new SecurityConfig(roleName);

            if(map.containsKey(url)){
                map.get(url).add(role);
            }else{
                List<ConfigAttribute> list =  new ArrayList<>();
                list.add( role );
                map.put( url , list );
            }
        }
		
//		rpList.forEach(rolePermission -> {
//			String url = rolePermission.getUrl();
//			String roleName = rolePermission.getRoleName();
//			ConfigAttribute configAttribute = new SecurityConfig(roleName);
//			if (map.containsKey(url)) {
//				map.get(url).add(configAttribute);
//			} else {
//				List<ConfigAttribute> caList = new ArrayList<ConfigAttribute>();
//				caList.add(configAttribute);
//				map.put(url, caList);
//			}
//		});
	}
}
