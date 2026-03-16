package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.LogOperation;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志响应 VO
 * <p>
 * 用于操作日志列表展示和详情查看。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Data
@Schema(description = "操作日志响应")
@AutoMapper(target = LogOperation.class)
public class LogOperationVO implements BaseVO {

    /** 日志ID */
    @Schema(description = "日志ID", example = "1")
    private Long id;

    /** 操作模块 */
    @Schema(description = "操作模块", example = "system")
    private String module;

    /** 操作类型 */
    @Schema(description = "操作类型", example = "create")
    private String operation;

    /** 操作描述 */
    @Schema(description = "操作描述", example = "创建用户")
    private String description;

    /** 请求方法 */
    @Schema(description = "请求方法", example = "POST")
    private String method;

    /** 请求URL */
    @Schema(description = "请求URL", example = "/base/system/user")
    private String requestUrl;

    /** 请求参数 */
    @Schema(description = "请求参数")
    private String requestParams;

    /** 响应数据 */
    @Schema(description = "响应数据")
    private String responseData;

    /** 操作IP地址 */
    @Schema(description = "操作IP地址", example = "127.0.0.1")
    private String ip;

    /** 用户代理 */
    @Schema(description = "用户代理")
    private String userAgent;

    /** 操作人ID */
    @Schema(description = "操作人ID", example = "1")
    private Long operatorId;

    /** 操作人姓名 */
    @Schema(description = "操作人姓名", example = "admin")
    private String operatorName;

    /** 操作时间 */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;

    /** 执行耗时（毫秒） */
    @Schema(description = "执行耗时（毫秒）", example = "120")
    private Long duration;

    /** 操作状态（0:失败, 1:成功） */
    @Schema(description = "操作状态", example = "1")
    private Integer status;

    /** 错误信息 */
    @Schema(description = "错误信息")
    private String errorMsg;

}
