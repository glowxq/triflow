package com.glowxq.common.sms.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 短信发送结果
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "短信发送结果")
public class SmsResult {

    /**
     * 是否发送成功
     */
    @Schema(description = "是否发送成功")
    private boolean success;

    /**
     * 配置标识（configId）
     */
    @Schema(description = "配置标识")
    private String configId;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息")
    private String message;

    /**
     * 错误码
     */
    @Schema(description = "错误码")
    private String errorCode;

    /**
     * 原始响应数据
     */
    @Schema(description = "原始响应数据")
    private Object rawResponse;

    /**
     * 创建成功结果
     */
    public static SmsResult success(String configId, String phone, String message) {
        return SmsResult.builder()
                        .success(true)
                        .configId(configId)
                        .phone(phone)
                        .message(message)
                        .build();
    }

    /**
     * 创建失败结果
     */
    public static SmsResult fail(String configId, String phone, String errorCode, String message) {
        return SmsResult.builder()
                        .success(false)
                        .configId(configId)
                        .phone(phone)
                        .errorCode(errorCode)
                        .message(message)
                        .build();
    }

}
