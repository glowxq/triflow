package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 开关作用范围枚举
 * <p>
 * 定义开关的作用范围级别。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum SwitchScopeEnum implements BaseEnum {

    /** 全局 - 对所有租户和用户生效 */
    GLOBAL("global", "全局"),

    /** 租户级 - 对指定租户生效 */
    TENANT("tenant", "租户级"),

    /** 用户级 - 对指定用户生效 */
    USER("user", "用户级"),
    ;

    /** 范围编码 */
    private final String code;

    /** 范围名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static SwitchScopeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (SwitchScopeEnum scope : values()) {
            if (scope.getCode().equals(code)) {
                return scope;
            }
        }
        return null;
    }
}
