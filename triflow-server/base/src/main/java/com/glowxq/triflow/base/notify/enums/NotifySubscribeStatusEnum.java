package com.glowxq.triflow.base.notify.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订阅状态枚举
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum NotifySubscribeStatusEnum implements BaseEnum {

    /** 用户同意订阅 */
    ACCEPT("accept", "已同意"),

    /** 用户拒绝订阅 */
    REJECT("reject", "已拒绝"),

    /** 用户被平台拒绝订阅 */
    BAN("ban", "已禁止"),
    ;

    private final String code;
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static NotifySubscribeStatusEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (NotifySubscribeStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
