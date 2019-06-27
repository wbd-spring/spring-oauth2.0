package com.wbd.oauth.jedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.wbd.oauth.utils.RandomUtils;

@Service
public class OauthRedisService {

	@Autowired
    private JedisClient jedisClient;

    @Value("${myredis.oauthstate}")
    public String OAUTH_STATE_KEY;

    /**
     * 生成并保存state入缓存
     * @author 
     * @since 2018/5/22 20:57
     */
    public String genState() {
        String state = RandomUtils.time();
        // 保证生成的state未存在于redis中
        while(jedisClient.sismember(OAUTH_STATE_KEY, state)) {
            state = RandomUtils.time();
        }

        // 保存state
        jedisClient.sadd(OAUTH_STATE_KEY, state);

       
        return state;
    }

    /**
     * 校验state
     * @author 
     * @since 2018/5/22 20:58
     */
    public Boolean checkState(String state) {
        Boolean flag = jedisClient.sismember(OAUTH_STATE_KEY, state);
        
        // 如果不存在，代表state非法；否则合法，并将其从缓存中删除
        if(!flag) {
            return false;
        } else {
            jedisClient.srem(OAUTH_STATE_KEY, state);
            return true;
        }
    }
}
