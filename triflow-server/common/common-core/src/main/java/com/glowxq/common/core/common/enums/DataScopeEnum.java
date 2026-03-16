package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/6/7
 */
@Getter
@AllArgsConstructor
public enum DataScopeEnum implements BaseEnum {

    All("All", "所有", ""),
    DeptAndChildren("DeptAndChildren", "本部门及下级部门", "dept_id"),
    Dept("Dept", "本部门", "dept_id"),
    UserCreate("UserCreate", "本人创建", "create_by"),
    JoinGroup("JoinGroup", "加入的用户组", "group_id"),
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
     * 字段
     */
    private final String field;

    public String getFieldUp() {
        return field.toUpperCase();
    }

    /**
     * 匹配代码
     *
     * @param code
     * @return {@link DataScopeEnum}
     */
    public static DataScopeEnum matchCode(String code) {
        for (DataScopeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return All;
    }
}
