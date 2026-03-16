package com.glowxq.common.core.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.glowxq.common.core.common.enums.IErrorCode;
import com.glowxq.common.core.common.filter.trace.TraceLogContext;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一 API 响应结果封装类
 * <p>
 * 用于规范化 RESTful API 的响应格式，包含响应码、消息、数据和请求追踪ID。
 * 所有接口的返回值都应该使用此类进行封装，以保证响应格式的一致性。
 * </p>
 *
 * <h3>响应格式：</h3>
 * <pre>{@code
 * // 普通响应
 * {
 *     "code": 0,
 *     "message": "SUCCESS",
 *     "data": {...},
 *     "requestId": "abc123"
 * }
 *
 * // 分页响应（自动解析 PageResult）
 * {
 *     "code": 0,
 *     "message": "SUCCESS",
 *     "data": [...],
 *     "current": 1,
 *     "limit": 10,
 *     "totalPage": 5,
 *     "total": 50,
 *     "requestId": "abc123"
 * }
 * }</pre>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 成功响应（无数据）
 * return ApiResult.success();
 *
 * // 成功响应（携带数据）
 * return ApiResult.success(userVO);
 *
 * // 分页响应（自动解析分页信息）
 * return ApiResult.success(pageResult);
 *
 * // 错误响应
 * return ApiResult.error(ErrorCodeEnum.PARAM_MISSING);
 *
 * // 自定义错误响应
 * return ApiResult.error(100001, "自定义错误消息");
 * }</pre>
 *
 * @param <T> 响应数据的类型
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Schema(description = "统一API响应结果")
public class ApiResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功响应码
     */
    public static final int SUCCESS_CODE = 0;

    /**
     * 成功响应消息
     */
    public static final String SUCCESS_MESSAGE = "SUCCESS";

    /**
     * 响应状态码
     * <p>
     * 0 表示成功，其他编码表示各种错误类型。
     * 错误码格式：TMMCCC (6位数字)
     * - T: 错误类型 (1:业务异常, 2:告警异常, 3:客户端处理异常)
     * - MM: 模块编码 (00-99)
     * - CCC: 错误序号 (000-999)
     * </p>
     */
    @Schema(description = "响应状态码，0表示成功，非0表示错误", example = "0")
    private int code = SUCCESS_CODE;

    /**
     * 响应消息
     * <p>
     * 成功时返回 "SUCCESS"，失败时返回具体的错误描述信息。
     * </p>
     */
    @Schema(description = "响应消息，成功时为SUCCESS，失败时为错误描述", example = "SUCCESS")
    private String message = SUCCESS_MESSAGE;

    /**
     * 响应数据
     * <p>
     * 接口返回的业务数据，类型由泛型参数 T 决定。
     * 当请求失败时，此字段通常为 null。
     * 当返回分页数据时，此字段为数据列表。
     * </p>
     */
    @Schema(description = "响应数据")
    private T data;

    // ==================== 分页信息（仅分页响应时存在） ====================

    /**
     * 当前页码（分页响应时存在）
     */
    @Schema(description = "当前页码（分页响应时存在）", example = "1")
    private Long current;

    /**
     * 每页条数（分页响应时存在）
     */
    @Schema(description = "每页条数（分页响应时存在）", example = "10")
    private Long limit;

    /**
     * 总页数（分页响应时存在）
     */
    @Schema(description = "总页数（分页响应时存在）", example = "5")
    private Long totalPage;

    /**
     * 总条数（分页响应时存在）
     */
    @Schema(description = "总条数（分页响应时存在）", example = "50")
    private Long total;

    /**
     * 唯一请求追踪ID
     * <p>
     * 用于链路追踪，可在日志中关联请求上下文，方便排查问题。
     * </p>
     */
    @Schema(description = "唯一请求追踪ID，用于链路追踪", example = "abc123def456")
    private String requestId = TraceLogContext.getSpanId();

    /**
     * 默认构造函数
     */
    public ApiResult() {
    }

    /**
     * 带参构造函数
     *
     * @param code    响应状态码
     * @param message 响应消息
     */
    public ApiResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 带参构造函数
     *
     * @param code    响应状态码
     * @param message 响应消息
     * @param data    响应数据
     */
    public ApiResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ==================== 成功响应 ====================

    /**
     * 返回成功响应（无数据）
     *
     * @param <T> 响应数据类型
     * @return 成功的 ApiResult 实例
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>();
    }

    /**
     * 返回成功响应（携带数据）
     * <p>
     * 如果传入的是 PageResult，会自动解析分页信息。
     * </p>
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 成功的 ApiResult 实例
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setData(data);
        return apiResult;
    }

    /**
     * 返回成功响应（携带自定义消息和数据）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     响应数据类型
     *
     * @return 成功的 ApiResult 实例
     */
    public static <T> ApiResult<T> success(String message, T data) {
        ApiResult<T> apiResult = success(data);
        apiResult.setMessage(message);
        return apiResult;
    }

    // ==================== 错误响应 ====================

    /**
     * 返回错误响应（使用预定义错误码）
     *
     * @param errorCode 错误码枚举
     * @param <T>       响应数据类型
     * @return 错误的 ApiResult 实例
     */
    public static <T> ApiResult<T> error(IErrorCode errorCode) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(errorCode.getCode());
        apiResult.setMessage(errorCode.getMessage());
        return apiResult;
    }

    /**
     * 返回错误响应（使用预定义错误码 + 自定义消息）
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     * @param <T>       响应数据类型
     * @return 错误的 ApiResult 实例
     */
    public static <T> ApiResult<T> error(IErrorCode errorCode, String message) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(errorCode.getCode());
        apiResult.setMessage(message);
        return apiResult;
    }

    /**
     * 返回错误响应（自定义错误码和消息）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 错误的 ApiResult 实例
     */
    public static <T> ApiResult<T> error(int code, String message) {
        return new ApiResult<>(code, message);
    }

    // ==================== 辅助方法 ====================

    /**
     * 判断响应是否成功
     *
     * @return 如果响应成功返回 true，否则返回 false
     */
    public boolean isSuccess() {
        return SUCCESS_CODE == this.code;
    }

    /**
     * 判断响应是否失败
     *
     * @return 如果响应失败返回 true，否则返回 false
     */
    public boolean isFailed() {
        return !isSuccess();
    }

    /**
     * 判断是否为分页响应
     *
     * @return 如果是分页响应返回 true
     */
    public boolean isPaged() {
        return this.total != null;
    }

    /**
     * 判断错误是否为业务异常（错误码以1开头）
     *
     * @return true 如果是业务异常
     */
    public boolean isBusinessError() {
        return code >= 100000 && code < 200000;
    }

    /**
     * 判断错误是否为告警异常（错误码以2开头）
     *
     * @return true 如果是告警异常
     */
    public boolean isAlertError() {
        return code >= 200000 && code < 300000;
    }

    /**
     * 判断错误是否为客户端处理异常（错误码以3开头）
     *
     * @return true 如果是客户端处理异常
     */
    public boolean isClientError() {
        return code >= 300000 && code < 400000;
    }
}
