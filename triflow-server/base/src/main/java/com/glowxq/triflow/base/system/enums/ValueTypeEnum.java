package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置值类型枚举
 * <p>
 * 定义配置值的数据类型，用于前端渲染和值校验。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum ValueTypeEnum implements BaseEnum {

    /** 字符串类型 */
    STRING("string", "字符串"),

    /** 数字类型 */
    NUMBER("number", "数字"),

    /** 布尔类型 */
    BOOLEAN("boolean", "布尔"),

    /** JSON对象类型 */
    JSON("json", "JSON对象"),

    /** 数组类型 */
    ARRAY("array", "数组"),
    ;

    /** 类型编码 */
    private final String code;

    /** 类型名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static ValueTypeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (ValueTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 获取默认值类型
     *
     * @return 默认为字符串类型
     */
    public static ValueTypeEnum defaultType() {
        return STRING;
    }
}
