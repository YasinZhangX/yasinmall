package com.yasinmall.service.impl;

import com.yasinmall.common.Const;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.common.TokenCache;
import com.yasinmall.dao.UserMapper;
import com.yasinmall.pojo.User;
import com.yasinmall.service.IUserService;
import com.yasinmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author yasin
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorM("用户名不存在");
        }

        String md5Passwd = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Passwd);
        if (user == null) {
            return ServerResponse.createByErrorM("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccessMD("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse<String> validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);

        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorM("注册失败");
        }

        return ServerResponse.createBySuccessM("注册成功");
    }

    /**
     * 验证新建用户的信息的合法性
     *
     * @param str  待验证消息
     * @param type 验证类型
     * @return com.yasinmall.common.ServerResponse<java.lang.String>
     */
    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorM("用户名已存在");
                }
            }
            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorM("email已存在");
                }
            }
        } else {
            return ServerResponse.createByErrorM("参数错误");
        }

        return ServerResponse.createBySuccessM("校验成功");
    }

    /**
     * 获取“找回密码”问题
     *
     * @param username 用户名
     * @return com.yasinmall.common.ServerResponse<java.lang.String>
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            // 用户不存在
            return ServerResponse.createByErrorM("用户不存在");
        }

        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccessD(question);
        }

        return ServerResponse.createByErrorM("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // 问题及问题答案属于该用户，并且答案正确
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccessD(forgetToken);
        }

        return ServerResponse.createByErrorM("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorM("参数错误,缺少token");
        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            // 用户不存在
            return ServerResponse.createByErrorM("用户不存在");
        }

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) {
            // token无效或者过期
            return ServerResponse.createByErrorM("token无效或者过期");
        }

        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);

            if (rowCount > 0) {
                return ServerResponse.createBySuccessM("修改密码成功");
            }
        } else {
            return ServerResponse.createByErrorM("token错误,请重新获取重置密码的token");
        }

        return ServerResponse.createByErrorM("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        // 为了防止横向越权，需要校验用户的旧密码为该用户所有
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorM("旧密码错误");
        }

        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessM("密码更新成功");
        }

        return ServerResponse.createByErrorM("密码更新失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        // username不能被更新
        // email需要验证是否存在，若存在则不能是当前用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.createByErrorM("email已存在，请更换email再尝试更新");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMD("更新个人信息成功", updateUser);
        }

        return ServerResponse.createByErrorM("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorM("找不到当前用户");
        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccessD(user);
    }

    /*
     **************
     *  backend
     **************
     */

    /**
     * 校验用户是否为管理员
     *
     * @param user 用户数据
     * @return ServerResponse 包含success和error
     */
    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
