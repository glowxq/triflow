package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.enums.ConfigTypeEnum;
import com.glowxq.triflow.base.system.enums.ValueTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 系统配置创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "系统配置创建请求")
@AutoMapper(target = SysConfig.class)
public class SysConfigCreateDTO implements BaseDTO {

    /**
     * 配置名称
     */
    @Schema(description = "配置名称", example = "用户初始密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100字符")
    private String configName;

    /**
     * 配置键
     */
    @Schema(description = "配置键", example = "sys.user.initPassword", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100字符")
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
    @Schema(description = "值类型", example = "string", defaultValue = "string")
    private String valueType;

    /**
     * 配置类型
     *
     * @see ConfigTypeEnum
     */
    @Schema(description = "配置类型", example = "1", defaultValue = "1")
    private Integer configType;

    /**
     * 配置分类
     */
    @Schema(description = "配置分类", example = "user")
    @Size(max = 50, message = "配置分类长度不能超过50字符")
    private String category;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序", example = "0", defaultValue = "0")
    private Integer sort;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1", defaultValue = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注说明", example = "这是备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysConfig.class);
    }

}
