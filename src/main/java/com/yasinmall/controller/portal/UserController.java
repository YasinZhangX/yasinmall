package com.yasinmall.controller.portal;

import com.yasinmall.common.Const;
import com.yasinmall.common.ResponseCode;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IUserService;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author yasin
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录，并创建对应的 session
     *
     * @param username 用户名
     * @param password 用户登录密码
     * @param session  用户session
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session,
                                      HttpServletResponse httpServletResponse) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            CookieUtil.writeLoginToken(httpServletResponse, session.getId());
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()),
                Const.RedisCacheExpireTime.REDIS_SESSION_TIME);
        }
        return response;
    }

    /**
     * 用户登出，直接删除对应的 session
     *
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest httpServletRequest,
                                         HttpServletResponse httpServletResponse) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        CookieUtil.deleteLoginToken(httpServletRequest, httpServletResponse);
        RedisShardedPoolUtil.del(loginToken);

        return ServerResponse.createBySuccess();
    }

    /**
     * 用户注册
     *
     * @param user 用户数据
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    /**
     * 验证用户信息，包括 username 和 email
     *
     * @param str  待验证消息
     * @param type 验证类型
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     *
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUerInfo(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorM("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        return ServerResponse.createBySuccessD(user);
    }

    /**
     * 获取用户“找回密码”问题
     *
     * @param username 用户名
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username) {
        return iUserService.selectQuestion(username);
    }

    /**
     * 确定用户“找回密码”问题答案是否正确
     *
     * @param username 用户名
     * @param question 用户找回密码问题
     * @param answer   用户找回密码答案
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码时，重置密码
     *
     * @param username    用户名
     * @param passwordNew 用户的新密码
     * @param forgetToken 用户的重置密码的token
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        return iUserService.forgetResetPassword(username, passwordNew, forgetToken);
    }

    /**
     * 在线用户重置密码
     *
     * @param httpServletRequest 用户request
     * @param passwordOld 用户的旧密码
     * @param passwordNew 用户的新密码
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpServletRequest httpServletRequest, String passwordOld, String passwordNew) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorM("用户未登录，无法获取当前用户的信息");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return ServerResponse.createByErrorM("用户未登录");
        }

        return iUserService.resetPassword(passwordOld, passwordNew, user);
    }

    /**
     * 更新用户数据
     *
     * @param httpServletRequest 用户request
     * @param user    用户数据
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> update_information(HttpServletRequest httpServletRequest, User user) {
        User currentUser = getCurrentUser(httpServletRequest);
        if (currentUser == null) {
            return ServerResponse.createByErrorM("用户未登录");
        }

        user.setId(currentUser.getId());
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            response.getData().setRole(currentUser.getRole());
            String loginToken = CookieUtil.readLoginToken(httpServletRequest);
            String userJsonStr = JsonUtil.obj2String(response.getData());
            RedisShardedPoolUtil.setEx(loginToken, userJsonStr, Const.RedisCacheExpireTime.REDIS_SESSION_TIME);
        }

        return response;
    }

    /**
     * 获取用户信息
     *
     * @param httpServletRequest 用户request
     * @return com.yasinmall.common.ServerResponse
     */
    @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> get_information(HttpServletRequest httpServletRequest) {
        User currentUser = getCurrentUser(httpServletRequest);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeM(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录status=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }

    /**
     * 获取当前登录用户
     */
    private User getCurrentUser(HttpServletRequest httpServletRequest) {
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isEmpty(loginToken)) {
            return null;
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        return JsonUtil.string2Obj(userJsonStr, User.class);
    }

}
