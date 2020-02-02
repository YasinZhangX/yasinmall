package com.yasinmall.configuration;

import com.yasinmall.controller.common.interceptor.AuthorityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Yasin Zhang
 */
@Configuration
public class WebConfigure implements WebMvcConfigurer {

    private AuthorityInterceptor authorityInterceptor;

    @Autowired
    WebConfigure(AuthorityInterceptor authorityInterceptor) {
        this.authorityInterceptor = authorityInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        registry.addInterceptor(authorityInterceptor).addPathPatterns("/manage/**").excludePathPatterns("/**/login.do");
    }
}
