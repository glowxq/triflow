package com.glowxq.common.core.common.exception;

import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ErrorCodeEnum;
import com.glowxq.common.core.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author glowxq
 * @version 1.0
 * @date 2024/9/30
 */
@Slf4j
public abstract class BaseExceptionHandler implements ExceptionHandle {

    /**
     * 打包错误日志
     *
     * @param request  请求
     * @param response 响应
     * @param e        异常
     * @return 格式化的错误消息
     */
    protected String logAndPackError(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("Request >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        String ip = ServletUtils.getClientIP(request);
        log.error("Request-IP:{}", ip);
        String uri = request.getRequestURI();
        log.error("Request-URL:{}", uri);
        String method = request.getMethod();
        log.error("Request-Method:{}", method);
        Map<String, String> param = ServletUtils.getParamMap(request);
        log.error("Request-Param:{}", param);
        Map<String, String> headers = ServletUtils.getHeaders(request);
        log.error("Request-Header:{}", headers);
        log.error("Request <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        log.error("Exception >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        log.error("Exception ", e);
        log.error("Exception <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        return requestMessage(e, uri, ip, method, headers, param);
    }

    /**
     * 获取异常的堆栈跟踪信息字符串
     * 此方法用于将异常的堆栈跟踪信息转换为字符串形式，便于日志记录或错误报告
     * 它通过使用PrintWriter和StringWriter来捕获异常的堆栈信息，避免直接使用e.printStackTrace()方法仅在标准错误输出中打印信息的限制
     *
     * @param e 需要获取堆栈跟踪信息的异常对象
     * @return 异常的堆栈跟踪信息字符串
     */
    protected String getStackTraceMessage(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    protected ApiResult<Object> wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();
        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(error.getCode());
            msg.append(":");
            msg.append(error.getObjectName());
            msg.append(":");
            msg.append(", ");
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());
        }
        return new ApiResult<>(ErrorCodeEnum.VALIDATION_ERROR.getCode(), msg.substring(2));
    }

    /**
     * 构建请求消息
     *
     * @param e       异常
     * @param uri     请求 URI
     * @param ip      客户端 IP
     * @param method  请求方法
     * @param headers 请求头
     * @param param   请求参数
     * @return 格式化的请求消息
     */
    private String requestMessage(Exception e, String uri, String ip, String method, Map<String, String> headers, Map<String, String> param) {
        StringBuilder sb = new StringBuilder();
        sb.append("Request: ").append(uri).append("\n\n");
        sb.append("Ip: ").append(ip).append("\n\n");
        sb.append("Method: ").append(method).append("\n\n");
        sb.append("Header: ").append(headers).append("\n\n");
        sb.append("Req-Param: ").append(param).append("\n\n");
        sb.append("Exception: ").append(e.getMessage()).append("\n\n");
        sb.append("StackTraceMessage: ").append(getStackTraceMessage(e));
        return sb.toString();
    }
}
