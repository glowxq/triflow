package com.glowxq.triflow.base.auth.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 飞书配置属性
 * <p>
 * 读取 feishu.app 下的配置信息，用于飞书 OAuth 登录。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Component
@ConfigurationProperties(prefix = "feishu.app")
@Schema(description = "飞书应用配置")
public class FeishuProperties {

    /** 飞书应用 App ID */
    @Schema(description = "飞书应用 App ID")
    private String appId;

    /** 飞书应用 App Secret */
    @Schema(description = "飞书应用 App Secret")
    private String appSecret;

    /** 飞书授权重定向地址 */
    @Schema(description = "飞书授权重定向地址")
    private String redirectUri;

    /** 飞书 OAuth 授权基础 URL */
    private static final String OAUTH_BASE_URL = "https://open.feishu.cn/open-apis/authen/v1/authorize";

    /** 飞书获取用户访问令牌 URL */
    private static final String TOKEN_URL = "https://open.feishu.cn/open-apis/authen/v1/oidc/access_token";

    /** 飞书获取用户信息 URL */
    private static final String USER_INFO_URL = "https://open.feishu.cn/open-apis/authen/v1/user_info";

    /** 飞书获取应用访问令牌 URL */
    private static final String APP_TOKEN_URL = "https://open.feishu.cn/open-apis/auth/v3/app_access_token/internal";

    /**
     * 获取 OAuth 授权基础 URL
     */
    public String getOauthBaseUrl() {
        return OAUTH_BASE_URL;
    }

    /**
     * 获取用户访问令牌 URL
     */
    public String getTokenUrl() {
        return TOKEN_URL;
    }

    /**
     * 获取用户信息 URL
     */
    public String getUserInfoUrl() {
        return USER_INFO_URL;
    }

    /**
     * 获取应用访问令牌 URL
     */
    public String getAppTokenUrl() {
        return APP_TOKEN_URL;
    }

}
