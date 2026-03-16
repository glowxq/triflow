package com.glowxq.triflow.base.ai.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 配置 VO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "AI 配置信息")
public class AiConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "提供商代码")
    private String provider;

    @Schema(description = "提供商名称")
    private String providerName;

    @Schema(description = "API Key (脱敏)")
    private String apiKey;

    @Schema(description = "API 端点")
    private String endpoint;

    @Schema(description = "默认模型")
    private String defaultModel;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "是否为默认提供商")
    private Boolean isDefault;

    @Schema(description = "超时时间 (秒)")
    private Integer timeout;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
