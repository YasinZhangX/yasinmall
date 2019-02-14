package com.yasinmall.service;

import com.github.pagehelper.PageInfo;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Product;

/**
 * @author yasin
 */
public interface IProductService {

    ServerResponse manageSaveOrUpdateProduct(Product product);

    ServerResponse<String> manageSetSaleStatus(Integer productId, Integer status);

    ServerResponse manageGetProductDetail(Integer productId);

    ServerResponse<PageInfo> manageGetProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> manageSearchProduce(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse getProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId,
                                                         int pageNum, int pageSize, String orderBy);

}
