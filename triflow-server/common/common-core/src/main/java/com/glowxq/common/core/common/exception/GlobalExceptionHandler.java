package com.glowxq.common.core.common.exception;

import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.common.exception.common.AlertsException;
import com.glowxq.common.core.common.exception.common.BaseException;
import com.glowxq.common.core.common.exception.common.BusinessException;
import com.glowxq.common.core.common.exception.common.ClientException;
import com.glowxq.common.core.common.feishu.utils.FeishuMessageUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获处理
 * <p>
 * 异常处理策略：
 * <ul>
 *   <li>BusinessException：常规业务异常，仅记录日志，返回业务错误码（1开头）</li>
 *   <li>AlertsException：需要告警的异常，记录日志并发送飞书告警，返回告警错误码（2开头）</li>
 *   <li>ClientException：需要前端处理的异常，返回客户端错误码（3开头）供前端处理</li>
 *   <li>其他异常：发送告警并返回未知错误</li>
 * </ul>
 * </p>
 *
 * <h3>错误码格式：TMMCCC (6位数字)</h3>
 * <ul>
 *   <li>T: 错误类型 - 1:业务异常, 2:告警异常, 3:客户端异常</li>
 *   <li>MM: 模块编码 - 00-99</li>
 *   <li>CCC: 错误序号 - 000-999</li>
 * </ul>
 *
 * @author glowxq
 * @version 2.0
 * @since 2025/1/21
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends BaseExceptionHandler {

    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.logAndPackError(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(ErrorCodeEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResult<Object>> handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.logAndPackError(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        ApiResult<Object> error = ApiResult.error(ErrorCodeEnum.UNKNOWN);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理业务异常
     * <p>仅记录日志，不发送告警。错误码以 1 开头。</p>
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request, HttpServletResponse response) {
        super.logAndPackError(request, response, e);
        return new ApiResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 处理告警异常
     * <p>记录日志并发送告警通知。错误码以 2 开头。</p>
     */
    @ExceptionHandler(value = AlertsException.class)
    @ResponseBody
    public ApiResult<Void> handleAlertsException(AlertsException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.logAndPackError(request, response, e);
        FeishuMessageUtils.sendInternalMessage(message);
        return new ApiResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 处理客户端异常
     * <p>返回特定错误码，前端根据错误码执行对应处理逻辑。错误码以 3 开头。</p>
     */
    @ExceptionHandler(value = ClientException.class)
    @ResponseBody
    public ApiResult<Void> handleClientException(ClientException e, HttpServletRequest request, HttpServletResponse response) {
        super.logAndPackError(request, response, e);
        return new ApiResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 处理基础异常
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ApiResult<Void> handleBaseException(BaseException e, HttpServletRequest request, HttpServletResponse response) {
        String message = super.logAndPackError(request, response, e);
        // 根据错误类型决定是否发送告警
        if (e.getErrorCode().isAlertError()) {
            FeishuMessageUtils.sendInternalMessage(message);
        }
        return new ApiResult<>(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Object>> handleValidException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        super.logAndPackError(request, response, e);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<ApiResult<Object>> handleBindException(BindException e, HttpServletRequest request, HttpServletResponse response) {
        super.logAndPackError(request, response, e);
        ApiResult<Object> apiResult = wrapperBindingResult(e);
        return new ResponseEntity<>(apiResult, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
