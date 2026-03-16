package com.glowxq.triflow.base.ai.pojo.vo;

import com.glowxq.triflow.base.ai.entity.SysPromptTemplate;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Prompt 模板 VO
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Data
@AutoMapper(target = SysPromptTemplate.class)
@Schema(description = "Prompt 模板信息")
public class PromptTemplateVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "模板代码")
    private String code;

    @Schema(description = "模板分类")
    private String category;

    @Schema(description = "系统提示")
    private String systemPrompt;

    @Schema(description = "用户提示模板")
    private String userPromptTemplate;

    @Schema(description = "模板变量 (JSON 数组)")
    private String variables;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
