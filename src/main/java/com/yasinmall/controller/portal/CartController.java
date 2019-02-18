package com.yasinmall.controller.portal;

import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    /**
     * 查询用户购物车数据
     *
     * @param session 用户session
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.list(user.getId());
    }

    /**
     * 向购物车添加产品
     *
     * @param session   用户session
     * @param count     添加产品数量
     * @param productId 添加产品ID
     * @return ServerResponse
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer count, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.add(user.getId(), productId, count);
    }

    /**
     * 更新购物车内产品数量
     *
     * @param session   用户session
     * @param count     添加产品数量
     * @param productId 添加产品ID
     * @return ServerResponse
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer count, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.update(user.getId(), productId, count);
    }

    /**
     * 删除购物车内产品
     *
     * @param session    用户session
     * @param productIds 由产品ID组成
     * @return ServerResponse
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpSession session, String productIds) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpSession session, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpSession session) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iCartService.getCartProductCount(user.getId());
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute(Const.CURRENT_USER);
    }

    /**
     * 返回需要登录Rsp
     */
    private ServerResponse needLoginRsp() {
        return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }
}
