package com.glowxq.common.core.common.constant;

/**
 * 全局常量
 *
 * @author glowxq
 * @version 1.1
 * @since 2022/8/23
 */
public class GlobalConstant {

    /**
     * Redis 发布订阅 channel（用于权限变更通知）
     */
    public static final String CHANGE_PERMISSIONS_SIGNAL = "change_permissions_signal";

    public static final String UTF_8 = "utf-8";

    private GlobalConstant() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 超级管理员角色标识
     */
    public static final String SUPER_ROLE = "admin";

}
