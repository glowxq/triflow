package com.glowxq.common.sms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信发送错误码枚举
 * <p>
 * 定义短信发送过程中可能出现的错误类型。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Getter
@AllArgsConstructor
public enum SmsErrorCode {

    /**
     * 发送成功
     */
    SUCCESS("SUCCESS", "发送成功"),

    /**
     * 发送过程中发生异常
     */
    SEND_ERROR("SEND_ERROR", "发送异常"),

    /**
     * 发送失败（渠道返回失败）
     */
    SEND_FAILED("SEND_FAILED", "发送失败"),

    /**
     * 配置不存在
     */
    CONFIG_NOT_FOUND("CONFIG_NOT_FOUND", "短信配置不存在"),

    /**
     * 参数无效
     */
    INVALID_PARAM("INVALID_PARAM", "参数无效"),

    /**
     * 手机号格式错误
     */
    INVALID_PHONE("INVALID_PHONE", "手机号格式错误"),

    /**
     * 模板不存在
     */
    TEMPLATE_NOT_FOUND("TEMPLATE_NOT_FOUND", "模板不存在"),

    /**
     * 频率限制
     */
    RATE_LIMITED("RATE_LIMITED", "发送频率超限"),

    /**
     * 余额不足
     */
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE", "账户余额不足");

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误描述
     */
    private final String message;

    /**
     * 根据错误码获取枚举
     *
     * @param code 错误码
     *
     * @return 对应的枚举值，未找到返回 SEND_FAILED
     */
    public static SmsErrorCode of(String code) {
        for (SmsErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return SEND_FAILED;
    }
}
