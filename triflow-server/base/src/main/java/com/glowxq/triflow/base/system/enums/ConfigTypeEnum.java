package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置类型枚举
 * <p>
 * 用于区分系统内置配置和业务配置。
 * 系统内置配置不可删除，业务配置可由用户自由管理。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum ConfigTypeEnum implements BaseEnum {

    /** 系统内置 - 核心配置，不可删除 */
    SYSTEM("0", "系统内置"),

    /** 业务配置 - 用户自定义配置，可删除 */
    BUSINESS("1", "业务配置"),
    ;

    /** 类型编码 */
    private final String code;

    /** 类型名称 */
    private final String name;

    /**
     * 获取类型值（数据库存储值）
     *
     * @return 类型值
     */
    public Integer getValue() {
        return Integer.valueOf(code);
    }

    /**
     * 根据类型值获取枚举
     *
     * @param value 类型值
     * @return 对应的枚举，未匹配则返回 null
     */
    public static ConfigTypeEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (ConfigTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为系统内置配置
     *
     * @param value 类型值
     * @return true 表示系统内置
     */
    public static boolean isSystem(Integer value) {
        return SYSTEM.getValue().equals(value);
    }
}
