package com.glowxq.triflow.base.notify.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.notify.entity.SysNotifySubscribe;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息订阅 VO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "消息订阅")
@AutoMapper(target = SysNotifySubscribe.class)
public class NotifySubscribeVO implements BaseVO {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "模板标识")
    private String templateKey;

    @Schema(description = "通知渠道")
    private String channel;

    @Schema(description = "订阅状态")
    private String subscribeStatus;

    @Schema(description = "订阅时间")
    private LocalDateTime subscribeTime;

}
