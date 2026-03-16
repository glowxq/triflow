package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 开关类型枚举
 * <p>
 * 定义系统开关的类型。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum SwitchTypeEnum implements BaseEnum {

    /** 功能开关 - 用于控制功能的启用/禁用 */
    FEATURE("feature", "功能开关"),

    /** 灰度开关 - 用于灰度发布控制 */
    GRAY("gray", "灰度开关"),

    /** 降级开关 - 用于服务降级控制 */
    DEGRADE("degrade", "降级开关"),

    /** 实验开关 - 用于A/B测试等实验功能 */
    EXPERIMENT("experiment", "实验开关"),
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
    public static SwitchTypeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (SwitchTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
