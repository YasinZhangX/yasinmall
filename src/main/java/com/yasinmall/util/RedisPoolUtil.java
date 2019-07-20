package com.yasinmall.util;

import com.yasinmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author Yasin Zhang
 */
@Slf4j
public class RedisPoolUtil {

    public static String set(String key, String value) {
        String result = null;

        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
        }

        return result;
    }

    public static String get(String key) {
        String result = null;

        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get value:{} error", key, e);
        }

        return result;
    }

    public static Long del(String key) {
        Long result = null;

        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del value:{} error", key, e);
        }

        return result;
    }

    public static String setEx(String key, String value, int expiredTime) {
        String result = null;

        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.setex(key, expiredTime, value);
        } catch (Exception e) {
            log.error("setex key:{} value:{} expiredTime:{} error", key, value, expiredTime, e);
        }

        return result;
    }

    /**
     * 设置key的有效期
     */
    public static Long expire(String key, int expiredTime) {
        Long result = null;

        try (Jedis jedis = RedisPool.getJedis()) {
            result = jedis.expire(key, expiredTime);
        } catch (Exception e) {
            log.error("expire key:{} expiredTime:{} error", key, expiredTime, e);
        }

        return result;
    }

}
