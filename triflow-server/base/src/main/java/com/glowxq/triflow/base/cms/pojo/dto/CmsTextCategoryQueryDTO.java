package com.glowxq.triflow.base.cms.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文本分类查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本分类查询请求")
public class CmsTextCategoryQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "父分类ID")
    private Long pid;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "页码", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;

}
