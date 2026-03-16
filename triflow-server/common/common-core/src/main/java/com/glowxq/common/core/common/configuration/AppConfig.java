package com.glowxq.common.core.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/4
 */
@Data
@ConfigurationProperties(prefix = "app")
@Configuration
public class AppConfig {

    /**
     * 项目名
     */
    private String project;

    /**
     * 应用名
     */
    private String name;

    /**
     * 业务
     */
    private String business = "system";

    /**
     * 环境
     */
    private String environment;

    /**
     * 版本
     */
    private String version;

    /**
     * 前端 preferences 缓存版本号
     * <p>
     * 修改此值可强制所有前端清除 preferences 缓存。
     * </p>
     */
    private String preferencesVersion = "1";
}
