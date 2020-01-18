package com.yasinmall.controller.portal;

import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.ICartService;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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
     * @param httpServletRequest 用户request
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.list(user.getId());
    }

    /**
     * 向购物车添加产品
     *
     * @param httpServletRequest 用户request
     * @param count     添加产品数量
     * @param productId 添加产品ID
     * @return ServerResponse
     */
    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, Integer count, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.add(user.getId(), productId, count);
    }

    /**
     * 更新购物车内产品数量
     *
     * @param httpServletRequest 用户request
     * @param count     添加产品数量
     * @param productId 添加产品ID
     * @return ServerResponse
     */
    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest, Integer count, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.update(user.getId(), productId, count);
    }

    /**
     * 删除购物车内产品
     *
     * @param httpServletRequest 用户request
     * @param productIds 由产品ID组成
     * @return ServerResponse
     */
    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpServletRequest httpServletRequest, String productIds) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.deleteProduct(user.getId(), productIds);
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpServletRequest httpServletRequest) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpServletRequest httpServletRequest) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse selectAll(HttpServletRequest httpServletRequest, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse unSelectAll(HttpServletRequest httpServletRequest, Integer productId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return needLoginRsp();
        }

        return iCartService.selectOrUnselect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpServletRequest httpServletRequest) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iCartService.getCartProductCount(user.getId());
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return null;
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        return JsonUtil.string2Obj(userJsonStr, User.class);
    }

    /**
     * 返回需要登录Rsp
     */
    private ServerResponse needLoginRsp() {
        return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
    }
}
