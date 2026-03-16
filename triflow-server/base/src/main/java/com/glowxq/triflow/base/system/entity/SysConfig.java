package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.enums.ConfigTypeEnum;
import com.glowxq.triflow.base.system.enums.ValueTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * <p>
 * 对应数据库表 sys_config，存储系统级和业务级的配置项。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("sys_config")
@Schema(description = "系统配置")
public class SysConfig implements BaseEntity {

    /**
     * 配置ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "配置ID")
    private Long id;

    /**
     * 配置名称
     */
    @Schema(description = "配置名称")
    private String configName;

    /**
     * 配置键（唯一标识，如: sys.user.initPassword）
     */
    @Schema(description = "配置键")
    private String configKey;

    /**
     * 配置值（支持JSON格式）
     */
    @Schema(description = "配置值")
    private String configValue;

    /**
     * 值类型
     *
     * @see ValueTypeEnum
     */
    @Schema(description = "值类型")
    private String valueType;

    /**
     * 配置类型
     *
     * @see ConfigTypeEnum
     */
    @Schema(description = "配置类型")
    private Integer configType;

    /**
     * 配置分类（用于分组显示，如: security, upload, email）
     */
    @Schema(description = "配置分类")
    private String category;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序")
    private Integer sort;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;

    // ========== 必需标准字段 ==========

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

    /** 数据权限部门ID */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    private Long createBy;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新者ID */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;

}
