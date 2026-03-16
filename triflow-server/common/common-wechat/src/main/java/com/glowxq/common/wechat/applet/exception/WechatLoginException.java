package com.glowxq.common.wechat.applet.exception;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.common.BusinessException;

/**
 * 微信登录异常
 * <p>
 * 继承 BusinessException，登录失败时抛出友好的错误信息
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
public class WechatLoginException extends BusinessException {

    public WechatLoginException(String message) {
        super(message);
    }

    public WechatLoginException(String message, Throwable cause) {
        super(ErrorCodeEnum.OPERATION_FAILED, null, message, cause);
    }
}
