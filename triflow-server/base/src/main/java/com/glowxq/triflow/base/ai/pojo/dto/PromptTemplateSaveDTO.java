package com.glowxq.triflow.base.ai.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Prompt 模板保存 DTO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "Prompt 模板保存参数")
public class PromptTemplateSaveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID (更新时必填)")
    private Long id;

    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "模板代码不能为空")
    @Schema(description = "模板代码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Schema(description = "模板分类")
    private String category;

    @NotBlank(message = "系统提示不能为空")
    @Schema(description = "系统提示", requiredMode = Schema.RequiredMode.REQUIRED)
    private String systemPrompt;

    @Schema(description = "用户提示模板")
    private String userPromptTemplate;

    @Schema(description = "模板变量 (JSON 数组)")
    private String variables;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否启用", example = "true")
    private Boolean enabled = true;

    @Schema(description = "排序", example = "0")
    private Integer sort = 0;
}
