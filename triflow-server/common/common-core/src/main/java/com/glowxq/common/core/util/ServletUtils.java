package com.glowxq.common.core.util;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端工具类
 *
 * @author ruoyi
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ServletUtils {

    /**
     * 最大 大小
     */
    public static final Integer MAX_LOG_SIZE = 1024_0;

    /**
     * 缓存限制
     */
    public static final int CACHE_LIMIT = 2048;

    /**
     * 获取String参数
     */
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取请求attributes
     *
     * @return {@link ServletRequestAttributes }
     */
    private static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    /**
     * 获取String参数
     */
    public static String getParameter(String name, String defaultValue) {
        String value = getRequest().getParameter(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name) {
        String value = getRequest().getParameter(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return NumberUtils.isCreatable(value.trim()) ? Integer.valueOf(value.trim()) : null;
    }

    /**
     * 获取Integer参数
     */
    public static Integer getParameterToInt(String name, Integer defaultValue) {
        Integer value = getParameterToInt(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name) {
        String value = getRequest().getParameter(name);
        if (value == null || value.isBlank()) {
            return null;
        }
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * 获取Boolean参数
     */
    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        Boolean value = getParameterToBool(name);
        return value != null ? value : defaultValue;
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String> getParamMap(ServletRequest request) {
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : getParams(request).entrySet()) {
            params.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        return params;
    }

    /**
     * 获得所有请求参数
     *
     * @param request 请求对象{@link ServletRequest}
     * @return Map
     */
    public static Map<String, String[]> getParams(ServletRequest request) {
        final Map<String, String[]> map = request.getParameterMap();
        return Collections.unmodifiableMap(map);
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static String getHeader(HttpServletRequest request, String name) {
        String value = request.getHeader(name);
        if (StringUtils.isEmpty(value)) {
            return StringUtils.EMPTY;
        }
        return urlDecode(value);
    }

    /**
     * 内容解码
     *
     * @param str 内容
     * @return 解码后的内容
     */
    public static String urlDecode(String str) {
        return URLDecoder.decode(str, StandardCharsets.UTF_8);
    }

    /**
     * 获取headers
     *
     * @param request 请求
     * @return {@link Map }<{@link String }, {@link String }>
     */
    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedCaseInsensitiveMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                String key = enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }

    /**
     * 将字符串渲染到客户端
     *
     * @param response 渲染对象
     * @param string   待渲染的字符串
     */
    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(org.springframework.http.HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().print(string);
        } catch (IOException e) {
            log.error("renderString error:", e);
        }
    }

    /**
     * 是否是Ajax异步请求
     *
     * @param request
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {

        String accept = request.getHeader("accept");
        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            return true;
        }

        String xRequestedWith = request.getHeader("X-Requested-With");
        if (xRequestedWith != null && xRequestedWith.contains("XMLHttpRequest")) {
            return true;
        }

        String uri = request.getRequestURI();
        if (StringUtils.equalsAnyIgnoreCase(uri, ".json", ".xml")) {
            return true;
        }

        String ajax = request.getParameter("__ajax");
        return StringUtils.equalsAnyIgnoreCase(ajax, "json", "xml");
    }

    public static String getClientIP() {
        return getClientIP(getRequest());
    }

    public static String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (ArrayUtils.isNotEmpty(otherHeaderNames)) {
            headers = ArrayUtils.addAll(headers, otherHeaderNames);
        }

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (!isUnknownIp(ip)) {
                return getFirstIp(ip);
            }
        }

        String ip = request.getRemoteAddr();
        return getFirstIp(ip);
    }

    /**
     * 检查IP地址是否为unknown
     */
    private static boolean isUnknownIp(String ip) {
        return ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip.trim());
    }

    /**
     * 从多级反向代理中获取第一个非unknown的IP地址
     */
    private static String getFirstIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return ip;
        }
        // 多级反向代理时，取第一个非unknown的IP
        if (ip.contains(",")) {
            for (String subIp : ip.split(",")) {
                String trimmed = subIp.trim();
                if (!isUnknownIp(trimmed)) {
                    return trimmed;
                }
            }
        }
        return ip.trim();
    }

    /**
     * 内容编码
     *
     * @param str 内容
     * @return 编码后的内容
     */
    public static String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }

    /**
     * 获取响应body
     *
     * @param response 响应
     * @return {@link String }
     */
    public static String getResponseBody(ContentCachingResponseWrapper response) {
        if (null == response) {
            log.info("Response-Body: response is null.");
            return "";
        }
        if (response.getContentSize() > MAX_LOG_SIZE) {
            log.info("Response-Body: response size > 10KB, no printing.");
            return "";
        }
        return new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
    }

    /**
     * 到内容缓存响应包装器
     *
     * @param response 响应
     * @return {@link ContentCachingResponseWrapper }
     */
    public static ContentCachingResponseWrapper toContentCachingResponseWrapper(HttpServletResponse response) {
        ContentCachingResponseWrapper wrapperResponse;
        if (response instanceof ContentCachingResponseWrapper) {
            wrapperResponse = (ContentCachingResponseWrapper) response;
        }
        else {
            wrapperResponse = new ContentCachingResponseWrapper(response);
        }
        return wrapperResponse;
    }

    // ==================== 以下方法从 HttpReqResUtil 迁移 ====================

    /**
     * 将 URL 参数字符串转换为 Map。
     * <p>
     * 解析 URL 参数，将键值对存储在 Map 中。如果参数中有键没有值，则值为空字符串。
     * </p>
     *
     * @param param 包含 URL 参数的字符串
     *
     * @return 包含参数键值对的 Map
     */
    public static Map<String, Object> getUrlParams(String param) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotEmpty(param)) {
            String[] params = param.split("&");
            for (String paramPair : params) {
                String[] p = paramPair.split("=", 2);
                if (p.length == 2) {
                    String key = URLDecoder.decode(p[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(p[1], StandardCharsets.UTF_8);
                    map.put(key, value);
                } else if (p.length == 1) {
                    String key = URLDecoder.decode(p[0], StandardCharsets.UTF_8);
                    map.put(key, "");
                }
            }
        }
        return map;
    }

    /**
     * 获取请求体中的内容。
     * <p>
     * 读取并返回请求体中的所有内容，移除空格和换行符。
     * </p>
     *
     * @param request ServletRequest 对象
     *
     * @return 请求体中的字符串内容
     */
    public static String getRequestBody(ServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = request.getInputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            log.error("Error reading the request body...", ex);
        }
        return stringBuilder.toString().replace(" ", "").replace("\\r\\n", "");
    }

    /**
     * 获取表单参数并转换为 Map<String, Object>。
     * <p>
     * 遍历请求中的所有表单参数，将它们存储在 Map 中。
     * </p>
     *
     * @param request ServletRequest 对象
     *
     * @return 包含表单参数的 Map
     */
    public static Map<String, Object> getFormParameter(ServletRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            paramMap.put(paramName, paramValue);
        }
        return paramMap;
    }
}
