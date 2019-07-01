package com.yasinmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * @author yasin
 */
@Slf4j
public class PropertiesUtil {

    private static Properties props;

    static {
        String fileName = "yasinmall.properties";
        props = new Properties();
        try {
            props.load(new InputStreamReader(Objects.requireNonNull(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)), StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("配置文件读取异常", e);
        }
    }

    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    public static String getProperty(String key, String defautlValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defautlValue;
        }
        return value.trim();
    }

}
