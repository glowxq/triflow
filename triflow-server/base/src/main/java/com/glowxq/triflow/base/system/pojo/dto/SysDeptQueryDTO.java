package com.glowxq.triflow.base.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "部门查询请求")
public class SysDeptQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词 */
    @Schema(description = "搜索关键词", example = "研发")
    private String keyword;

    /** 状态筛选 */
    @Schema(description = "状态筛选", example = "1")
    private Integer status;

}
