package com.glowxq.triflow.base.wechat.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 微信小程序底部菜单查询 DTO
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@Schema(description = "微信小程序底部菜单查询")
public class WechatTabbarQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（菜单名称/页面路径） */
    @Schema(description = "搜索关键词", example = "首页")
    private String keyword;

    /** 状态筛选 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 页码 */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页数量 */
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
}
