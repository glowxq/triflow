package com.glowxq.triflow.base.wallet.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 钱包充值订单状态枚举
 *
 * @author glowxq
 * @since 2025-02-10
 */
@Getter
@AllArgsConstructor
public enum WalletRechargeStatus implements BaseEnum {

    /** 待支付 */
    PENDING("pending", "待支付"),

    /** 已支付 */
    PAID("paid", "已支付"),

    /** 已退款 */
    REFUNDED("refunded", "已退款"),

    /** 已关闭 */
    CLOSED("closed", "已关闭"),

    /** 支付失败 */
    FAILED("failed", "支付失败");

    private final String code;
    private final String name;

    public static WalletRechargeStatus fromCode(String code) {
        for (WalletRechargeStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
