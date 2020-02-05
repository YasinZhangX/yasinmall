package com.yasinmall.task;

import com.yasinmall.common.Const;
import com.yasinmall.service.IOrderService;
import com.yasinmall.util.PropertiesUtil;
import com.yasinmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author Yasin Zhang
 */
@Component
@Slf4j
public class CloseOrderTask {

    private IOrderService iOrderService;

    @Autowired
    public CloseOrderTask(IOrderService iOrderService) {
        this.iOrderService = iOrderService;
    }

    // @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
        iOrderService.closeOrder(hour);
    }

    @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
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

    private void closeOrder(String lockName) {
        RedisShardedPoolUtil.expire(lockName, 50); // 有效期50s防止死锁
        log.info("获取{}, ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}, ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
    }
}
