package com.glowxq.triflow.base.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户角色关联实体类
 * <p>
 * 对应数据库表 sys_user_role，实现用户与角色的多对多关系。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_user_role")
@Schema(description = "用户角色关联")
public class SysUserRole implements Serializable {

    /** 主键ID（自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

}
