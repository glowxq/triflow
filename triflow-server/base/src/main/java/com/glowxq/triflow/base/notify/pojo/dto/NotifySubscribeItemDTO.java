package com.glowxq.triflow.base.notify.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息订阅项 DTO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "消息订阅项")
public class NotifySubscribeItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 模板ID */
    @NotBlank(message = "模板ID不能为空")
    @Size(max = 100, message = "模板ID长度不能超过100字符")
    @Schema(description = "模板ID")
    private String templateId;

    /** 订阅状态 */
    @NotBlank(message = "订阅状态不能为空")
    @Size(max = 20, message = "订阅状态长度不能超过20字符")
    @Schema(description = "订阅状态")
    private String subscribeStatus;

}
