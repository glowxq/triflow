package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色查询请求")
public class SysRoleQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（角色编码/名称模糊匹配） */
    @Schema(description = "搜索关键词", example = "admin")
    private String keyword;

    /** 状态筛选 */
    @Schema(description = "状态筛选", example = "1")
    private Integer status;

}
