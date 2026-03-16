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
public enum AppEnum implements BaseEnum {

    system("system", "基础系统"),


    ;

    /**
     * CODE
     */
    private final String code;

    /**
     * 名字
     */
    private final String name;

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link AppEnum}
     */
    public static AppEnum matchCode(String code) {
        for (AppEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 AppType 枚举类型", code));
    }

    /**
     * 获取Redis prefix
     *
     * @return {@link String }
     */
    public String getRedisKeyPrefix() {
        return this.getCode() + ":";
    }

    /**
     * 是业务应用程序
     *
     * @return {@link Boolean }
     */
    public Boolean isBusinessApp() {
        return !isSystemApp();
    }

    /**
     * 是admin app
     *
     * @return {@link Boolean}
     */
    public Boolean isSystemApp() {
        return AppEnum.system.equals(this);
    }
}
