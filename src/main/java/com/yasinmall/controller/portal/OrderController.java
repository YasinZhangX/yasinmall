package com.yasinmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Yasin Zhang
 */
@Controller
@RequestMapping("/order/")
@Slf4j
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    /**
     * 创建订单
     * @param session 用户session
     * @param shippingId 收货地址ID
     * @return ServerResponse
     */
    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iOrderService.createOrder(user.getId(), shippingId);
    }

    /**
     * 删除订单
     * @param session 用户session
     * @param orderNo 待删除订单号
     * @return ServerResponse
     */
    @RequestMapping("cancel.do")
    @ResponseBody
    public ServerResponse cancel(HttpSession session, Long orderNo) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iOrderService.cancel(user.getId(), orderNo);
    }

    /**
     * 获取用户购物车中产品列表
     * @param session 用户session
     * @return ServerResponse
     */
    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrderCartProduct(HttpSession session) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iOrderService.getOrderCartProduct(user.getId());
    }

    /**
     * 获取用户订单详情
     *
     * @param session 用户session
     * @param orderNo 用户订单ID
     * @return ServerResponse
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iOrderService.getOrderDetail(user.getId(), orderNo);
    }

    /**
     * 查看订单
     *
     * @param session 用户session
     * @param pageNum 页码数
     * @param pageSize 页面大小
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iOrderService.getOrderList(user.getId(), pageNum, pageSize);
    }

    /**
     * 订单支付
     * @param session 用户session
     * @param orderNo 订单号
     * @param request 用户请求
     * @return ServerResponse
     */
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo, user.getId(), path);
    }

    /**
     * Alipay回调处理函数
     * @param request Alipay HTTP数据
     * @return alipay回调字符串
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request) {
        // 获取支付宝回调数据
        Map<String, String> params = Maps.newHashMap();
        Map<String, String[]> requestParms = request.getParameterMap();
        for (String name : requestParms.keySet()) {
            String[] values = requestParms.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length-1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());

        // 验证回调的正确性(非常重要！！！)
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());

            if (!alipayRSACheckedV2) {
                return ServerResponse.createByErrorM("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常", e);
        }

        // 验证各种数据
        ServerResponse resultRsp = iOrderService.alipayCallback(params);
        if (resultRsp.isSuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }

    /**
     * 查询订单支付状态
     * @param session 用户session
     * @param orderNo 订单号
     * @return ServerResponse
     */
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServerResponse queryOrderPayStatus(HttpSession session, Long orderNo) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        ServerResponse resultRsp = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
        if (resultRsp.isSuccess()) {
            return ServerResponse.createBySuccessD(true);
        }
        return ServerResponse.createBySuccessD(false);
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(Const.CURRENT_USER);
    }

    /**
     * 返回需要登录Rsp
     */
    private ServerResponse needLoginRsp() {
        return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }

}
