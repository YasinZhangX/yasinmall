package com.yasinmall.service;

import com.yasinmall.common.ServerResponse;

/**
 * @author yasin
 */
public interface ICartService {

    ServerResponse list(Integer userId);

    ServerResponse add(Integer userId, Integer productId, Integer count);

    ServerResponse update(Integer userId, Integer productId, Integer count);

    ServerResponse deleteProduct(Integer userId, String productIds);

    ServerResponse selectOrUnselect(Integer userId, Integer productId, Integer checked);

    ServerResponse getCartProductCount(Integer userId);
}
