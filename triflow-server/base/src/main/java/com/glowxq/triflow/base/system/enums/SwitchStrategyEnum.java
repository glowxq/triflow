package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 开关生效策略枚举
 * <p>
 * 定义开关的生效策略。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum SwitchStrategyEnum implements BaseEnum {

    /** 全部 - 对所有用户生效 */
    ALL("all", "全部"),

    /** 白名单 - 仅对白名单中的用户生效 */
    WHITELIST("whitelist", "白名单"),

    /** 百分比 - 按百分比随机生效 */
    PERCENTAGE("percentage", "百分比"),

    /** 定时 - 在指定时间段内生效 */
    SCHEDULE("schedule", "定时"),
    ;

    /** 策略编码 */
    private final String code;

    /** 策略名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static SwitchStrategyEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (SwitchStrategyEnum strategy : values()) {
            if (strategy.getCode().equals(code)) {
                return strategy;
            }
        }
        return null;
    }
}
