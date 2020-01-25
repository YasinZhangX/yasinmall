package com.yasinmall.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yasin Zhang
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.session.cookie")
public class CookieConfig {

    private String name = "SESSION";

    private String domain;

    private String path;

    private Integer maxAge = -1;

    private String domainPattern;

    private String jvmRoute;

    private boolean httpOnly = true;

    private boolean secure = false;

    private boolean useBase64Encoding = true;

}
