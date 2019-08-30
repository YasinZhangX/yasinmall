package com.yasinmall.controller.common;

import com.yasinmall.common.Const;
import com.yasinmall.pojo.User;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisPoolUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Yasin Zhang
 */
@Order(1)
@WebFilter(filterName = "sessionExpireFilter", urlPatterns = "*.do")
public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            // 判断loginToken是否为空或""
            String userJsonStr = RedisPoolUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr, User.class);
            if (user != null) {
                // 如果user不为空, 则重置session时间, 即调用expire
                RedisPoolUtil.expire(loginToken, Const.RedisCacheExpireTime.REDIS_SESSION_TIME);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
