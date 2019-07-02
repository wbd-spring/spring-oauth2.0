package com.wbd.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
	}
}
