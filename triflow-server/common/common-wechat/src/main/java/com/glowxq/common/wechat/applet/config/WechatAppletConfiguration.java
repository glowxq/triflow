package com.glowxq.common.wechat.applet.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置属性
 * <p>
 * 用于配置微信小程序的基本参数，包括 AppID、密钥等。
 * </p>
 *
 * <h3>配置示例</h3>
 * <pre>{@code
 * wechat:
 *   mini-app:
 *     app-id: wx1234567890
 *     secret: your-app-secret
 *     token: your-token
 *     aes-key: your-aes-key
 *     msg-data-format: JSON
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.mini-app")
@Schema(description = "微信小程序配置")
public class WechatAppletConfiguration {

    /**
     * 小程序 AppID
     */
    @Schema(description = "小程序 AppID", example = "wx1234567890")
    private String appId;

    /**
     * 小程序密钥
     */
    @Schema(description = "小程序密钥")
    private String secret;

    /**
     * 消息校验 Token
     * <p>
     * 用于验证微信服务器发送的消息。
     * </p>
     */
    @Schema(description = "消息校验 Token")
    private String token;

    /**
     * 消息加解密密钥
     * <p>
     * 用于消息加解密，需要在微信公众平台配置。
     * </p>
     */
    @Schema(description = "消息加解密密钥（AES Key）")
    private String aesKey;

    /**
     * 消息数据格式
     * <p>
     * 支持 JSON 和 XML 格式，默认为 JSON。
     * </p>
     */
    @Schema(description = "消息数据格式", defaultValue = "JSON")
    private String msgDataFormat = "JSON";
}
