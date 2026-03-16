package com.glowxq.common.core.common.constant;

/**
 * 系统全局常量定义
 * <p>
 * 存放系统级别的通用常量，如包路径、系统属性键名等。
 * 注意：敏感配置（如密码、密钥）不应定义在此处，应通过配置文件或环境变量获取。
 * </p>
 *
 * @author glowxq
 * @version 1.1
 * @since 2025/4/22
 */
public interface Constant {

    // ==================== 包路径常量 ====================

    /**
     * 项目基础包名
     */
    String BASE_PACKAGE = "com.glowxq";

    /**
     * MyBatis Mapper 扫描路径
     */
    String BASE_MAPPER_PACKAGE = BASE_PACKAGE + ".**.mapper";

    // ==================== 系统属性常量 ====================

    /**
     * 用户工作目录属性键
     */
    String USER_DIR = "user.dir";

    // ==================== 路径常量 ====================

    /**
     * URL 路径分隔符
     */
    String URL_SEPARATOR = "/";

    /**
     * Windows 系统默认盘符
     */
    String WINDOWS_DIR = "D:";

    // ==================== 业务常量 ====================

    /**
     * 短信自动登录参数名
     */
    String SMS_AUTO_LOGIN = "autoLogin";

    // ==================== 字符常量 ====================

    /**
     * 空字符串
     */
    String EMPTY = "";

    /**
     * 逗号分隔符
     */
    String COMMA = ",";

    /**
     * 冒号分隔符
     */
    String COLON = ":";

    /**
     * 下划线
     */
    String UNDERSCORE = "_";

    /**
     * 连字符
     */
    String HYPHEN = "-";

    // ==================== 编码常量 ====================

    /**
     * UTF-8 编码
     */
    String UTF_8 = "UTF-8";

    // ==================== 数值常量 ====================

    /**
     * 默认页码
     */
    int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     */
    int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    int MAX_PAGE_SIZE = 100;
}
