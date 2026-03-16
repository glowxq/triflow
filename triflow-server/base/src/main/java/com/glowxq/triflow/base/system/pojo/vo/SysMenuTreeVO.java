package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.triflow.base.system.entity.SysMenu;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单树形响应 VO
 * <p>
 * 用于前端菜单渲染，对应前端 MenuRecordRaw 结构。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "菜单树形响应")
@AutoMapper(target = SysMenu.class)
public class SysMenuTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @Schema(description = "菜单ID")
    private Long id;

    /** 父菜单ID */
    @Schema(description = "父菜单ID")
    private Long parentId;

    /** 菜单名称（路由name） */
    @Schema(description = "菜单名称")
    private String name;

    /** 路由路径 */
    @Schema(description = "路由路径")
    private String path;

    /** 组件路径 */
    @Schema(description = "组件路径")
    private String component;

    /** 重定向地址 */
    @Schema(description = "重定向地址")
    private String redirect;

    /** 菜单类型 (M:目录, C:菜单, F:按钮) */
    @Schema(description = "菜单类型")
    private String menuType;

    /** meta 信息 */
    @Schema(description = "路由元信息")
    private MenuMeta meta;

    /** 子菜单 */
    @Schema(description = "子菜单")
    private List<SysMenuTreeVO> children;

    /**
     * 菜单元信息（对应前端 RouteMeta）
     */
    @Data
    @Schema(description = "路由元信息")
    public static class MenuMeta implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 菜单标题 */
        @Schema(description = "菜单标题")
        private String title;

        /** 菜单图标 */
        @Schema(description = "菜单图标")
        private String icon;

        /** 权限标识 */
        @Schema(description = "权限标识")
        private String authority;

        /** 排序 */
        @Schema(description = "排序")
        private Integer order;

        /** 是否固定标签页 */
        @Schema(description = "是否固定标签页")
        private Boolean affixTab;

        /** 是否缓存 */
        @Schema(description = "是否缓存")
        private Boolean keepAlive;

        /** 是否隐藏 */
        @Schema(description = "是否在菜单中隐藏")
        private Boolean hideInMenu;

    }

}
