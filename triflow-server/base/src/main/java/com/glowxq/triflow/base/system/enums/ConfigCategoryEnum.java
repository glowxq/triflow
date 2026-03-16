package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 配置分类枚举
 * <p>
 * 定义系统配置的分类，用于分组管理配置项。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Getter
@AllArgsConstructor
public enum ConfigCategoryEnum implements BaseEnum {

    /** 系统配置 - 系统级别的核心配置 */
    SYSTEM("system", "系统配置"),

    /** 安全配置 - 安全相关的配置项 */
    SECURITY("security", "安全配置"),

    /** 上传配置 - 文件上传相关配置 */
    UPLOAD("upload", "上传配置"),

    /** 邮件配置 - 邮件服务相关配置 */
    EMAIL("email", "邮件配置"),

    /** 短信配置 - 短信服务相关配置 */
    SMS("sms", "短信配置"),

    /** 支付配置 - 支付相关配置 */
    PAYMENT("payment", "支付配置"),

    /** 存储配置 - 存储服务相关配置 */
    STORAGE("storage", "存储配置"),

    /** 缓存配置 - 缓存相关配置 */
    CACHE("cache", "缓存配置"),

    /** 业务配置 - 业务相关配置 */
    BUSINESS("business", "业务配置"),

    /** 其他配置 - 未分类的配置项 */
    OTHER("other", "其他配置"),
    ;

    /** 分类编码 */
    private final String code;

    /** 分类名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static ConfigCategoryEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (ConfigCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}
