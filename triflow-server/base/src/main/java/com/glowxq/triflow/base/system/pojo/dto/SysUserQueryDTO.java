package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.triflow.base.system.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户查询请求 DTO
 * <p>
 * 用于列表查询和分页查询时的参数传递。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "用户查询请求")
public class SysUserQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（用户名/昵称/手机号模糊匹配） */
    @Schema(description = "搜索关键词", example = "admin")
    private String keyword;

    /**
     * 状态筛选
     *
     * @see UserStatusEnum
     */
    @Schema(description = "状态筛选", example = "1")
    private Integer status;

    /** 部门ID筛选 */
    @Schema(description = "部门ID筛选", example = "1")
    private Long deptId;

    /** 数据权限筛选 */
    @Schema(description = "数据权限筛选", example = "UserCreate")
    private String dataScope;

    /** 创建时间-开始 */
    @Schema(description = "创建时间-开始", example = "2025-01-01T00:00:00")
    private LocalDateTime startTime;

    /** 创建时间-结束 */
    @Schema(description = "创建时间-结束", example = "2025-01-31T23:59:59")
    private LocalDateTime endTime;

}
