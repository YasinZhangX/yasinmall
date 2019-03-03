package com.yasinmall.service;

import com.github.pagehelper.PageInfo;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.vo.OrderVo;

import java.util.Map;

/**
 * @author Yasin Zhang
 */
public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancel(Integer userId, Long orderNo);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //backend
    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<OrderVo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse manageSendGoods(Long orderNo);

}
