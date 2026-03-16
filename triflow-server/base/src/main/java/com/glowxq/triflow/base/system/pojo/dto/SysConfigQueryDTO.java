package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.enums.ConfigTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统配置查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统配置查询请求")
public class SysConfigQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（配置名称模糊匹配） */
    @Schema(description = "搜索关键词", example = "密码")
    private String keyword;

    /**
     * 配置类型筛选
     *
     * @see ConfigTypeEnum
     */
    @Schema(description = "配置类型", example = "1", allowableValues = {"0", "1"})
    private Integer configType;

    /** 配置分类筛选 */
    @Schema(description = "配置分类", example = "user")
    private String category;

    /**
     * 状态筛选
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

}
