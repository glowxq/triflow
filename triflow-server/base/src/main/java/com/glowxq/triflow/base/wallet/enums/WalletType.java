package com.glowxq.triflow.base.wallet.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包类型枚举
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Getter
@AllArgsConstructor
public enum WalletType implements BaseEnum {

    /** 积分 */
    POINTS("points", "积分"),

    /** 余额 */
    BALANCE("balance", "余额");

    /** 类型编码 */
    private final String code;

    /** 类型名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 类型编码
     * @return 对应枚举，未匹配返回 null
     */
    public static WalletType fromCode(String code) {
        for (WalletType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
