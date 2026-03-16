package com.glowxq.triflow.base.auth.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 短信服务商枚举
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum SmsProviderEnum implements BaseEnum {

    /** SMS4J 统一短信服务 */
    SMS4J("sms4j", "SMS4J 短信服务"),

    /** 阿里云号码认证服务 */
    ALIYUN_PNVS("aliyun-pnvs", "阿里云号码认证服务"),
    ;

    /** 服务商编码 */
    private final String code;

    /** 服务商名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static SmsProviderEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (SmsProviderEnum provider : values()) {
            if (provider.getCode().equals(code)) {
                return provider;
            }
        }
        return null;
    }
}
