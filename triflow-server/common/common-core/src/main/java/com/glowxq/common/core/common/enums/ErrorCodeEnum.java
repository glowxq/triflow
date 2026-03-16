package com.glowxq.common.core.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.glowxq.common.core.common.enums.ErrorModule.*;
import static com.glowxq.common.core.common.enums.IErrorCode.buildCode;

/**
 * 系统核心错误码枚举
 * <p>
 * 定义系统核心模块的错误码，采用模块化设计。
 * 错误码格式：TMMCCC (6位数字)
 * </p>
 *
 * <h3>错误码构成：</h3>
 * <ul>
 *   <li>T: 错误类型 - 1:业务异常, 2:告警异常, 3:客户端异常</li>
 *   <li>MM: 模块编码 - 参见 {@link ErrorModule}</li>
 *   <li>CCC: 错误序号 - 000-999</li>
 * </ul>
 *
 * <h3>使用示例：</h3>
 * <pre>{@code
 * // 抛出业务异常
 * throw new BusinessException(ErrorCodeEnum.PARAM_MISSING);
 *
 * // 抛出客户端处理异常（前端特殊处理）
 * throw new ClientException(ErrorCodeEnum.TOKEN_EXPIRED);
 *
 * // 抛出告警异常（发送告警通知）
 * throw new AlertsException(ErrorCodeEnum.DATABASE_ERROR);
 * }</pre>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
@AllArgsConstructor
@Getter
public enum ErrorCodeEnum implements IErrorCode {

    // ==================== 成功 ====================

    /**
     * 操作成功
     */
    SUCCESS(0, "操作成功"),

    // ==================== 通用系统模块 (模块00) ====================

    /**
     * 未知异常
     */
    UNKNOWN(buildCode(TYPE_ALERT, SYSTEM, 999), "未知异常"),

    /**
     * 系统繁忙
     */
    SYSTEM_BUSY(buildCode(TYPE_ALERT, SYSTEM, 1), "系统繁忙，请稍后重试"),

    /**
     * 操作失败
     */
    OPERATION_FAILED(buildCode(TYPE_BUSINESS, SYSTEM, 2), "操作失败"),

    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(buildCode(TYPE_BUSINESS, SYSTEM, 3), "数据不存在"),

    /**
     * 数据已存在
     */
    DATA_ALREADY_EXISTS(buildCode(TYPE_BUSINESS, SYSTEM, 4), "数据已存在"),

    /**
     * 非法操作
     */
    ILLEGAL_OPERATION(buildCode(TYPE_BUSINESS, SYSTEM, 5), "非法操作"),

    // ==================== 认证授权模块 (模块01) ====================

    /**
     * 未登录
     */
    NOT_LOGIN(buildCode(TYPE_CLIENT, AUTH, 1), "请先登录"),

    /**
     * Token无效
     */
    INVALID_TOKEN(buildCode(TYPE_CLIENT, AUTH, 2), "Token无效，请重新登录"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(buildCode(TYPE_CLIENT, AUTH, 3), "Token已过期，请重新登录"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED(buildCode(TYPE_CLIENT, AUTH, 4), "权限不足"),

    /**
     * 角色权限不足
     */
    ROLE_DENIED(buildCode(TYPE_CLIENT, AUTH, 5), "角色权限不足"),

    /**
     * 账号被禁用
     */
    ACCOUNT_DISABLED(buildCode(TYPE_CLIENT, AUTH, 6), "账号已被禁用"),

    /**
     * 账号被锁定
     */
    ACCOUNT_LOCKED(buildCode(TYPE_CLIENT, AUTH, 7), "账号已被锁定"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(buildCode(TYPE_BUSINESS, AUTH, 8), "密码错误"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(buildCode(TYPE_BUSINESS, AUTH, 9), "验证码错误"),

    /**
     * 验证码已过期
     */
    CAPTCHA_EXPIRED(buildCode(TYPE_BUSINESS, AUTH, 10), "验证码已过期"),

    /**
     * 客户端无效
     */
    CLIENT_INVALID(buildCode(TYPE_CLIENT, AUTH, 11), "客户端无效"),

    /**
     * 客户端已禁用
     */
    CLIENT_BLOCKED(buildCode(TYPE_CLIENT, AUTH, 12), "客户端已禁用"),

    /**
     * 被踢下线
     */
    KICKED_OUT(buildCode(TYPE_CLIENT, AUTH, 13), "您已在其他设备登录"),

    // ==================== 参数校验模块 (模块02) ====================

    /**
     * 参数缺失
     */
    PARAM_MISSING(buildCode(TYPE_BUSINESS, VALIDATION, 1), "参数缺失"),

    /**
     * 参数格式错误
     */
    PARAM_FORMAT_ERROR(buildCode(TYPE_BUSINESS, VALIDATION, 2), "参数格式错误"),

    /**
     * 参数值无效
     */
    PARAM_INVALID(buildCode(TYPE_BUSINESS, VALIDATION, 3), "参数值无效"),

    /**
     * 参数验证失败
     */
    VALIDATION_ERROR(buildCode(TYPE_BUSINESS, VALIDATION, 4), "参数验证失败"),

    /**
     * JSON解析错误
     */
    JSON_PARSE_ERROR(buildCode(TYPE_BUSINESS, VALIDATION, 5), "JSON解析错误"),

    // ==================== 数据库模块 (模块03) ====================

    /**
     * 数据库异常
     */
    DATABASE_ERROR(buildCode(TYPE_ALERT, DATABASE, 1), "数据库异常"),

    /**
     * 数据库连接失败
     */
    DATABASE_CONNECTION_ERROR(buildCode(TYPE_ALERT, DATABASE, 2), "数据库连接失败"),

    /**
     * 数据库操作超时
     */
    DATABASE_TIMEOUT(buildCode(TYPE_ALERT, DATABASE, 3), "数据库操作超时"),

    /**
     * 数据完整性约束错误
     */
    DATA_INTEGRITY_ERROR(buildCode(TYPE_BUSINESS, DATABASE, 4), "数据完整性约束错误"),

    // ==================== 缓存模块 (模块04) ====================

    /**
     * 缓存异常
     */
    CACHE_ERROR(buildCode(TYPE_ALERT, CACHE, 1), "缓存异常"),

    /**
     * 缓存连接失败
     */
    CACHE_CONNECTION_ERROR(buildCode(TYPE_ALERT, CACHE, 2), "缓存连接失败"),

    // ==================== 文件存储模块 (模块05) ====================

    /**
     * 文件上传失败
     */
    FILE_UPLOAD_ERROR(buildCode(TYPE_BUSINESS, OSS, 1), "文件上传失败"),

    /**
     * 文件下载失败
     */
    FILE_DOWNLOAD_ERROR(buildCode(TYPE_BUSINESS, OSS, 2), "文件下载失败"),

    /**
     * 文件不存在
     */
    FILE_NOT_FOUND(buildCode(TYPE_BUSINESS, OSS, 3), "文件不存在"),

    /**
     * 文件格式不支持
     */
    FILE_FORMAT_ERROR(buildCode(TYPE_BUSINESS, OSS, 4), "文件格式不支持"),

    /**
     * 文件大小超限
     */
    FILE_SIZE_EXCEEDED(buildCode(TYPE_BUSINESS, OSS, 5), "文件大小超过限制"),

    // ==================== Excel模块 (模块07) ====================

    /**
     * Excel导入错误
     */
    EXCEL_IMPORT_ERROR(buildCode(TYPE_BUSINESS, EXCEL, 1), "Excel导入错误"),

    /**
     * Excel导出错误
     */
    EXCEL_EXPORT_ERROR(buildCode(TYPE_BUSINESS, EXCEL, 2), "Excel导出错误"),

    /**
     * Excel模板错误
     */
    EXCEL_TEMPLATE_ERROR(buildCode(TYPE_BUSINESS, EXCEL, 3), "Excel模板错误"),

    // ==================== 短信模块 (模块08) ====================

    /**
     * 短信发送失败
     */
    SMS_SEND_ERROR(buildCode(TYPE_BUSINESS, SMS, 1), "短信发送失败"),

    /**
     * 短信发送频率过高
     */
    SMS_RATE_LIMIT(buildCode(TYPE_BUSINESS, SMS, 2), "短信发送频率过高，请稍后再试"),

    // ==================== 微信模块 (模块09) ====================

    /**
     * 微信接口调用失败
     */
    WECHAT_API_ERROR(buildCode(TYPE_BUSINESS, WECHAT, 1), "微信接口调用失败"),

    /**
     * 微信登录失败
     */
    WECHAT_LOGIN_ERROR(buildCode(TYPE_BUSINESS, WECHAT, 2), "微信登录失败"),

    /**
     * 微信支付失败
     */
    WECHAT_PAY_ERROR(buildCode(TYPE_BUSINESS, WECHAT, 3), "微信支付失败"),

    // ==================== AI 服务模块 (模块13) ====================

    /**
     * AI 提供商未找到
     */
    AI_PROVIDER_NOT_FOUND(buildCode(TYPE_BUSINESS, AI, 1), "AI 提供商未找到"),

    /**
     * AI 提供商不可用
     */
    AI_PROVIDER_UNAVAILABLE(buildCode(TYPE_BUSINESS, AI, 2), "AI 提供商不可用"),

    /**
     * AI 调用失败
     */
    AI_CALL_ERROR(buildCode(TYPE_ALERT, AI, 3), "AI 调用失败"),

    /**
     * AI 流式调用失败
     */
    AI_STREAM_ERROR(buildCode(TYPE_ALERT, AI, 4), "AI 流式调用失败"),

    /**
     * AI 结构化输出失败
     */
    AI_STRUCTURED_OUTPUT_ERROR(buildCode(TYPE_ALERT, AI, 5), "AI 结构化输出失败"),

    // ==================== 外部服务模块 (模块10) ====================

    /**
     * 外部服务调用失败
     */
    EXTERNAL_SERVICE_ERROR(buildCode(TYPE_ALERT, EXTERNAL, 1), "外部服务调用失败"),

    /**
     * 外部服务超时
     */
    EXTERNAL_SERVICE_TIMEOUT(buildCode(TYPE_ALERT, EXTERNAL, 2), "外部服务调用超时"),

    ;

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 根据错误码解析对应的枚举值
     *
     * @param code 错误码
     * @return 对应的枚举值，如果未找到则返回 UNKNOWN
     */
    public static ErrorCodeEnum parse(int code) {
        for (ErrorCodeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return UNKNOWN;
    }

    /**
     * 判断错误码是否表示成功
     *
     * @param code 错误码
     *
     * @return true 如果成功
     */
    public static boolean isSuccess(int code) {
        return code == SUCCESS.code;
    }
}
