package com.glowxq.triflow.base.notify.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息订阅查询 DTO
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息订阅查询")
public class NotifySubscribeQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（模板标识/模板ID） */
    @Schema(description = "搜索关键词", example = "template")
    private String keyword;

    /** 用户ID */
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    /** 通知渠道 */
    @Schema(description = "通知渠道", example = "wechat_miniapp")
    private String channel;

    /** 订阅状态 */
    @Schema(description = "订阅状态", example = "accept")
    private String subscribeStatus;

}
