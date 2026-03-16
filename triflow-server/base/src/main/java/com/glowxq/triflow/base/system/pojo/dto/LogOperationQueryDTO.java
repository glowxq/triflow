package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志查询请求 DTO
 * <p>
 * 用于操作日志的分页查询和条件筛选。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "操作日志查询请求")
public class LogOperationQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（操作描述/操作人模糊匹配） */
    @Schema(description = "搜索关键词", example = "创建用户")
    private String keyword;

    /** 操作模块筛选 */
    @Schema(description = "操作模块", example = "system")
    private String module;

    /** 操作类型筛选 */
    @Schema(description = "操作类型", example = "create")
    private String operation;

    /** 操作状态筛选（0:失败, 1:成功） */
    @Schema(description = "操作状态", example = "1")
    private Integer status;

    /** 操作时间-开始 */
    @Schema(description = "操作时间-开始", example = "2026-01-01T00:00:00")
    private LocalDateTime startTime;

    /** 操作时间-结束 */
    @Schema(description = "操作时间-结束", example = "2026-01-31T23:59:59")
    private LocalDateTime endTime;

}
