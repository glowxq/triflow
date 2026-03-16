package com.glowxq.triflow.base.ai.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 配置实体
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Table("sys_ai_config")
public class SysAiConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 提供商代码
     */
    private String provider;

    /**
     * API Key
     */
    private String apiKey;

    /**
     * API 端点
     */
    private String endpoint;

    /**
     * 默认模型
     */
    private String defaultModel;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否为默认提供商
     */
    private Boolean isDefault;

    /**
     * 超时时间 (秒)
     */
    private Integer timeout;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
