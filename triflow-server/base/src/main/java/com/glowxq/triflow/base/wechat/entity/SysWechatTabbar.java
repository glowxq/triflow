package com.glowxq.triflow.base.wechat.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 微信小程序底部菜单实体
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@Table("sys_wechat_tabbar")
@Schema(description = "微信小程序底部菜单")
public class SysWechatTabbar implements BaseEntity {

    /** 主键ID */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String text;

    /** 页面路径 */
    @Schema(description = "页面路径")
    private String pagePath;

    /** 图标类型 */
    @Schema(description = "图标类型")
    private String iconType;

    /** 图标资源 */
    @Schema(description = "图标资源")
    private String icon;

    /** 选中图标资源 */
    @Schema(description = "选中图标资源")
    private String iconActive;

    /** 徽标 */
    @Schema(description = "徽标")
    private String badge;

    /** 是否鼓包 */
    @Schema(description = "是否鼓包")
    private Integer isBulge;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sort;

    /** 状态 */
    @Schema(description = "状态")
    private Integer status;

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

    /** 删除标识 */
    @Schema(description = "删除标识")
    private Integer deleted;
}
