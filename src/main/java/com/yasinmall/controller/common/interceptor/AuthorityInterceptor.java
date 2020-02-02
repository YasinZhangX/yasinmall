package com.yasinmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.yasinmall.common.Const;
import com.yasinmall.common.ServerResponse;
import com.yasinmall.pojo.User;
import com.yasinmall.util.CookieUtil;
import com.yasinmall.util.JsonUtil;
import com.yasinmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * @author Yasin Zhang
 */
@Component
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        // 请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        // 解析参数，具体的参数key以及value
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        for (Object entry : paramMap.entrySet()) {
            Map.Entry e = (Map.Entry) entry;
            String mapKey = (String)e.getKey();
            String mapValue = StringUtils.EMPTY;
            // request 中 value 是 String[]
            Object obj = e.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[])obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        log.info("权限拦截器拦截到请求，className:{}, methodName:{}", className, methodName);
        log.info("get parameters: {}", requestParamBuffer.toString());

        User user = null;
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = JsonUtil.string2Obj(userJsonStr, User.class);
        }
        if (user == null || (user.getRole() != Const.Role.ROLE_ADMIN)) {
            // 返回false
            response.reset(); // 需要添加reset，否则会抛出 getWriter() has already been called
            response.setCharacterEncoding("UTF-8"); // 需要设置编码，否则会乱码
            response.setContentType("application/json;charset=UTF-8"); // 需要设置返回值类型，因为是json接口

            PrintWriter out = response.getWriter();
            // PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    // 特殊处理富文本文件上传
                    Map<String, Object> resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员账户");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorM("拦截器拦截，用户未登录")));
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    // 特殊处理富文本文件上传
                    Map<String, Object> resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    out.print(JsonUtil.obj2String(resultMap));
                } else {
                    out.print(JsonUtil.obj2String(ServerResponse.createByErrorM("拦截器拦截，用户无权限操作")));
                }
            }
            out.flush();
            out.close(); // 记得关闭

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }
}
