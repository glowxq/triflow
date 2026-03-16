package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysMenu;
import com.glowxq.triflow.base.system.enums.MenuTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "菜单创建请求")
@AutoMapper(target = SysMenu.class)
public class SysMenuCreateDTO implements BaseDTO {

    /** 父菜单ID */
    @Schema(description = "父菜单ID", example = "0", defaultValue = "0")
    private Long parentId;

    /** 菜单名称 */
    @Schema(description = "菜单名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50字符")
    private String menuName;

    /**
     * 菜单类型
     *
     * @see MenuTypeEnum
     */
    @Schema(description = "菜单类型", example = "C", allowableValues = {"M", "C", "F"})
    private String menuType;

    /** 路由路径 */
    @Schema(description = "路由路径", example = "/system/user")
    @Size(max = 200, message = "路由路径长度不能超过200字符")
    private String path;

    /** 路由名称 */
    @Schema(description = "路由名称", example = "SystemUser")
    @Size(max = 100, message = "路由名称长度不能超过100字符")
    private String name;

    /** 组件路径 */
    @Schema(description = "组件路径", example = "/system/user/index")
    @Size(max = 200, message = "组件路径长度不能超过200字符")
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
    @Schema(description = "排序", example = "1", defaultValue = "0")
    private Integer sort;

    /**
     * 是否可见
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否可见", example = "1", defaultValue = "1", allowableValues = {"0", "1"})
    private Integer visible;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1", defaultValue = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 是否外链
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否外链", example = "0", defaultValue = "0", allowableValues = {"0", "1"})
    private Integer isFrame;

    /**
     * 是否缓存
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否缓存", example = "1", defaultValue = "1", allowableValues = {"0", "1"})
    private Integer isCache;

    /**
     * 是否固定标签页
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否固定标签页", example = "0", defaultValue = "0", allowableValues = {"0", "1"})
    private Integer isAffix;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysMenu.class);
    }

}
