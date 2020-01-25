package com.yasinmall.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.ServletContext;

/**
 * @author Yasin Zhang
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800)
public class RedisHttpSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer(ServletContext servletContext, CookieConfig config) {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        if (servletContext != null) {
            if (config != null) {
                if (config.getName() != null)
                    cookieSerializer.setCookieName(config.getName());
                if (config.getDomain() != null)
                    cookieSerializer.setDomainName(config.getDomain());
                if (config.getPath() != null)
                    cookieSerializer.setCookiePath(config.getPath());
                if (config.getMaxAge() != -1)
                    cookieSerializer.setCookieMaxAge(config.getMaxAge());
                if (config.getDomainPattern() != null)
                    cookieSerializer.setDomainNamePattern(config.getDomainPattern());
                if (config.getJvmRoute() != null)
                    cookieSerializer.setJvmRoute(config.getJvmRoute());
                cookieSerializer.setUseSecureCookie(config.isSecure());
                cookieSerializer.setUseBase64Encoding(config.isUseBase64Encoding());
                cookieSerializer.setUseHttpOnlyCookie(config.isHttpOnly());
            }
        }

        return cookieSerializer;
    }
}
