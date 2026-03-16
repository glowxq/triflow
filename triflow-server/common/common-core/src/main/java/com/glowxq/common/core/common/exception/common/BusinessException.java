package com.glowxq.common.core.common.exception.common;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.enums.IErrorCode;

import java.io.Serial;

/**
 * 业务异常
 * <p>
 * 用于业务逻辑中的常规异常情况，仅记录日志，不发送告警通知。
 * 错误码以 1 开头 (TYPE_BUSINESS)。
 * </p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>参数校验失败</li>
 *   <li>业务规则校验失败</li>
 *   <li>数据不存在</li>
 *   <li>数据重复</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 使用预定义错误码
 * throw new BusinessException(ErrorCodeEnum.DATA_NOT_FOUND);
 *
 * // 使用自定义消息
 * throw new BusinessException(ErrorCodeEnum.PARAM_INVALID, "用户名不能为空");
 *
 * // 简单消息（使用默认业务错误码）
 * throw new BusinessException("订单已关闭，无法支付");
 * }</pre>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
public class BusinessException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用默认业务错误码
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(ErrorCodeEnum.OPERATION_FAILED, message);
    }

    /**
     * 使用指定错误码
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(IErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    /**
     * 使用指定错误码和自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public BusinessException(IErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用指定错误码、参数和消息
     *
     * @param errorCode 错误码枚举
     * @param args      参数数组
     * @param message   错误消息
     */
    public BusinessException(IErrorCode errorCode, Object[] args, String message) {
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
    public BusinessException(IErrorCode errorCode, Object[] args, String message, Throwable cause) {
        super(errorCode, args, message, cause);
    }
}
