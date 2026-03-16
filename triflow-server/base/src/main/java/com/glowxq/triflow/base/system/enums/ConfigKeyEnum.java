package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统配置键枚举
 * <p>
 * 定义系统配置项的唯一标识，便于类型安全的配置读取。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Getter
@AllArgsConstructor
public enum ConfigKeyEnum implements BaseEnum {

    // ========== 系统安全 ==========
    SYS_CAPTCHA_TYPE("sys.captcha.type", "验证码类型"),
    SYS_TOKEN_EXPIRE_TIME("sys.token.expireTime", "Token有效期(分钟)"),

    SYS_USER_DEFAULT_DATA_SCOPE("sys.user.defaultDataScope", "新用户默认数据权限"),

    // ========== 钱包模块 ==========
    WALLET_SIGNIN_POINTS("wallet.signin.points", "每日签到积分"),
    ;

    private final String code;
    private final String name;

    public static ConfigKeyEnum fromCode(String code) {
        for (ConfigKeyEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return null;
    }

    public String getKey() {
        return this.code;
    }
}
