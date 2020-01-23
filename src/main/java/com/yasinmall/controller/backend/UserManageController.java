package com.yasinmall.controller.backend;

import com.yasinmall.common.Const;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IUserService;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    /**
     * 管理员账户登录，检查了用户角色是否为管理员
     *
     * @param username 用户名
     * @param password 用户登录密码
     * @param httpServletRequest  用户httpServletRequest
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpServletRequest httpServletRequest) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                // 管理员登录
                String loginToken = CookieUtil.readLoginToken(httpServletRequest);
                RedisPoolUtil.setEx(loginToken, JsonUtil.obj2String(user), Const.RedisCacheExpireTime.REDIS_SESSION_TIME);
                return response;
            } else {
                return ServerResponse.createByErrorM("不是管理员，无法登录");
            }
        }

        return response;
    }
}
