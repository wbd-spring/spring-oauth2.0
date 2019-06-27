package com.wbd.oauth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wbd.oauth.jedis.JedisClient;
import com.wbd.oauth.jedis.JedisClientPoolImpl;

import redis.clients.jedis.JedisPool;

@Configuration
public class JedisConfig {
	
	@Value("${spring.redis.host}")
	private String host;
	
	@Value("${spring.redis.port}")
	private Integer port;
	
	@Bean
    public JedisClient jedisClientPool() {
		JedisClientPoolImpl jedisClientPool = new JedisClientPoolImpl();
        jedisClientPool.setJedisPool(jedisPool());

        return jedisClientPool;
    }

	 @Bean
	    public JedisPool jedisPool() {
	        return new JedisPool(host,port);
	    }
}
