package com.yasinmall.controller.backend;

import com.yasinmall.common.ServerResponse;
import com.yasinmall.service.ICategoryService;
import com.yasinmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;


    /**
     * 添加品类，只有已登录管理员可以操作
     *
     * @param httpServletRequest      用户httpServletRequest
     * @param categoryName 品类名称
     * @param parentId     品类父类ID
     * @return ServerResponse
     */
    @RequestMapping(value = "add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest httpServletRequest, String categoryName,
                                      @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        return iCategoryService.addCategory(categoryName, parentId);
    }

    /**
     * 更新品类名称
     *
     * @param httpServletRequest      用户httpServletRequest
     * @param categoryId   品类ID
     * @param categoryName 品类名称
     * @return ServerResponse
     */
    @RequestMapping(value = "set_category_name.do")
    @ResponseBody
    public ServerResponse setCategoryName(HttpServletRequest httpServletRequest, Integer categoryId, String categoryName) {
        // 更新 Category Name
        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    /**
     * 查询当前品类的子分类
     *
     * @param httpServletRequest    用户httpServletRequest
     * @param categoryId 当前品类ID
     * @return ServerResponse
     */
    @RequestMapping(value = "get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpServletRequest httpServletRequest,
                                                      @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        // 查询子节点的category信息，并且不递归，保持平级
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 查询本节点ID以及其子节点ID
     *
     * @param httpServletRequest    用户httpServletRequest
     * @param categoryId 当前品类ID
     * @return ServerResponse
     */
    @RequestMapping(value = "get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpServletRequest httpServletRequest,
                                                             @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        // 查询当前节点id和递归子节点id
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
