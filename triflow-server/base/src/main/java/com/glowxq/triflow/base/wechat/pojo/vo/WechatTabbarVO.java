package com.glowxq.triflow.base.wechat.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.wechat.entity.SysWechatTabbar;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 微信小程序底部菜单 VO
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@Schema(description = "微信小程序底部菜单")
@AutoMapper(target = SysWechatTabbar.class)
public class WechatTabbarVO implements BaseVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "菜单名称")
    private String text;

    @Schema(description = "页面路径")
    private String pagePath;

    @Schema(description = "图标类型")
    private String iconType;

    @Schema(description = "图标资源")
    private String icon;

    @Schema(description = "选中图标资源")
    private String iconActive;

    @Schema(description = "徽标")
    private String badge;

    @Schema(description = "是否鼓包")
    private Integer isBulge;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
