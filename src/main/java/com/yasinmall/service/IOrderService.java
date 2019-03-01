package com.yasinmall.service;

import com.yasinmall.common.ServerResponse;

import java.util.Map;

/**
 * @author Yasin Zhang
 */
public interface IOrderService {

    ServerResponse pay(Long orderNo, Integer userId, String path);

    ServerResponse alipayCallback(Map<String, String> params);

    ServerResponse queryOrderPayStatus(Integer userId, Long orderNo);

}
