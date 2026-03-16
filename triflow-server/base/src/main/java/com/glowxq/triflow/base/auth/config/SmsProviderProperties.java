package com.glowxq.triflow.base.auth.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信服务商配置
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
@Schema(description = "短信服务商配置")
public class SmsProviderProperties {

    public static final String DEFAULT_PROVIDER = "sms4j";

    /**
     * 服务商类型（sms4j / aliyun-pnvs）
     */
    @Schema(description = "短信服务商类型")
    private String provider = DEFAULT_PROVIDER;

    /**
     * 阿里云号码认证服务配置
     */
    @Schema(description = "阿里云号码认证服务配置")
    private AliyunPnvsProperties pnvs = new AliyunPnvsProperties();

    @Data
    public static class AliyunPnvsProperties {

        private static final String DEFAULT_ENDPOINT = "dypnsapi.aliyuncs.com";
        private static final String DEFAULT_COUNTRY_CODE = "86";
        private static final long DEFAULT_VALID_TIME = 300L;
        private static final long DEFAULT_INTERVAL = 60L;
        private static final long DEFAULT_CODE_LENGTH = 6L;
        private static final long DEFAULT_CODE_TYPE = 1L;
        private static final long DEFAULT_DUPLICATE_POLICY = 1L;
        private static final long DEFAULT_AUTO_RETRY = 1L;

        /** AccessKey ID */
        @Schema(description = "AccessKey ID")
        private String accessKeyId;

        /** AccessKey Secret */
        @Schema(description = "AccessKey Secret")
        private String accessKeySecret;

        /** 服务 Endpoint */
        @Schema(description = "服务 Endpoint")
        private String endpoint = DEFAULT_ENDPOINT;

        /** 签名名称 */
        @Schema(description = "签名名称")
        private String signName;

        /** 方案名称 */
        @Schema(description = "方案名称")
        private String schemeName;

        /** 国家码 */
        @Schema(description = "国家码")
        private String countryCode = DEFAULT_COUNTRY_CODE;

        /** 验证码有效期（秒） */
        @Schema(description = "验证码有效期（秒）")
        private Long validTime = DEFAULT_VALID_TIME;

        /** 发送间隔（秒） */
        @Schema(description = "发送间隔（秒）")
        private Long interval = DEFAULT_INTERVAL;

        /** 验证码长度 */
        @Schema(description = "验证码长度")
        private Long codeLength = DEFAULT_CODE_LENGTH;

        /** 验证码类型 */
        @Schema(description = "验证码类型")
        private Long codeType = DEFAULT_CODE_TYPE;

        /** 重复策略 */
        @Schema(description = "重复策略")
        private Long duplicatePolicy = DEFAULT_DUPLICATE_POLICY;

        /** 自动重试 */
        @Schema(description = "自动重试")
        private Long autoRetry = DEFAULT_AUTO_RETRY;

        /** 是否返回验证码 */
        @Schema(description = "是否返回验证码")
        private Boolean returnVerifyCode = false;
    }
}
