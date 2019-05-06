package com.yasinmall.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * @author Yasin Zhang
 */
public class WebConfig {

    //配置文件上传
    @Bean(name = {"multipartResolver"})
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver=new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("utf-8");
        commonsMultipartResolver.setMaxUploadSize(10485760);
        commonsMultipartResolver.setMaxInMemorySize(4096);
        return commonsMultipartResolver;
    }

}
