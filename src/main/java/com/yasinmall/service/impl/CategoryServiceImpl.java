package com.yasinmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.dao.CategoryMapper;
import com.yasinmall.pojo.Category;
import com.yasinmall.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @author yasin
 */
@Service("iCategoryService")
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    //private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加品类
     */
    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorM("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); // 表明该分类可用

        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessM("添加品类成功");
        }

        return ServerResponse.createByErrorM("添加品类失败");
    }

    /**
     * 更新品类名称
     */
    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorM("添加品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) {
            return ServerResponse.createBySuccessM("更新品类名称成功");
        }

        return ServerResponse.createByErrorM("更新品类名称失败");
    }

    /**
     * 查询当前品类的子分类
     *
     * @param categoryId 当前品类ID
     * @return 子分类的List
     */
    @Override
    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccessD(categoryList);
    }

    /**
     * 递归查询本节点ID以及其子节点ID
     *
     * @param categoryId 本节点ID
     * @return 携带节点ID List的ServerResponse
     */
    @Override
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId) {
        Set<Category> categorySet = Sets.newHashSet();
        List<Integer> categoryIdList = Lists.newArrayList();

        if (categoryId != null) {
            findChildCategory(categorySet, categoryId);
            for (Category categoryItem : categorySet) {
                categoryIdList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccessD(categoryIdList);
    }

    /**
     * 采用递归算法，算出子节点
     *
     * @param categorySet 递归数据
     * @param categoryId  递归起点
     * @return 子节点Set
     */
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null) {
            categorySet.add(category);
        }

        // 查找子节点，退出条件为子节点为空
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }

        return categorySet;
    }
}
