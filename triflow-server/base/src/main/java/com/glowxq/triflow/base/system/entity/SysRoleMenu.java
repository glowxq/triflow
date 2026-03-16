package com.glowxq.triflow.base.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联实体类
 * <p>
 * 对应数据库表 sys_role_menu，实现角色与菜单的多对多关系。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_role_menu")
@Schema(description = "角色菜单关联")
public class SysRoleMenu implements Serializable {

    /** 主键ID（自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /** 角色ID */
    @Schema(description = "角色ID")
    private Long roleId;

    /** 菜单ID */
    @Schema(description = "菜单ID")
    private Long menuId;

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

}
