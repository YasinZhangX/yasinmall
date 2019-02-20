package com.yasinmall.controller.portal;

import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.Shipping;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IShippingService;
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
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.delete(user.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.update(user.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.select(user.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        // 获取当前用户
        User user = getCurrentUser(session);
        if (user == null) {
            return ServerResponse.createBySuccessD(0);
        }

        return iShippingService.list(user.getId(), pageNum, pageSize);
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
