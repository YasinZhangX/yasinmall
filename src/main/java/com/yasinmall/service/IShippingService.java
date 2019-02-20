package com.yasinmall.service;

import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Shipping;

/**
 * @author yasin
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse delete(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse select(Integer userId, Integer shippingId);

    ServerResponse list(Integer userId, int pageNum, int pageSize);

}
