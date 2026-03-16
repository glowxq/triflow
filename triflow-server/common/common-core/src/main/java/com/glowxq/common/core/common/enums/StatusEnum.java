package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用状态枚举
 * <p>
 * 用于表示实体的启用/禁用状态。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum StatusEnum implements BaseEnum {

    /** 禁用 - 实体处于禁用状态，不可用 */
    DISABLED("0", "禁用"),

    /** 正常 - 实体处于正常状态，可用 */
    ENABLED("1", "正常"),
    ;

    /** 状态编码 */
    private final String code;

    /** 状态名称 */
    private final String name;

    /**
     * 获取状态值（数据库存储值）
     *
     * @return 状态值
     */
    public Integer getValue() {
        return Integer.valueOf(code);
    }

    /**
     * 根据状态值获取枚举
     *
     * @param value 状态值
     * @return 对应的枚举，未匹配则返回 null
     */
    public static StatusEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (StatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否启用
     *
     * @param value 状态值
     * @return true 表示启用
     */
    public static boolean isEnabled(Integer value) {
        return ENABLED.getValue().equals(value);
    }

    /**
     * 判断是否禁用
     *
     * @param value 状态值
     * @return true 表示禁用
     */
    public static boolean isDisabled(Integer value) {
        return DISABLED.getValue().equals(value);
    }
}
