package com.wbd.oauth.config;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.wbd.oauth.service.CustomerUserDetailsService;

/**
 * 授权/认证服务器
 * 
 * @author zgh
 *
 */
@Configuration
@EnableAuthorizationServer // 授权/认证服务器注解
public class CustomAuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

	// 注入权限验证器，来支持password grant type
	@Autowired
	private AuthenticationManager authenticationManager;

	// 注入userDetailsService 开启refresh_token需要用到
	@Autowired
	private CustomerUserDetailsService userDetailsService;

	/**
	 * 数据源
	 */
	@Autowired
	private DataSource dataSource;

	// 设置保存token机制，一共有5种， 比如内存，redis、数据库， 我们采用数据库存储方式
	@Autowired
	private TokenStore tokenStore;

	// 设置用数据库来存储token
	@Bean
	public TokenStore tokenStore() {

		return new JdbcTokenStore(dataSource);
	}

	@Autowired
	private WebResponseExceptionTranslator  webResponseExceptionTranslator;
	// 客户端信息从数据库中读取
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// 客户端信息从数据库中读取
		clients.jdbc(dataSource);
	}

	/**
	 * 配置oauth2服务跨域
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {

		CorsConfigurationSource source = new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
				CorsConfiguration cc = new CorsConfiguration();
				cc.addAllowedHeader("*");
				cc.addAllowedOrigin(request.getHeader(HttpHeaders.ORIGIN));
				cc.addAllowedMethod("*");
				cc.setAllowCredentials(true);
				cc.setMaxAge(3600l);
				return cc;
			}
		};
		
		security.tokenKeyAccess("permitAll()")
		.checkTokenAccess("permitAll()")
		.allowFormAuthenticationForClients()
		.addTokenEndpointAuthenticationFilter(new CorsFilter(source));

	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		//开启密码授权类型
		endpoints.authenticationManager(authenticationManager);
		//开启token存储方式
		endpoints.tokenStore(tokenStore);
		
		//自定义登录或者鉴权失败时返回的信息
		endpoints.exceptionTranslator(webResponseExceptionTranslator);
		//要使用refresh_token,需要额外配置userDetailsService
		endpoints.userDetailsService(userDetailsService);
	}
}
