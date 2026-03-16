package com.glowxq.triflow.base.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 枚举选项 VO
 * <p>
 * 用于前端下拉选择组件的通用枚举选项。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "枚举选项")
public class EnumItem {

    /**
     * 枚举编码
     */
    @Schema(description = "枚举编码", example = "1")
    private String code;

    /**
     * 枚举名称
     */
    @Schema(description = "枚举名称", example = "启用")
    private String name;
}
