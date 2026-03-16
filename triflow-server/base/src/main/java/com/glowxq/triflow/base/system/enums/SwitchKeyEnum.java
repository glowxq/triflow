package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统开关键枚举
 * <p>
 * 定义所有系统开关的唯一标识，便于类型安全的开关查询。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Getter
@AllArgsConstructor
public enum SwitchKeyEnum implements BaseEnum {

    // ========== 用户模块 ==========
    USER_REGISTER("user.register.enabled", "用户注册开关"),
    USER_LOGIN_PHONE("user.login.phone.enabled", "手机号登录开关"),
    USER_LOGIN_SOCIAL("user.login.social.enabled", "第三方登录开关"),
    USER_PROFILE_COMPLETE("user.profile.complete.enabled", "用户信息完善开关"),

    // ========== 安全模块 ==========
    SECURITY_CAPTCHA("security.captcha.enabled", "验证码开关"),

    // ========== 系统模块 ==========
    SYSTEM_MAINTENANCE("system.maintenance.enabled", "系统维护模式"),

    // ========== 微信模块 ==========
    WECHAT_COLLECT_PHONE("wechat.collect.phone.enabled", "微信登录收集手机号开关"),

    // ========== 钱包模块 ==========
    WALLET_SIGNIN_ENABLED("wallet.signin.enabled", "每日签到开关"),

    ;

    private final String code;
    private final String name;

    /**
     * 根据 code 获取枚举
     */
    public static SwitchKeyEnum fromCode(String code) {
        for (SwitchKeyEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 获取开关键
     */
    public String getKey() {
        return this.code;
    }
}
