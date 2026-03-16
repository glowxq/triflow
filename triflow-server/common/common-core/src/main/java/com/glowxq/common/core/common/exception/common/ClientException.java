package com.glowxq.common.core.common.exception.common;

import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.enums.IErrorCode;

import java.io.Serial;

/**
 * 客户端处理异常
 * <p>
 * 用于需要前端/客户端进行特殊处理的异常，如重新登录、刷新页面等。
 * 前端可根据错误码（以 3 开头）执行对应的处理逻辑。
 * 错误码以 3 开头 (TYPE_CLIENT)。
 * </p>
 *
 * <h3>使用场景：</h3>
 * <ul>
 *   <li>Token 过期需要重新登录</li>
 *   <li>权限变更需要刷新页面</li>
 *   <li>账号被禁用/锁定</li>
 *   <li>被踢下线</li>
 *   <li>需要用户确认的操作</li>
 * </ul>
 *
 * <h3>前端处理建议：</h3>
 * <pre>{@code
 * // 前端根据错误码类型判断
 * if (code >= 300000 && code < 400000) {
 *     // 这是客户端处理异常，需要特殊处理
 *     switch (code) {
 *         case 301001: // NOT_LOGIN
 *         case 301002: // INVALID_TOKEN
 *         case 301003: // TOKEN_EXPIRED
 *             router.push('/login');
 *             break;
 *         case 301004: // PERMISSION_DENIED
 *             showPermissionDeniedModal();
 *             break;
 *         // ... 其他处理
 *     }
 * }
 * }</pre>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // Token过期
 * throw new ClientException(ErrorCodeEnum.TOKEN_EXPIRED);
 *
 * // 权限不足
 * throw new ClientException(ErrorCodeEnum.PERMISSION_DENIED, "您没有删除用户的权限");
 *
 * // 账号被禁用
 * throw new ClientException(ErrorCodeEnum.ACCOUNT_DISABLED);
 * }</pre>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
public class ClientException extends BaseException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 使用默认客户端错误码
     *
     * @param message 错误消息
     */
    public ClientException(String message) {
        super(ErrorCodeEnum.NOT_LOGIN, message);
    }

    /**
     * 使用指定错误码
     *
     * @param errorCode 错误码枚举
     */
    public ClientException(IErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    /**
     * 使用指定错误码和自定义消息
     *
     * @param errorCode 错误码枚举
     * @param message   自定义错误消息
     */
    public ClientException(IErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    /**
     * 使用指定错误码、参数和消息
     *
     * @param errorCode 错误码枚举
     * @param args      参数数组
     * @param message   错误消息
     */
    public ClientException(IErrorCode errorCode, Object[] args, String message) {
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
    public ClientException(IErrorCode errorCode, Object[] args, String message, Throwable cause) {
        super(errorCode, args, message, cause);
    }
}
