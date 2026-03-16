package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import com.glowxq.common.core.common.exception.common.AlertsException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/7
 */
@Getter
@AllArgsConstructor
public enum DeleteFlagEnum implements BaseEnum {

    T("T", "已经删除"),

    F("F", "未删除"),
    ;

    private final String code;

    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link DeleteFlagEnum}
     */
    public static DeleteFlagEnum matchCode(String code) {
        for (DeleteFlagEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 EnvType枚举类型", code));
    }
}
