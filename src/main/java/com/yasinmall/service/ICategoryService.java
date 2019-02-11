package com.yasinmall.service;

import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Category;

import java.util.List;

/**
 * @author yasin
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse selectCategoryAndChildrenById(Integer categoryId);

}
