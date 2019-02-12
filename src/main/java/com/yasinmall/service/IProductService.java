package com.yasinmall.service;

import com.github.pagehelper.PageInfo;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Product;
import com.yasinmall.vo.ProductDetailVo;

/**
 * @author yasin
 */
public interface IProductService {

    ServerResponse manageSaveOrUpdateProduct(Product product);

    ServerResponse<String> manageSetSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageGetProductDetail(Integer productId);

    ServerResponse<PageInfo> manageGetProductList(int pageNum, int pageSize);

}
