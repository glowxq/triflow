package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.enums.ConfigTypeEnum;
import com.glowxq.triflow.base.system.enums.ValueTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "系统配置响应")
@AutoMapper(target = SysConfig.class)
public class SysConfigVO implements BaseVO {

    /**
     * 配置ID
     */
    @Schema(description = "配置ID", example = "1")
    private Long id;

    /**
     * 配置名称
     */
    @Schema(description = "配置名称", example = "用户初始密码")
    private String configName;

    /**
     * 配置键
     */
    @Schema(description = "配置键", example = "sys.user.initPassword")
    private String configKey;

    /**
     * 配置值
     */
    @Schema(description = "配置值", example = "123456")
    private String configValue;

    /**
     * 值类型
     *
     * @see ValueTypeEnum
     */
    @Schema(description = "值类型", example = "string")
    private String valueType;

    /**
     * 配置类型
     *
     * @see ConfigTypeEnum
     */
    @Schema(description = "配置类型", example = "1")
    private Integer configType;

    /**
     * 配置分类
     */
    @Schema(description = "配置分类", example = "user")
    private String category;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序", example = "0")
    private Integer sort;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注说明", example = "这是备注")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
