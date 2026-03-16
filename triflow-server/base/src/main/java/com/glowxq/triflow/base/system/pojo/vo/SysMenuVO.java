package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.SysMenu;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单响应 VO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "菜单响应")
@AutoMapper(target = SysMenu.class)
public class SysMenuVO implements BaseVO {

    /** 菜单ID */
    @Schema(description = "菜单ID", example = "1")
    private Long id;

    /** 父菜单ID */
    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    /** 菜单名称 */
    @Schema(description = "菜单名称", example = "用户管理")
    private String menuName;

    /** 菜单类型 (M:目录, C:菜单, F:按钮) */
    @Schema(description = "菜单类型", example = "C")
    private String menuType;

    /** 路由路径 */
    @Schema(description = "路由路径", example = "/system/user")
    private String path;

    /** 路由名称 */
    @Schema(description = "路由名称", example = "SystemUser")
    private String name;

    /** 组件路径 */
    @Schema(description = "组件路径", example = "/system/user/index")
    private String component;

    /** 重定向地址 */
    @Schema(description = "重定向地址")
    private String redirect;

    /** 权限标识 */
    @Schema(description = "权限标识", example = "system:user:list")
    private String permission;

    /** 图标 */
    @Schema(description = "图标", example = "lucide:users")
    private String icon;

    /** 排序 */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /** 是否可见 */
    @Schema(description = "是否可见", example = "1")
    private Integer visible;

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 是否外链 */
    @Schema(description = "是否外链", example = "0")
    private Integer isFrame;

    /** 是否缓存 */
    @Schema(description = "是否缓存", example = "1")
    private Integer isCache;

    /** 是否固定标签页 */
    @Schema(description = "是否固定标签页", example = "0")
    private Integer isAffix;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
