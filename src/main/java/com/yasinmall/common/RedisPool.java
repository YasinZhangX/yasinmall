package com.yasinmall.common;

import com.yasinmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Yasin Zhang
 */
public class RedisPool {

    private static JedisPool pool;      // jedis连接池

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

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = PropertiesUtil.getIntProperty("redis.port");
    private static String redisPass = PropertiesUtil.getProperty("redis.password");

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

        pool = new JedisPool(config, redisIp, redisPort, 1000*2, redisPass);
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

}
