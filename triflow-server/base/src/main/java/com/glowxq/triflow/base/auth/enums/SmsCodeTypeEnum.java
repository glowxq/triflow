package com.glowxq.triflow.base.auth.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信验证码类型枚举
 * <p>
 * 定义短信验证码的用途类型，用于区分不同业务场景。
 * </p>
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Getter
@AllArgsConstructor
public enum SmsCodeTypeEnum implements BaseEnum {

    /**
     * 登录验证码
     */
    LOGIN("login", "登录验证码"),

    /**
     * 注册验证码
     */
    REGISTER("register", "注册验证码"),

    /**
     * 重置密码验证码
     */
    RESET("reset", "重置密码验证码"),
    ;

    /**
     * 验证码类型代码
     */
    @JsonValue
    private final String code;

    /**
     * 验证码类型名称
     */
    private final String name;

    /**
     * 根据代码获取枚举
     *
     * @param code 验证码类型代码
     * @return 枚举值，未找到则返回 null
     */
    @JsonCreator
    public static SmsCodeTypeEnum of(String code) {
        for (SmsCodeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
