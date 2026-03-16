package com.glowxq.triflow.base.wallet.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包操作类型枚举
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Getter
@AllArgsConstructor
public enum WalletAction implements BaseEnum {

    /** 收入 */
    INCOME("income", "收入"),

    /** 支出 */
    EXPENSE("expense", "支出"),

    /** 冻结 */
    FREEZE("freeze", "冻结"),

    /** 解冻 */
    UNFREEZE("unfreeze", "解冻");

    /** 操作编码 */
    private final String code;

    /** 操作名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 操作编码
     * @return 对应枚举，未匹配返回 null
     */
    public static WalletAction fromCode(String code) {
        for (WalletAction action : values()) {
            if (action.code.equals(code)) {
                return action;
            }
        }
        return null;
    }
}
