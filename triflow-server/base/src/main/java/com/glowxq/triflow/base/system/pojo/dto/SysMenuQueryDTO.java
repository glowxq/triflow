package com.glowxq.triflow.base.system.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "菜单查询请求")
public class SysMenuQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词 */
    @Schema(description = "搜索关键词", example = "用户")
    private String keyword;

    /** 菜单类型筛选 (M:目录, C:菜单, F:按钮) */
    @Schema(description = "菜单类型筛选", example = "C")
    private String menuType;

    /** 状态筛选 */
    @Schema(description = "状态筛选", example = "1")
    private Integer status;

}
