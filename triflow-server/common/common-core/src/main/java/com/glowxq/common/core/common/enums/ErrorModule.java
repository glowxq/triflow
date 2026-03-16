package com.glowxq.common.core.common.enums;

/**
 * 错误码模块编码常量
 * <p>
 * 定义系统中所有模块的编码，用于构建模块化的错误码。
 * 模块编码范围：00-99
 * </p>
 *
 * <h3>模块分配规则：</h3>
 * <ul>
 *   <li>00-09: 系统核心模块</li>
 *   <li>10-19: 基础服务模块</li>
 *   <li>20-29: 系统管理模块</li>
 *   <li>30-49: 预留</li>
 *   <li>50-99: 业务模块（各项目自定义）</li>
 * </ul>
 *
 * @author glowxq
 * @version 1.0
 * @since 2025/1/21
 */
public final class ErrorModule {

    /**
     * 通用/系统模块
     */
    public static final int SYSTEM = 0;

    // ==================== 系统核心模块 (00-09) ====================

    /**
     * 认证授权模块
     */
    public static final int AUTH = 1;

    /**
     * 参数校验模块
     */
    public static final int VALIDATION = 2;

    /**
     * 数据库模块
     */
    public static final int DATABASE = 3;

    /**
     * 缓存模块
     */
    public static final int CACHE = 4;

    /**
     * 文件存储模块
     */
    public static final int OSS = 5;

    /**
     * 日志模块
     */
    public static final int LOG = 6;

    /**
     * Excel模块
     */
    public static final int EXCEL = 7;

    /**
     * 短信模块
     */
    public static final int SMS = 8;

    /**
     * 微信模块
     */
    public static final int WECHAT = 9;

    /**
     * 外部服务调用模块
     */
    public static final int EXTERNAL = 10;

    // ==================== 基础服务模块 (10-19) ====================

    /**
     * 定时任务模块
     */
    public static final int SCHEDULER = 11;

    /**
     * 消息通知模块
     */
    public static final int NOTIFICATION = 12;

    /**
     * 用户管理模块
     */
    public static final int USER = 20;

    // ==================== 系统管理模块 (20-29) ====================

    /**
     * 角色权限模块
     */
    public static final int ROLE = 21;

    /**
     * 菜单管理模块
     */
    public static final int MENU = 22;

    /**
     * 部门管理模块
     */
    public static final int DEPT = 23;

    /**
     * 数据字典模块
     */
    public static final int DICT = 24;

    /**
     * 系统配置模块
     */
    public static final int CONFIG = 25;

    /**
     * 业务模块起始编码
     * <p>各业务项目从此编码开始定义自己的模块</p>
     */
    public static final int BUSINESS_START = 50;


    /**
     * AI 服务模块
     */
    public static final int AI = 13;

    // ==================== 业务模块起始值 (50+) ====================

    private ErrorModule() {
        // 防止实例化
    }

}
