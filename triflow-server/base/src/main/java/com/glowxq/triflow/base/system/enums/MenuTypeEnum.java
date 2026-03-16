package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 菜单类型枚举
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Getter
@AllArgsConstructor
public enum MenuTypeEnum implements BaseEnum {

    /** 目录 */
    DIRECTORY("M", "目录"),

    /** 菜单 */
    MENU("C", "菜单"),

    /** 按钮 */
    BUTTON("F", "按钮");

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
    public static MenuTypeEnum of(String code) {
        for (MenuTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
