package com.glowxq.triflow.base.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 配置版本 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "配置版本信息")
public class ConfigVersionVO {

    @Schema(description = "应用版本号", example = "1.0.0")
    private String appVersion;

    @Schema(description = "前端 preferences 缓存版本号，修改此值可强制前端清除缓存", example = "1")
    private String preferencesVersion;
}
