package com.yasinmall.util;

import com.yasinmall.common.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

/**
 * @author Yasin Zhang
 */
public class RedisPoolUtilTest {

    @Test
    public void test() {
        Jedis jedis = RedisPool.getJedis();

        RedisPoolUtil.set("keyTest", "value");

        String value = RedisPoolUtil.get("keyTest");
        System.out.println(value);

        RedisPoolUtil.setEx("keyEx", "valueEx", 60*10);

        RedisPoolUtil.expire("keyTest", 60*20);

        RedisPoolUtil.del("keyTest");
        RedisPoolUtil.del("keyEx");

        jedis.close();
    }
}
