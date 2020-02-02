package com.yasinmall.util;

import com.yasinmall.common.RedisShardedPool;
import org.junit.Test;
import redis.clients.jedis.ShardedJedis;

/**
 * @author Yasin Zhang
 */
public class RedisPoolUtilTest {

    @Test
    public void test() {
        ShardedJedis jedis = RedisShardedPool.getJedis();

        RedisShardedPoolUtil.set("keyTest", "value");

        String value = RedisShardedPoolUtil.get("keyTest");
        System.out.println(value);

        RedisShardedPoolUtil.setEx("keyEx", "valueEx", 60*10);

        RedisShardedPoolUtil.expire("keyTest", 60*20);

        RedisShardedPoolUtil.del("keyTest");
        RedisShardedPoolUtil.del("keyEx");

        jedis.close();
    }
}
