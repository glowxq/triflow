package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.enums.SwitchScopeEnum;
import com.glowxq.triflow.base.system.enums.SwitchStrategyEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统开关响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "系统开关响应")
@AutoMapper(target = SysSwitch.class)
public class SysSwitchVO implements BaseVO {

    /**
     * 开关ID
     */
    @Schema(description = "开关ID", example = "1")
    private Long id;

    /**
     * 开关名称
     */
    @Schema(description = "开关名称", example = "用户注册开关")
    private String switchName;

    /**
     * 开关键
     */
    @Schema(description = "开关键", example = "user.register.enabled")
    private String switchKey;

    /**
     * 开关状态
     *
     * @see StatusEnum
     */
    @Schema(description = "开关状态", example = "1")
    private Integer switchValue;

    /**
     * 开关类型
     *
     * @see SwitchTypeEnum
     */
    @Schema(description = "开关类型", example = "feature")
    private String switchType;

    /**
     * 开关分类
     */
    @Schema(description = "开关分类", example = "user")
    private String category;

    /**
     * 作用范围
     *
     * @see SwitchScopeEnum
     */
    @Schema(description = "作用范围", example = "global")
    private String scope;

    /**
     * 生效策略
     *
     * @see SwitchStrategyEnum
     */
    @Schema(description = "生效策略", example = "all")
    private String strategy;

    /**
     * 白名单配置
     */
    @Schema(description = "白名单配置", example = "[1,2,3]")
    private String whitelist;

    /**
     * 灰度百分比
     */
    @Schema(description = "灰度百分比", example = "100")
    private Integer percentage;

    /**
     * 生效开始时间
     */
    @Schema(description = "生效开始时间")
    private LocalDateTime startTime;

    /**
     * 生效结束时间
     */
    @Schema(description = "生效结束时间")
    private LocalDateTime endTime;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序", example = "0")
    private Integer sort;

    /**
     * 开关描述
     */
    @Schema(description = "开关描述", example = "控制用户注册功能")
    private String description;

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
