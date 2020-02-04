package com.yasinmall.task;

import com.yasinmall.service.IOrderService;
import com.yasinmall.util.PropertiesUtil;
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

    @Scheduled(cron = "0 */1 * * * ?")  // 每分钟执行
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = PropertiesUtil.getIntProperty("close_order_task.time.hour", 2);
        iOrderService.closeOrder(hour);
    }
}
