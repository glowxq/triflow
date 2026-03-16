package com.glowxq.common.wechat.pay.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信支付配置属性
 * <p>
 * 用于配置微信支付 V3 版本的相关参数。
 * </p>
 *
 * <h3>配置示例</h3>
 * <pre>{@code
 * wechat:
 *   pay:
 *     app-id: wx1234567890
 *     mch-id: 1234567890
 *     mch-key: your-mch-key
 *     api-v3-key: your-api-v3-key
 *     notify-url: https://your-domain.com/notify/wechat
 *     private-key-path: classpath:cert/apiclient_key.pem
 *     private-cert-path: classpath:cert/apiclient_cert.pem
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
@Schema(description = "微信支付配置")
public class WechatPaymentConfiguration {

    /**
     * 关联的小程序/公众号 AppID
     */
    @Schema(description = "关联的 AppID", example = "wx1234567890")
    private String appId;

    /**
     * 商户号
     */
    @Schema(description = "商户号", example = "1234567890")
    private String mchId;

    /**
     * 商户密钥（V2 API 使用）
     */
    @Schema(description = "商户密钥")
    private String mchKey;

    /**
     * 子商户 AppID（服务商模式使用）
     */
    @Schema(description = "子商户 AppID")
    private String subAppId;

    /**
     * 子商户号（服务商模式使用）
     */
    @Schema(description = "子商户号")
    private String subMchId;

    /**
     * 支付结果通知 URL 前缀
     * <p>
     * 微信支付回调的基础 URL，具体路径会自动拼接。
     * </p>
     */
    @Schema(description = "通知 URL 前缀", example = "https://your-domain.com/api")
    private String notifyUrl;

    /**
     * 支付通知路径
     */
    @Schema(description = "支付通知路径", defaultValue = "/notify/wechat/pay")
    private String payNotifyPath = "/notify/wechat/pay";

    /**
     * 退款通知路径
     */
    @Schema(description = "退款通知路径", defaultValue = "/notify/wechat/refund")
    private String refundNotifyPath = "/notify/wechat/refund";

    /**
     * API V3 密钥
     * <p>
     * 用于 V3 版本接口的加解密。
     * </p>
     */
    @Schema(description = "API V3 密钥")
    private String apiV3Key;

    /**
     * 商户私钥文件路径
     * <p>
     * 支持 classpath: 和 file: 前缀。
     * </p>
     */
    @Schema(description = "私钥文件路径", example = "classpath:cert/apiclient_key.pem")
    private String privateKeyPath;

    /**
     * 商户证书文件路径
     * <p>
     * 支持 classpath: 和 file: 前缀。
     * </p>
     */
    @Schema(description = "证书文件路径", example = "classpath:cert/apiclient_cert.pem")
    private String privateCertPath;

    /**
     * p12 证书文件路径（可选）
     * <p>
     * 某些旧版接口可能需要。
     * </p>
     */
    @Schema(description = "p12 证书路径")
    private String keyPath;

    /**
     * 获取完整的支付通知 URL
     *
     * @return 完整的支付通知 URL
     */
    public String getFullPayNotifyUrl() {
        return normalizeUrl(notifyUrl) + payNotifyPath;
    }

    /**
     * 获取完整的退款通知 URL
     *
     * @return 完整的退款通知 URL
     */
    public String getFullRefundNotifyUrl() {
        return normalizeUrl(notifyUrl) + refundNotifyPath;
    }

    /**
     * 规范化 URL（移除末尾斜杠）
     */
    private String normalizeUrl(String url) {
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }
}
