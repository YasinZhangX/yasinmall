package com.yasinmall.controller.portal;

import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Shipping;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IShippingService;
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
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest httpServletRequest, Shipping shipping) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpServletRequest httpServletRequest, Integer shippingId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest httpServletRequest, Shipping shipping) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.update(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpServletRequest httpServletRequest, Integer shippingId) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpServletRequest httpServletRequest,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 获取当前用户
        User user = getCurrentUser(httpServletRequest);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.list(user.getId(), pageNum, pageSize);
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
