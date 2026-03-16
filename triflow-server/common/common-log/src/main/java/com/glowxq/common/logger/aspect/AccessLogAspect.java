package com.glowxq.common.logger.aspect;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.glowxq.common.core.common.entity.AccessRequestLog;
import com.glowxq.common.core.common.entity.AccessResponseLog;
import com.glowxq.common.core.common.filter.trace.TraceLogContext;
import com.glowxq.common.core.util.JsonUtils;
import com.glowxq.common.core.util.ServletUtils;
import com.glowxq.common.security.pojo.WhitelistProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * HTTP 访问日志切面
 * <p>
 * 记录 Controller 层的请求和响应日志，支持慢查询检测和敏感接口过滤。
 * </p>
 *
 * @author glowxq
 * @since 2024/01/01
 */
@Component
@Aspect
@Slf4j(topic = "http-log")
@RequiredArgsConstructor
public class AccessLogAspect {

    private final WhitelistProperties whitelistProperties;

    /** 请求开始时间标记 */
    private static final String SEND_TIME = "SEND_TIME";

    /** 慢查询阈值（毫秒） */
    private static final long SLOW_QUERY_THRESHOLD = 2000;

    /** 路径匹配器（线程安全，可复用） */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** 白名单缓存 */
    private Set<String> whitelistCache;

    @PostConstruct
    public void init() {
        // 初始化时添加固定的白名单路径
        whitelistCache = new HashSet<>(whitelistProperties.getWhitelist());
        whitelistCache.add("/auth/logout");
    }

    @Pointcut("execution(public * com.glowxq..*.controller..*.*(..))")
    public void controllerPointcut() {
        // 切点定义：匹配所有 controller 包下的公共方法
    }

    @Before("controllerPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            AccessRequestLog requestLog = buildRequestLog(joinPoint, request);

            if (shouldRecordUserId(joinPoint, request)) {
                requestLog.setUserId(StpUtil.getLoginIdAsString());
            }

            log.info("[aop] request log : {}", JsonUtils.toJsonString(requestLog));
            request.setAttribute(SEND_TIME, System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error in doBefore method", e);
        }
    }

    @AfterReturning(returning = "returnValue", pointcut = "controllerPointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
        try {
            HttpServletRequest request = getCurrentHttpRequest();
            AccessResponseLog responseLog = buildResponseLog(joinPoint, returnValue, request);

            if (shouldRecordUserId(joinPoint, request)) {
                responseLog.setUserId(StpUtil.getLoginIdAsString());
            }

            // 仅记录慢查询日志
            if (responseLog.getMs() >= SLOW_QUERY_THRESHOLD) {
                log.info("[aop] slow response log : {}", JsonUtils.toJsonString(responseLog));
            }
        } catch (Exception e) {
            log.error("Error in doAfterReturning method", e);
        }
    }

    /**
     * 判断是否需要记录用户ID
     */
    private boolean shouldRecordUserId(JoinPoint joinPoint, HttpServletRequest request) {
        return isNotSaIgnoreInterface(joinPoint)
                && isNotWhitelist(request.getRequestURI(), request.getContextPath());
    }

    private HttpServletRequest getCurrentHttpRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes())).getRequest();
    }

    private AccessRequestLog buildRequestLog(JoinPoint joinPoint, HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = ServletUtils.getUrlParams(queryString);
        String body = ServletUtils.getRequestBody(request);
        Map<String, Object> parameter = ServletUtils.getFormParameter(request);
        Object[] args = filterArguments(joinPoint.getArgs());

        return AccessRequestLog.builder()
                               .traceId(TraceLogContext.getSpanId())
                               .url(request.getRequestURI())
                               .timestamp(System.currentTimeMillis())
                               .method(request.getMethod())
                               .ip(ServletUtils.getClientIP(request))
                               .param(urlParams)
                               .body(body)
                               .form(parameter)
                               .requestBody(args)
                               .type("request")
                               .contentType(request.getContentType())
                .build();
    }

    private AccessResponseLog buildResponseLog(JoinPoint joinPoint, Object returnValue, HttpServletRequest request) {
        String queryString = request.getQueryString();
        Map<String, Object> urlParams = ServletUtils.getUrlParams(queryString);
        Map<String, Object> parameter = ServletUtils.getFormParameter(request);
        long sendTime = (long) request.getAttribute(SEND_TIME);
        long ms = System.currentTimeMillis() - sendTime;

        return AccessResponseLog.builder()
                                .timestamp(System.currentTimeMillis())
                                .traceId(TraceLogContext.getSpanId())
                                .param(JsonUtils.toJsonString(urlParams))
                                .form(JsonUtils.toJsonString(parameter))
                                .reqBody(filterArguments(joinPoint.getArgs()))
                                .resBody(returnValue)
                                .method(request.getMethod())
                                .url(request.getRequestURI())
                                .ms(ms)
                                .build();
    }

    /**
     * 过滤不可序列化的参数
     */
    private Object[] filterArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return new Object[0];
        }
        return Arrays.stream(args)
                     .filter(arg -> !(arg instanceof HttpServletResponse
                             || arg instanceof HttpServletRequest
                             || arg instanceof MultipartFile))
                     .toArray();
    }

    /**
     * 判断请求路径是否不在白名单中
     */
    private boolean isNotWhitelist(String requestURI, String contextPath) {
        if (requestURI == null || requestURI.isEmpty()) {
            return true;
        }
        String pathAfterContext = "/".equals(contextPath)
                                  ? (requestURI.length() > 1 ? requestURI.substring(1) : requestURI)
                                  : requestURI.replace(contextPath, "");
        return whitelistCache.stream()
                             .noneMatch(pattern -> PATH_MATCHER.match(pattern, pathAfterContext));
    }

    /**
     * 判断方法是否没有 @SaIgnore 注解
     */
    private boolean isNotSaIgnoreInterface(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(SaIgnore.class);
    }
}
