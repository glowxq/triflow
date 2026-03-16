package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.enums.SwitchScopeEnum;
import com.glowxq.triflow.base.system.enums.SwitchStrategyEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统开关创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "系统开关创建请求")
@AutoMapper(target = SysSwitch.class)
public class SysSwitchCreateDTO implements BaseDTO {

    /**
     * 开关名称
     */
    @Schema(description = "开关名称", example = "用户注册开关", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开关名称不能为空")
    @Size(max = 100, message = "开关名称长度不能超过100字符")
    private String switchName;

    /**
     * 开关键
     */
    @Schema(description = "开关键", example = "user.register.enabled", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开关键不能为空")
    @Size(max = 100, message = "开关键长度不能超过100字符")
    private String switchKey;

    /**
     * 开关状态
     *
     * @see StatusEnum
     */
    @Schema(description = "开关状态", example = "1", defaultValue = "1")
    private Integer switchValue;

    /**
     * 开关类型
     *
     * @see SwitchTypeEnum
     */
    @Schema(description = "开关类型", example = "feature", defaultValue = "feature")
    private String switchType;

    /**
     * 开关分类
     */
    @Schema(description = "开关分类", example = "user")
    @Size(max = 50, message = "开关分类长度不能超过50字符")
    private String category;

    /**
     * 作用范围
     *
     * @see SwitchScopeEnum
     */
    @Schema(description = "作用范围", example = "global", defaultValue = "global")
    private String scope;

    /**
     * 生效策略
     *
     * @see SwitchStrategyEnum
     */
    @Schema(description = "生效策略", example = "all", defaultValue = "all")
    private String strategy;

    /** 白名单配置 */
    @Schema(description = "白名单配置（JSON格式）", example = "[1, 2, 3]")
    private String whitelist;

    /** 灰度百分比 */
    @Schema(description = "灰度百分比（0-100）", example = "100", defaultValue = "100")
    private Integer percentage;

    /** 生效开始时间 */
    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    /** 生效结束时间 */
    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    /** 显示排序 */
    @Schema(description = "显示排序", example = "0", defaultValue = "0")
    private Integer sort;

    /** 开关描述 */
    @Schema(description = "开关描述")
    @Size(max = 500, message = "开关描述长度不能超过500字符")
    private String description;

    /** 备注 */
    @Schema(description = "备注说明")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysSwitch.class);
    }

}
