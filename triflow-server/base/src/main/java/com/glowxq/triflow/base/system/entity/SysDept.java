package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门实体类
 * <p>
 * 对应数据库表 sys_dept，存储组织架构信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_dept")
@Schema(description = "系统部门")
public class SysDept implements BaseEntity {

    /** 部门ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "部门ID")
    private Long id;

    /** 父部门ID（0表示根部门） */
    @Schema(description = "父部门ID")
    private Long parentId;

    /** 祖级列表（逗号分隔，如: 0,1,2） */
    @Schema(description = "祖级列表")
    private String ancestors;

    /** 部门名称 */
    @Schema(description = "部门名称")
    private String deptName;

    /** 显示排序 */
    @Schema(description = "排序")
    private Integer sort;

    /** 负责人 */
    @Schema(description = "负责人")
    private String leader;

    /** 联系电话 */
    @Schema(description = "联系电话")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
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
