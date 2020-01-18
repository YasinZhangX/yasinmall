package com.yasinmall.controller.backend;

import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IOrderService;
import com.yasinmall.service.IUserService;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse orderList(HttpServletRequest httpServletRequest,
                                    @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);

        // 校验用户是否存在且有管理员权限
        ServerResponse resultRsp = userAdminAuth(user);
        if (resultRsp.isSuccess()) {
            return iOrderService.manageList(pageNum, pageSize);
        } else {
            return resultRsp;
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpServletRequest httpServletRequest, Long orderNo) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);

        // 校验用户是否存在且有管理员权限
        ServerResponse resultRsp = userAdminAuth(user);
        if (resultRsp.isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        } else {
            return resultRsp;
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpServletRequest httpServletRequest, Long orderNo,
                                      @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);

        // 校验用户是否存在且有管理员权限
        ServerResponse resultRsp = userAdminAuth(user);
        if (resultRsp.isSuccess()) {
            return iOrderService.manageSearch(orderNo, pageNum, pageSize);
        } else {
            return resultRsp;
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpServletRequest httpServletRequest, Long orderNo) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);

        // 校验用户是否存在且有管理员权限
        ServerResponse resultRsp = userAdminAuth(user);
        if (resultRsp.isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        } else {
            return resultRsp;
        }
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
     * 校验用户是否存在且有管理员权限
     */
    private ServerResponse userAdminAuth(User user) {
        if (user == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }

        // 校验是否为管理员
        if (iUserService.checkAdminRole(user).isSuccess()) {
            // 用户为管理员
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorM("无权限操作，需要管理员权限");
        }
    }

}
