package com.glowxq.triflow.base.notify.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息通知模板 VO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "消息通知模板")
@AutoMapper(target = SysNotifyTemplate.class)
public class NotifyTemplateVO implements BaseVO {

    @Schema(description = "模板ID")
    private Long id;

    @Schema(description = "模板标识")
    private String templateKey;

    @Schema(description = "模板名称")
    private String templateName;

    @Schema(description = "模板ID")
    private String templateId;

    @Schema(description = "通知渠道")
    private String channel;

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
