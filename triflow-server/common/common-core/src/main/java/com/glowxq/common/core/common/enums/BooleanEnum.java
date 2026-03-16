package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通用布尔值枚举
 * <p>
 * 用于表示数据库中的布尔类型字段（0/1）。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum BooleanEnum implements BaseEnum {

    /** 否 */
    NO("0", "否"),

    /** 是 */
    YES("1", "是"),
    ;

    /** 值编码 */
    private final String code;

    /** 值名称 */
    private final String name;

    /**
     * 获取值（数据库存储值）
     *
     * @return 值
     */
    public Integer getValue() {
        return Integer.valueOf(code);
    }

    /**
     * 根据值获取枚举
     *
     * @param value 值
     * @return 对应的枚举，未匹配则返回 null
     */
    public static BooleanEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (BooleanEnum bool : values()) {
            if (bool.getCode().equals(code)) {
                return bool;
            }
        }
        return null;
    }

    /**
     * 判断是否为是
     *
     * @param value 值
     * @return true 表示是
     */
    public static boolean isYes(Integer value) {
        return YES.getValue().equals(value);
    }

    /**
     * 判断是否为否
     *
     * @param value 值
     * @return true 表示否
     */
    public static boolean isNo(Integer value) {
        return NO.getValue().equals(value);
    }
}
