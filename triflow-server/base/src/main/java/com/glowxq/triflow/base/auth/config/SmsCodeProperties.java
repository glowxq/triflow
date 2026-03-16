package com.glowxq.triflow.base.auth.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Set;

/**
 * 短信验证码模板配置
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms.code")
@Schema(description = "短信验证码模板配置")
public class SmsCodeProperties {

    public static final String TYPE_LOGIN = "login";
    public static final String TYPE_REGISTER = "register";
    public static final String TYPE_RESET = "reset";

    private static final Set<String> SUPPORTED_TYPES = Set.of(TYPE_LOGIN, TYPE_REGISTER, TYPE_RESET);

    @Schema(description = "登录验证码模板ID")
    private String loginTemplateId;
    @Schema(description = "注册验证码模板ID")
    private String registerTemplateId;
    @Schema(description = "重置密码验证码模板ID")
    private String resetTemplateId;

    public String resolveTemplateId(String type) {
        String normalizedType = normalizeType(type);
        if (normalizedType == null) {
            return null;
        }
        return switch (normalizedType) {
            case TYPE_LOGIN -> loginTemplateId;
            case TYPE_REGISTER -> registerTemplateId;
            case TYPE_RESET -> resetTemplateId;
            default -> null;
        };
    }

    public boolean isSupportedType(String type) {
        String normalizedType = normalizeType(type);
        return normalizedType != null && SUPPORTED_TYPES.contains(normalizedType);
    }

    private String normalizeType(String type) {
        if (!StringUtils.hasText(type)) {
            return null;
        }
        return type.trim().toLowerCase(Locale.ROOT);
    }
}
