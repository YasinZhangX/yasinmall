package com.yasinmall.controller.backend;

import com.yasinmall.common.ServerResponse;
import com.yasinmall.service.IOrderService;
import com.yasinmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse orderList(HttpServletRequest httpServletRequest,
                                    @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iOrderService.manageList(pageNum, pageSize);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpServletRequest httpServletRequest, Long orderNo) {
        return iOrderService.manageDetail(orderNo);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpServletRequest httpServletRequest, Long orderNo,
                                      @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return iOrderService.manageSearch(orderNo, pageNum, pageSize);
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpServletRequest httpServletRequest, Long orderNo) {
        return iOrderService.manageSendGoods(orderNo);
    }
}
