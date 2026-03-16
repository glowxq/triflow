package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.enums.SwitchCategoryEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 系统开关查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统开关查询请求")
public class SysSwitchQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（开关名称/标识模糊匹配） */
    @Schema(description = "搜索关键词", example = "wechat.collect.phone.enabled")
    private String keyword;

    /** 开关标识（精确匹配） */
    @Schema(description = "开关标识", example = "wechat.collect.phone.enabled")
    private String switchKey;

    /**
     * 开关类型筛选
     *
     * @see SwitchTypeEnum
     */
    @Schema(description = "开关类型（feature:功能开关, gray:灰度开关, degrade:降级开关, experiment:实验开关）", example = "feature")
    private String switchType;

    /**
     * 开关分类筛选
     *
     * @see SwitchCategoryEnum
     */
    @Schema(description = "开关分类", example = "user")
    private String category;

    /**
     * 开关状态筛选
     *
     * @see StatusEnum
     */
    @Schema(description = "开关状态", example = "1", allowableValues = {"0", "1"})
    private Integer switchValue;

}
