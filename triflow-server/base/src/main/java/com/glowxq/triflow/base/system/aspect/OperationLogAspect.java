package com.glowxq.triflow.base.system.aspect;

import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.BusinessLogEnum;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.util.JsonUtils;
import com.glowxq.common.core.util.ServletUtils;
import com.glowxq.common.security.core.util.LoginUtils;
import com.glowxq.triflow.base.system.entity.LogOperation;
import com.glowxq.triflow.base.system.service.LogOperationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * 操作日志 AOP 切面
 * <p>
 * 拦截标注了 {@link OperationLog} 的方法或类，自动记录操作日志到 log_operation 表。
 * 支持类级别和方法级别注解，方法级别优先。
 * </p>
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final LogOperationService logOperationService;

    /** 请求参数最大长度 */
    private static final int MAX_PARAMS_LENGTH = 2000;
    /** 响应数据最大长度 */
    private static final int MAX_RESPONSE_LENGTH = 2000;

    @Pointcut("@annotation(com.glowxq.common.core.common.annotation.OperationLog) "
            + "|| @within(com.glowxq.common.core.common.annotation.OperationLog)")
    public void operationLogPointcut() {
        // 切点：标注了 @OperationLog 的方法或类
    }

    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable error = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            error = throwable;
            throw throwable;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            try {
                recordLog(joinPoint, result, error, duration);
            } catch (Exception e) {
                log.error("记录操作日志异常: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 记录操作日志
     */
    private void recordLog(ProceedingJoinPoint joinPoint, Object result, Throwable error, long duration) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取注解（方法级别优先，其次类级别）
        OperationLog methodAnnotation = method.getAnnotation(OperationLog.class);
        OperationLog classAnnotation = joinPoint.getTarget().getClass().getAnnotation(OperationLog.class);
        OperationLog annotation = methodAnnotation != null ? methodAnnotation : classAnnotation;

        if (annotation == null) {
            return;
        }

        // 解析模块
        ModuleEnum moduleEnum = methodAnnotation != null ? methodAnnotation.module()
                : classAnnotation != null ? classAnnotation.module() : ModuleEnum.System;

        // 解析操作类型
        BusinessLogEnum handleType = resolveHandleType(methodAnnotation, classAnnotation);

        // 解析描述
        String desc = resolveDescription(methodAnnotation, classAnnotation, method);

        // 构建日志实体
        LogOperation logEntity = new LogOperation();
        logEntity.setModule(moduleEnum.getCode());
        logEntity.setOperation(handleType.getCode());
        logEntity.setDescription(desc);
        logEntity.setDuration(duration);
        logEntity.setOperateTime(LocalDateTime.now());
        logEntity.setStatus(error == null ? 1 : 0);

        if (error != null) {
            logEntity.setErrorMsg(truncate(error.getMessage(), MAX_PARAMS_LENGTH));
        }

        // 填充请求信息
        fillRequestInfo(logEntity, joinPoint, annotation);

        // 填充响应数据
        if (annotation.isSaveResponse() && result != null && error == null) {
            logEntity.setResponseData(truncate(JsonUtils.toJsonString(result), MAX_RESPONSE_LENGTH));
        }

        // 填充操作人信息
        fillOperatorInfo(logEntity);

        // 异步保存
        logOperationService.asyncSave(logEntity);
    }

    /**
     * 解析操作类型（方法级别 > HTTP 方法自动推断 > 类级别 > 默认）
     */
    private BusinessLogEnum resolveHandleType(OperationLog methodAnnotation, OperationLog classAnnotation) {
        // 方法级别明确指定
        if (methodAnnotation != null && methodAnnotation.handleType() != BusinessLogEnum.NONE) {
            return methodAnnotation.handleType();
        }

        // 根据 HTTP 方法自动推断
        BusinessLogEnum inferred = inferFromHttpMethod();
        if (inferred != null) {
            return inferred;
        }

        // 类级别指定
        if (classAnnotation != null && classAnnotation.handleType() != BusinessLogEnum.NONE) {
            return classAnnotation.handleType();
        }

        return BusinessLogEnum.Other;
    }

    /**
     * 从 HTTP 方法推断操作类型
     */
    private BusinessLogEnum inferFromHttpMethod() {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }
            return switch (request.getMethod().toUpperCase()) {
                case "POST" -> BusinessLogEnum.Create;
                case "PUT", "PATCH" -> BusinessLogEnum.Update;
                case "DELETE" -> BusinessLogEnum.Delete;
                case "GET" -> BusinessLogEnum.Query;
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析描述文本
     */
    private String resolveDescription(OperationLog methodAnnotation, OperationLog classAnnotation, Method method) {
        if (methodAnnotation != null && StringUtils.isNotBlank(methodAnnotation.desc())) {
            return methodAnnotation.desc();
        }
        if (classAnnotation != null && StringUtils.isNotBlank(classAnnotation.desc())) {
            return classAnnotation.desc() + " - " + method.getName();
        }
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    /**
     * 填充请求信息
     */
    private void fillRequestInfo(LogOperation logEntity, ProceedingJoinPoint joinPoint, OperationLog annotation) {
        try {
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return;
            }

            logEntity.setMethod(request.getMethod());
            logEntity.setRequestUrl(request.getRequestURI());
            logEntity.setIp(ServletUtils.getClientIP(request));
            logEntity.setUserAgent(truncate(request.getHeader("User-Agent"), 500));

            // 保存请求参数
            if (annotation.isSaveRequest()) {
                String params = buildRequestParams(joinPoint, annotation.excludeParamNames());
                logEntity.setRequestParams(truncate(params, MAX_PARAMS_LENGTH));
            }
        } catch (Exception e) {
            log.debug("填充请求信息异常: {}", e.getMessage());
        }
    }

    /**
     * 构建请求参数字符串
     */
    private String buildRequestParams(ProceedingJoinPoint joinPoint, String[] excludeParamNames) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }

        Object[] filtered = Arrays.stream(args)
                .filter(arg -> !(arg instanceof HttpServletRequest
                        || arg instanceof HttpServletResponse
                        || arg instanceof MultipartFile))
                .toArray();

        if (filtered.length == 0) {
            return null;
        }

        return JsonUtils.toJsonString(filtered.length == 1 ? filtered[0] : filtered);
    }

    /**
     * 填充操作人信息
     */
    private void fillOperatorInfo(LogOperation logEntity) {
        try {
            if (LoginUtils.isLogin()) {
                logEntity.setOperatorId(LoginUtils.getUserId());
                logEntity.setOperatorName(LoginUtils.getUsername());
                logEntity.setDeptId(LoginUtils.getDeptId());
            }
        } catch (Exception e) {
            log.debug("获取操作人信息异常: {}", e.getMessage());
        }
    }

    /**
     * 获取当前 HTTP 请求
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 截断字符串到指定长度
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        return str.length() > maxLength ? str.substring(0, maxLength) : str;
    }
}
