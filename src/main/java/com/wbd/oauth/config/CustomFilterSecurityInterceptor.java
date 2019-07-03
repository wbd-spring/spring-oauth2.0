package com.wbd.oauth.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

/**
 * 拦截器， 来过滤请求，
 * 注入，决策器和FilterInvocationSecurityMetadataSource
 * @author jwh
 *
 */
@Component
public class CustomFilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter{

	//注入 metedatasource
	@Autowired
	private FilterInvocationSecurityMetadataSource fisms;
	
	//注入决策器
	@Autowired
	public void setCustomAccessDecisionManager(CustomAccessDecisionManager  cadm) {
		System.out.println("依赖注入setter方法........");
		super.setAccessDecisionManager(cadm);
	}
	
	
	@Override
	public Class<?> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	
	
	//obtain获得
	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		
		return this.fisms;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		FilterInvocation fi = new FilterInvocation(request, response, chain);
		
		invoke(fi);
	}
	
	/**
	 * 
	 * FilterInvocation里面有一个被拦截的url
	 * 
	 * 里面调用FilterInvocationSercurityMetedatasource的getAttributes方法获取url对应的权限
	 * 在调用决策器的decide的方法来验证用户的权限是否足够
	 * 
	 * @param fi
	 */
	private void invoke(FilterInvocation fi) {
		
		//执行 FilterInvocationSercurityMetedatasource
		InterceptorStatusToken ist = super.beforeInvocation(fi);
		
		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getHttpResponse());
		} catch (IOException | ServletException e) {
		
			e.printStackTrace();
		}finally {
			//执行决策器
			System.out.println("执行决策器........");
			super.afterInvocation(ist, null);
		}
		
	}

}
