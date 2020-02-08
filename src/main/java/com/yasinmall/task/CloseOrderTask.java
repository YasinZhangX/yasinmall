package com.yasinmall.task;

import com.yasinmall.common.Const;
import com.yasinmall.service.IOrderService;
import com.yasinmall.util.PropertiesUtil;
import com.yasinmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Yasin Zhang
 */
@Component
@Slf4j
public class CloseOrderTask {

    private RedissonClient redissonClient;

    private IOrderService iOrderService;

    @Autowired
    public CloseOrderTask(IOrderService iOrderService, RedissonClient redissonClient) {
        this.iOrderService = iOrderService;
        this.redissonClient = redissonClient;
    }

    // @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
        iOrderService.closeOrder(hour);
    }

    // @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "50000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
            String.valueOf(System.currentTimeMillis()+lockTimeout));
        if (setnxResult != null && setnxResult == 1) {
            // 返回1表示设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("获取分布式锁失败：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
    }

    // @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "50000"));

        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
            String.valueOf(System.currentTimeMillis()+lockTimeout));
        if (setnxResult != null && setnxResult == 1) {
            // 返回1表示设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            // 未获取锁，继续判断时间戳，看是否可以重置并获取到锁
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                String getSetResult = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                    String.valueOf(System.currentTimeMillis()+lockTimeout));
                // 再次用当前时间戳getset，获取key的旧值，根据旧值判断是否可以获取锁
                // 当key没有旧值时，key不存在，可以获取锁
                if (getSetResult == null || StringUtils.equals(lockValueStr, getSetResult)) {
                    // 真正获取到锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            } else {
                log.info("没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV4() {
        RLock lock = redissonClient.getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock = false;
        try {
            if (getLock = lock.tryLock(0, 50, TimeUnit.SECONDS)) {
                log.info("Redisson获取分布式锁：{},ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                    Thread.currentThread().getName());
                int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
                // TODO: 添加业务
                // iOrderService.closeOrder(hour);
            } else {
                log.info("Redisson没有获取到分布式锁:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        } catch (InterruptedException e) {
            log.error("Redisson分布式锁获取异常", e);
        } finally {
            if (getLock) {
                lock.unlock();
                log.info("Redisson分布式锁释放");
            }
        }
    }

    private void closeOrder(String lockName) {
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout", "50000"));
        RedisShardedPoolUtil.expire(lockName, (int) (lockTimeout/1000)); // 有效期50s防止死锁
        log.info("获取{}, ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}, ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
    }
}
