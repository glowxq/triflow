package com.glowxq.triflow.base.ai.pojo.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Prompt 模板查询参数
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@Schema(description = "Prompt 模板查询参数")
public class PromptTemplateQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板代码")
    private String code;

    @Schema(description = "模板分类")
    private String category;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
