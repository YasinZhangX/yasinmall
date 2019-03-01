package com.yasinmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yasin Zhang
 */
public class OrderProductVo {

    private List<OrderItemVo> orderItemVoList;

    private BigDecimal productTotalPrice;

    private String ImageHost;

    public List<OrderItemVo> getOrderItemVoList() {
        return orderItemVoList;
    }

    public void setOrderItemVoList(List<OrderItemVo> orderItemVoList) {
        this.orderItemVoList = orderItemVoList;
    }

    public BigDecimal getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductTotalPrice(BigDecimal productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public String getImageHost() {
        return ImageHost;
    }

    public void setImageHost(String imageHost) {
        ImageHost = imageHost;
    }
}
