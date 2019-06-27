package com.wbd.oauth.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myredis")
public class PropertiesUtils {

	private String oauthstate;

	public String getOauthstate() {
		return oauthstate;
	}

	public void setOauthstate(String oauthstate) {
		this.oauthstate = oauthstate;
	}

	
	

	

	
}
