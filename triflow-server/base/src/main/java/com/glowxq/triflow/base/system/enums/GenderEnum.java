package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别枚举
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Getter
@AllArgsConstructor
public enum GenderEnum implements BaseEnum {

    /** 未知 */
    UNKNOWN("0", "未知"),

    /** 男 */
    MALE("1", "男"),

    /** 女 */
    FEMALE("2", "女");

    /** 性别编码 */
    private final String code;

    /** 性别名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 性别编码
     * @return 对应枚举，未匹配返回 null
     */
    public static GenderEnum of(String code) {
        for (GenderEnum gender : values()) {
            if (gender.code.equals(code)) {
                return gender;
            }
        }
        return null;
    }
}
