package com.wbd.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class OauthBootApplication {


	public static void main(String[] args) {
		SpringApplication.run(OauthBootApplication.class, args);
		
	  
	}
	
	
}
