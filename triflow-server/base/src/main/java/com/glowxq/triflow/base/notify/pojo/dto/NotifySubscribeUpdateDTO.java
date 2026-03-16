package com.glowxq.triflow.base.notify.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.notify.entity.SysNotifySubscribe;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;

/**
 * 消息订阅更新 DTO
 *
 * @author glowxq
 * @since 2025-02-01
 */
@Data
@Schema(description = "消息订阅更新")
@AutoMapper(target = SysNotifySubscribe.class)
public class NotifySubscribeUpdateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @NotNull(message = "订阅ID不能为空")
    @Schema(description = "订阅ID")
    private Long id;

    /** 订阅状态 */
    @NotBlank(message = "订阅状态不能为空")
    @Size(max = 20, message = "订阅状态长度不能超过20字符")
    @Schema(description = "订阅状态")
    private String subscribeStatus;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysNotifySubscribe.class);
    }
}
