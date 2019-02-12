package com.yasinmall.controller.backend;

import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Product;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IProductService;
import com.yasinmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    /**
     * 新增或更新产品
     *
     * @param session 用户session
     * @param product 产品数据
     * @return ServerResponse
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，添加产品
            return iProductService.manageSaveOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 更新产品销售状态
     *
     * @param session   用户session
     * @param productId 产品ID
     * @param status    产品销售状态
     * @return ServerResponse
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，更新产品销售状态
            return iProductService.manageSetSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取产品详细信息
     *
     * @param session   用户session
     * @param productId 产品ID
     * @return ServerResponse
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，填充产品
            return iProductService.manageGetProductDetail(productId);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

    /**
     * 获取产品列表
     *
     * @param session  用户session
     * @param pageNum  页码数
     * @param pageSize 页面大小
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，获取产品列表
            return iProductService.manageGetProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }


    /**
     * 搜索产品
     *
     * @param session     用户session
     * @param productName 产品名称
     * @param productId   产品ID
     * @param pageNum     页码数
     * @param pageSize    页面大小
     * @return ServerResponse
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, String productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员，搜索产品
            return iProductService.manageGetProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

}
