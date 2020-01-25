package com.yasinmall.common;

import com.yasinmall.util.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.util.Hashing;
import redis.clients.jedis.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yasin Zhang
 */
public class RedisShardedPool {

    private static ShardedJedisPool pool;      // sharded jedis连接池

    // 最大连接数
    private static Integer maxTotal = PropertiesUtil.getIntProperty("redis.max.total", 20);
    // 最大空闲连接数
    private static Integer maxIdle = PropertiesUtil.getIntProperty("redis.max.idle", 10);
    // 最小空闲连接数
    private static Integer minIdle = PropertiesUtil.getIntProperty("redis.min.idle", 2);

    // 从jedis连接池中获取jedis实例是否进行验证操作, 保证获取的jedis实例的可用性
    private static Boolean testOnBorrow = PropertiesUtil.getBoolProperty("redis.test.borrow", true);
    // 在return一个jedis实例的时候，是否进行验证操作，若赋值为true，则返回jedisPool的实例可用
    private static Boolean testOnReturn = PropertiesUtil.getBoolProperty("redis.test.return", true);

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = PropertiesUtil.getIntProperty("redis1.port");
    private static String redis1Pass = PropertiesUtil.getProperty("redis1.password");

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = PropertiesUtil.getIntProperty("redis2.port");
    private static String redis2Pass = PropertiesUtil.getProperty("redis2.password");

    static {
        initPool();
    }

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);  //连接耗尽时是否阻塞，false则抛出异常，true阻塞直到超时。默认为true

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000*2, "redis1");
        info1.setPassword(redis1Pass);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000*2, "redis2");
        info2.setPassword(redis2Pass);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

}
