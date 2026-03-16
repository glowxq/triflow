package com.glowxq.common.security.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由白名单配置
 * <p>
 * 配置路径相对于 context-path，即不含模块前缀（如 /base）
 * 例如：/public/** 会匹配 /base/public/sms/send
 *
 * @author glowxq
 * @since 2024/1/22
 */
@Data
@Component
@ConfigurationProperties(prefix = "router")
public class WhitelistProperties {

    /**
     * 白名单路径列表
     * <p>
     * 支持 Ant 风格路径匹配：
     * - ? 匹配单个字符
     * - * 匹配零个或多个字符（不含路径分隔符）
     * - ** 匹配零个或多个目录
     */
    private List<String> whitelist = new ArrayList<>();

}
