package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements BaseEnum {

    /** 禁用 */
    DISABLED("0", "禁用"),

    /** 正常 */
    ENABLED("1", "正常"),

    /** 锁定 */
    LOCKED("2", "锁定");

    /** 状态编码 */
    private final String code;

    /** 状态名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 状态编码
     * @return 对应枚举，未匹配返回 null
     */
    public static UserStatusEnum of(String code) {
        for (UserStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
