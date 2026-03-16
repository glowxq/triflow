package com.glowxq.triflow.base.notify.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 消息通知模板查询 DTO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "消息通知模板查询")
public class NotifyTemplateQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词 */
    @Schema(description = "搜索关键词")
    private String keyword;

    /** 通知渠道 */
    @Schema(description = "通知渠道")
    private String channel;

    /** 状态 */
    @Schema(description = "状态")
    private Integer status;

}
