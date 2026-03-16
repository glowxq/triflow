package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 灰度发布类型枚举
 * <p>
 * 定义开关的灰度发布策略。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum GrayscaleTypeEnum implements BaseEnum {

    /** 全量发布 - 对所有用户生效 */
    ALL("0", "全量"),

    /** 按用户 - 仅对指定用户生效 */
    USER("1", "按用户"),

    /** 按百分比 - 按比例随机生效 */
    PERCENTAGE("2", "按百分比"),
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
    public static GrayscaleTypeEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (GrayscaleTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为全量发布
     *
     * @param value 类型值
     * @return true 表示全量发布
     */
    public static boolean isAll(Integer value) {
        return ALL.getValue().equals(value);
    }
}
