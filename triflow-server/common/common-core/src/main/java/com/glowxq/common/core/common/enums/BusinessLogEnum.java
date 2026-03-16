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
public enum BusinessLogEnum implements BaseEnum {

    /**
     * 创建
     */
    Create("Create", "创建"),
    /**
     * 更新
     */
    Update("Update", "更新"),
    /**
     * 删除
     */
    Delete("Delete", "删除"),

    /**
     * 查询
     */
    Query("Query", "查询"),
    /**
     * 导出
     */
    Export("Export", "导出"),
    /**
     * 导入
     */
    Import("Import", "导入"),
    /**
     * 登录
     */
    Login("Login", "登录"),
    /**
     * 登出
     */
    Logout("Logout", "登出"),
    /**
     * 其他
     */
    Other("Other", "其他"),

    NONE("none", "未设置");

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
     * @return {@link BusinessLogEnum}
     */
    public static BusinessLogEnum matchCode(String code) {
        for (BusinessLogEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new AlertsException(String.format("%s 未匹配到对应 AppType 枚举类型", code));
    }
}
