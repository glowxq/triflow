package com.glowxq.common.mysql.context;

import com.glowxq.common.core.common.enums.DataScopeEnum;

import java.util.*;

/**
 * 数据权限 ThreadLocal 工具类
 * <p>
 * 用于在线程上下文中存储和管理数据权限相关的配置信息。
 * 每次请求结束后，必须调用 {@link #clear()} 释放资源，防止内存泄漏。
 * </p>
 *
 * @author glowxq
 * @since 2024/7/12
 */
public class DataScopeThreadLocal {

    /** 数据权限启用状态 */
    private static final ThreadLocal<Boolean> DATA_SCOPE_ENABLED = ThreadLocal.withInitial(() -> false);

    /** 超级管理员过滤状态 */
    private static final ThreadLocal<Boolean> SUPER_ADMIN_FILTER = ThreadLocal.withInitial(() -> false);

    /** 忽略数据权限过滤的表集合 */
    private static final ThreadLocal<Set<String>> IGNORE_TABLES = ThreadLocal.withInitial(HashSet::new);

    /** 当前线程的数据权限枚举 */
    private static final ThreadLocal<DataScopeEnum> DATA_SCOPE_ENUM = new ThreadLocal<>();

    /** 数据权限关联的 ID 列表（如部门 ID 列表） */
    private static final ThreadLocal<List<Long>> SCOPE_IDS = ThreadLocal.withInitial(ArrayList::new);

    private DataScopeThreadLocal() {
        // 工具类禁止实例化
    }

    // ==================== 数据权限启用状态 ====================

    /**
     * 设置数据权限启用状态
     *
     * @param enabled 是否启用数据权限
     */
    public static void setDataScopeEnabled(boolean enabled) {
        DATA_SCOPE_ENABLED.set(enabled);
    }

    /**
     * 获取数据权限启用状态
     *
     * @return 是否启用数据权限
     */
    public static boolean isDataScopeEnabled() {
        return Boolean.TRUE.equals(DATA_SCOPE_ENABLED.get());
    }

    // ==================== 超级管理员过滤 ====================

    /**
     * 设置超级管理员过滤状态
     *
     * @param filter 是否对超级管理员进行数据权限过滤
     */
    public static void setSuperAdminFilter(boolean filter) {
        SUPER_ADMIN_FILTER.set(filter);
    }

    /**
     * 获取超级管理员过滤状态
     *
     * @return 是否对超级管理员进行数据权限过滤
     */
    public static boolean isSuperAdminFilter() {
        return Boolean.TRUE.equals(SUPER_ADMIN_FILTER.get());
    }

    // ==================== 忽略表 ====================

    /**
     * 添加需要忽略数据权限过滤的表
     *
     * @param tables 表名数组
     */
    public static void addIgnoreTables(String... tables) {
        if (tables != null && tables.length > 0) {
            IGNORE_TABLES.get().addAll(Arrays.asList(tables));
        }
    }

    /**
     * 添加需要忽略数据权限过滤的表
     *
     * @param tables 表名列表
     */
    public static void addIgnoreTables(List<String> tables) {
        if (tables != null && !tables.isEmpty()) {
            IGNORE_TABLES.get().addAll(tables);
        }
    }

    /**
     * 检查表是否在忽略列表中
     *
     * @param tableName 表名
     * @return 是否忽略该表的数据权限过滤
     */
    public static boolean isTableIgnored(String tableName) {
        return tableName != null && IGNORE_TABLES.get().contains(tableName);
    }

    // ==================== 数据权限枚举 ====================

    /**
     * 获取当前线程的数据权限枚举
     *
     * @return 数据权限枚举，可能为 null
     */
    public static DataScopeEnum getDataScopeEnum() {
        return DATA_SCOPE_ENUM.get();
    }

    /**
     * 设置当前线程的数据权限枚举
     *
     * @param dataScope 数据权限枚举
     */
    public static void setDataScopeEnum(DataScopeEnum dataScope) {
        DATA_SCOPE_ENUM.set(dataScope);
    }

    // ==================== 权限关联 ID ====================

    /**
     * 获取数据权限关联的 ID 列表
     *
     * @return ID 列表（不会为 null）
     */
    public static List<Long> getScopeIds() {
        return SCOPE_IDS.get();
    }

    /**
     * 批量添加数据权限关联 ID
     *
     * @param ids ID 列表
     */
    public static void addScopeIds(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            SCOPE_IDS.get().addAll(ids);
        }
    }

    /**
     * 添加单个数据权限关联 ID
     *
     * @param id 部门 ID 等
     */
    public static void addScopeId(Long id) {
        if (id != null) {
            SCOPE_IDS.get().add(id);
        }
    }

    // ==================== 清理 ====================

    /**
     * 清除所有 ThreadLocal 数据
     * <p>
     * 必须在请求结束时调用，防止线程复用导致的数据泄漏。
     * </p>
     */
    public static void clear() {
        DATA_SCOPE_ENABLED.remove();
        SUPER_ADMIN_FILTER.remove();
        IGNORE_TABLES.remove();
        DATA_SCOPE_ENUM.remove();
        SCOPE_IDS.remove();
    }

}
