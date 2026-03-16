package com.glowxq.triflow.base.notify.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 消息订阅提交 DTO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "消息订阅提交")
public class NotifySubscribeSubmitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 通知渠道 */
    @NotBlank(message = "通知渠道不能为空")
    @Size(max = 50, message = "通知渠道长度不能超过50字符")
    @Schema(description = "通知渠道")
    private String channel;

    /** 订阅项 */
    @NotEmpty(message = "订阅项不能为空")
    @Schema(description = "订阅项")
    private List<NotifySubscribeItemDTO> items;

}
