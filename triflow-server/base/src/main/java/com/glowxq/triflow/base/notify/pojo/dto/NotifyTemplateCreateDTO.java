package com.glowxq.triflow.base.notify.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.notify.entity.SysNotifyTemplate;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;

/**
 * 消息通知模板创建 DTO
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "消息通知模板创建")
@AutoMapper(target = SysNotifyTemplate.class)
public class NotifyTemplateCreateDTO implements BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 模板标识 */
    @NotBlank(message = "模板标识不能为空")
    @Size(max = 100, message = "模板标识长度不能超过100字符")
    @Schema(description = "模板标识")
    private String templateKey;

    /** 模板名称 */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 100, message = "模板名称长度不能超过100字符")
    @Schema(description = "模板名称")
    private String templateName;

    /** 模板ID */
    @NotBlank(message = "模板ID不能为空")
    @Size(max = 100, message = "模板ID长度不能超过100字符")
    @Schema(description = "模板ID")
    private String templateId;

    /** 通知渠道 */
    @NotBlank(message = "通知渠道不能为空")
    @Size(max = 50, message = "通知渠道长度不能超过50字符")
    @Schema(description = "通知渠道")
    private String channel;

    /** 排序 */
    @Schema(description = "排序")
    private Integer sort;

    /** 状态 */
    @Schema(description = "状态")
    private Integer status;

    /** 备注 */
    @Size(max = 500, message = "备注长度不能超过500字符")
    @Schema(description = "备注")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysNotifyTemplate.class);
    }

}
