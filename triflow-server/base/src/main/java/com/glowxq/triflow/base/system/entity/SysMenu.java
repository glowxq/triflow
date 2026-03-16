package com.glowxq.triflow.base.system.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.enums.MenuTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单权限实体类
 * <p>
 * 对应数据库表 sys_menu，严格匹配数据库字段。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_menu")
@Schema(description = "系统菜单")
public class SysMenu implements BaseEntity {

    /** 菜单ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "菜单ID")
    private Long id;

    /** 父菜单ID（0表示根菜单） */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String menuName;

    /**
     * 菜单类型
     *
     * @see MenuTypeEnum
     */
    @Schema(description = "菜单类型")
    private String menuType;

    /** 路由路径 */
    @Schema(description = "路由路径")
    private String path;

    /** 路由名称（前端使用） */
    @Schema(description = "路由名称")
    private String name;

    /** 组件路径 */
    @Schema(description = "组件路径")
    private String component;

    /** 重定向地址 */
    @Schema(description = "重定向地址")
    private String redirect;

    /** 权限标识（如: system:user:list） */
    @Schema(description = "权限标识")
    private String permission;

    /** 菜单图标 */
    @Schema(description = "图标")
    private String icon;

    /** 显示排序 */
    @Schema(description = "排序")
    private Integer sort;

    /**
     * 是否可见
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否可见")
    private Integer visible;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 是否外链
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否外链")
    private Integer isFrame;

    /**
     * 是否缓存
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否缓存")
    private Integer isCache;

    /**
     * 是否固定标签页
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否固定标签页")
    private Integer isAffix;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    // ========== 标准字段 ==========

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
