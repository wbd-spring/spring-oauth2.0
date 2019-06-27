package com.wbd.test;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wbd.oauth.OauthBootApplication;
import com.wbd.oauth.jedis.JedisClient;
import com.wbd.oauth.utils.PropertiesUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OauthBootApplication.class)
public class TestYml {

	/**
	 * 获取yml中的属性，有以下两种， 第二种更方便
	 */
	
	// 自定义的属性
	@Autowired
	private PropertiesUtils pu;

	// 系统自带属性
	@Value("${spring.redis.port}")
	private Integer port;

	// 自定义的属性
	@Value("${mygood.c}")
	private String c;
	
	
	@Autowired
	private JedisClient  jcc;

	@Test
	public void test() {
		System.out.println(pu.getOauthstate());
		System.out.println(port);
		System.out.println(c);
	}
	
	@Test
	public void testRedis() {
		
//		jcc.sadd("zgh", "567","123");
//		jcc.sadd("abc", "567","123");
		
		Set<String> s= jcc.smembers("zgh");
		
		s.stream().forEach(System.out::println);
	}

}
