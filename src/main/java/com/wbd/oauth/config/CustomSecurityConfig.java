package com.wbd.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import com.wbd.oauth.service.CustomerUserDetailsService;

@Configuration
@EnableWebSecurity
public class CustomSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomerUserDetailsService cuds;

	//用户来源和密码验证。
	//创建用户， 用户可以是基于内存的， 也可以基于数据库的（数据库设计中已经自己建立了，用户，角色，与权限对应表）， 我们采用基于数据库的方式，密码采用MD5加密
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//验证用户，可以是内存用户， 也可以是数据库用户
		auth.userDetailsService(cuds).passwordEncoder(new PasswordEncoder() {
			// 对密码进行加密
			@Override
			public String encode(CharSequence rawPassword) {
				System.out.println(rawPassword.toString());
				return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
			}

			@Override
			public boolean matches(CharSequence rawPassword, String encodedPassword) {
				String rawToMd5 = DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());

				return rawToMd5.equals(encodedPassword) ? true : false;
			}

		});
		
		//基于内存的  使用用户名"user"和密码“password”,角色"ROLE_USER"来认证
		
//		 auth
//         .inMemoryAuthentication()
//              .withUser("user")
//                   .password("password")
//                   .roles("USER");
		
		// 配置基于openId的认证方式 basic示例，不使用attribute exchange

//		 auth
//         .inMemoryAuthentication()
//             // the username must match the OpenID of the user you are
//             // logging in with
//             .withUser("https://www.google.com/accounts/o8/id?id=lmkCn9xzPdsxVwG7pjYMuDgNNdASFmobNkcRPaWU")
//                 .password("password")
//                 .roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.requestMatchers().antMatchers("/oauth/**","/login","/login/error")
		.and().authorizeRequests().antMatchers("/oauth/**").authenticated()
		.and().formLogin().loginPage("/login").failureUrl("/login-error");
	}
	
	
}
