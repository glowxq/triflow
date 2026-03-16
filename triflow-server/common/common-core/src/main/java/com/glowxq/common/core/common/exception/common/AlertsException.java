package com.glowxq.common.core.common.exception.common;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.enums.IErrorCode;

import java.io.Serial;

/**
 * 告警异常
 * <p>
 * 用于需要发送告警通知的严重异常，如系统错误、数据库连接失败等。
 * 记录日志并发送告警通知（飞书/钉钉等）。
 * 错误码以 2 开头 (TYPE_ALERT)。
 * </p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>数据库连接失败</li>
 *   <li>缓存服务异常</li>
 *   <li>外部服务调用失败</li>
 *   <li>未知系统异常</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 使用预定义错误码
 * throw new AlertsException(ErrorCodeEnum.DATABASE_ERROR);
 *
 * // 包装原始异常
 * throw new AlertsException(ErrorCodeEnum.CACHE_ERROR, e);
 *
 * // 使用自定义消息
 * throw new AlertsException("Redis连接池耗尽");
 * }</pre>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
public class AlertsException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用默认系统异常错误码
     *
     * @param message 错误消息
     */
    public AlertsException(String message) {
        super(ErrorCodeEnum.SYSTEM_BUSY, message);
    }

    /**
     * 使用指定错误码
     *
     * @param errorCode 错误码枚举
     */
    public AlertsException(IErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    /**
     * 使用指定错误码和自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public AlertsException(IErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用默认错误码包装原始异常
     *
     * @param cause 原始异常
     */
    public AlertsException(Throwable cause) {
        super(ErrorCodeEnum.UNKNOWN, cause.getMessage(), cause);
    }

    /**
     * 使用指定错误码包装原始异常
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public AlertsException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause.getMessage(), cause);
    }

    /**
     * 使用自定义消息包装原始异常
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public AlertsException(String message, Throwable cause) {
        super(ErrorCodeEnum.UNKNOWN, message, cause);
    }

    /**
     * 使用指定错误码、参数和消息
     *
     * @param errorCode 错误码枚举
     * @param args      参数数组
     * @param message   错误消息
     */
    public AlertsException(IErrorCode errorCode, Object[] args, String message) {
        super(errorCode, args, message);
    }

    /**
     * 使用指定错误码、参数、消息和原因
     *
     * @param errorCode 错误码枚举
     * @param args      参数数组
     * @param message   错误消息
     * @param cause     原始异常
     */
    public AlertsException(IErrorCode errorCode, Object[] args, String message, Throwable cause) {
        super(errorCode, args, message, cause);
    }
}
