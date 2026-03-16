package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型枚举
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Getter
@AllArgsConstructor
public enum OperationTypeEnum implements BaseEnum {

    /** 新增 */
    CREATE("create", "新增"),

    /** 修改 */
    UPDATE("update", "修改"),

    /** 删除 */
    DELETE("delete", "删除"),

    /** 查询 */
    QUERY("query", "查询"),

    /** 登录 */
    LOGIN("login", "登录"),

    /** 登出 */
    LOGOUT("logout", "登出"),

    /** 导入 */
    IMPORT("import", "导入"),

    /** 导出 */
    EXPORT("export", "导出"),

    /** 其他 */
    OTHER("other", "其他");

    /** 操作编码 */
    private final String code;

    /** 操作名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 操作编码
     * @return 对应枚举，未匹配返回 null
     */
    public static OperationTypeEnum of(String code) {
        for (OperationTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

}
