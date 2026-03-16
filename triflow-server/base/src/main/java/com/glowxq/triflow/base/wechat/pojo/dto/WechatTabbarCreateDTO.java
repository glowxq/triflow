package com.glowxq.triflow.base.wechat.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.wechat.entity.SysWechatTabbar;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;

/**
 * 微信小程序底部菜单创建 DTO
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@Schema(description = "微信小程序底部菜单创建")
@AutoMapper(target = SysWechatTabbar.class)
public class WechatTabbarCreateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50字符")
    @Schema(description = "菜单名称")
    private String text;

    /** 页面路径 */
    @NotBlank(message = "页面路径不能为空")
    @Size(max = 200, message = "页面路径长度不能超过200字符")
    @Schema(description = "页面路径")
    private String pagePath;

    /** 图标类型 */
    @NotBlank(message = "图标类型不能为空")
    @Size(max = 20, message = "图标类型长度不能超过20字符")
    @Schema(description = "图标类型")
    private String iconType;

    /** 图标资源 */
    @NotBlank(message = "图标资源不能为空")
    @Size(max = 255, message = "图标资源长度不能超过255字符")
    @Schema(description = "图标资源")
    private String icon;

    /** 选中图标资源 */
    @Size(max = 255, message = "选中图标资源长度不能超过255字符")
    @Schema(description = "选中图标资源")
    private String iconActive;

    /** 徽标 */
    @Size(max = 20, message = "徽标长度不能超过20字符")
    @Schema(description = "徽标")
    private String badge;

    /** 是否鼓包 */
    @Schema(description = "是否鼓包")
    private Integer isBulge;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sort;

    /** 状态 */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态")
    private Integer status;

    /** 备注 */
    @Size(max = 500, message = "备注长度不能超过500字符")
    @Schema(description = "备注")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysWechatTabbar.class);
    }
}
