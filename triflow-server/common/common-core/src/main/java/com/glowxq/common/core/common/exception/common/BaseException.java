package com.glowxq.common.core.common.exception.common;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.enums.IErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 基础异常类
 * <p>
 * 所有业务异常的基类，提供统一的错误码和错误消息处理。
 * </p>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
@Getter
public class BaseException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final IErrorCode errorCode;

    /**
     * 额外参数（用于消息格式化等）
     */
    private final Object[] args;

    public BaseException(IErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BaseException(IErrorCode errorCode, Object[] args, String message) {
        super(message);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(IErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public BaseException(IErrorCode errorCode, Object[] args, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.args = args;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCodeEnum.UNKNOWN;
        this.args = null;
    }

    /**
     * 获取错误码数值
     *
     * @return 错误码
     */
    public int getCode() {
        return errorCode.getCode();
    }
}
