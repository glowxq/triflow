package com.glowxq.common.logger.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志脱敏工具类
 * <p>
 * 根据 logback-desensitize.yml 配置的规则，对日志中的敏感信息进行脱敏处理。
 * 支持手机号、身份证、邮箱、密码等多种脱敏规则。
 * </p>
 *
 * @author glowxq
 * @since 2021/1/9
 */
public class DesensitizationUtil {

    /**
     * 匹配 key:value 或 key=value 格式的正则
     * <p>
     * 该正则表达式第三个分组可能无法匹配以某些特殊符号开头和结尾的值
     * （如密码这种字段，前后如果有很多特殊字段，则无法匹配，建议密码直接加密，无需脱敏）
     * </p>
     */
    public static final Pattern REGEX_PATTERN = Pattern.compile(
            "\\s*(\"?\\w+\"?)\\s*[:=]\\s*([^\\u4e00-\\u9fa5@,.*{\\[\\w]*\\s*)([\\u4e00-\\u9fa5_\\-@.\\w]+)\\s*");

    /** 匹配非数字 */
    public static final Pattern REGEX_NUM = Pattern.compile("[^0-9]");

    /** 脱敏规则类型常量 */
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String IDENTITY = "identity";
    public static final String OTHER = "other";
    public static final String PASSWORD = "password";
    public static final String PWD = "pwd";

    /** 是否开启脱敏 */
    public static Boolean openFlag = false;

    /** 是否忽略 key 的大小写 */
    public static Boolean ignoreFlag = true;

    /** 所有 key:value 配置匹配对 */
    public static Map<String, Object> allPattern;

    /** key 为全小写的 allPattern */
    public static Map<String, Object> lowerCaseAllPattern;

    /** openFlag 初始化标记 */
    private static Boolean initOpenFlag = false;

    /** ignoreFlag 初始化标记 */
    private static Boolean initIgnoreFlag = false;
    /**
     * 判断是否为邮箱
     */
    public static boolean isEmail(String str) {
        String emailRegex = "^[\\w-\\.]+@[\\w-]+\\.[\\w-]{2,4}$";
        return str.matches(emailRegex);
    }
    /**
     * 判断是否为身份证号
     */
    public static boolean isIdentity(String str) {
        return str.matches("(^\\d{18}$)|(^\\d{15}$)");
    }
    /**
     * 判断是否为手机号
     */
    public static boolean isMobile(String str) {
        return str.matches("^1[0-9]{10}$");
    }
    /**
     * 对日志消息进行脱敏处理
     *
     * @param eventFormattedMessage LoggingEvent 的 formattedMessage 属性
     * @return 脱敏后的日志信息，如果无需脱敏则返回空字符串
     */
    public String customChange(String eventFormattedMessage) {
        try {
            Map<String, Object> patternMap = YmlUtils.patternMap;
            if (MapUtils.isEmpty(patternMap)) {
                return "";
            }

            if (!this.checkOpen(patternMap)) {
                return "";
            }

            String originalMessage = eventFormattedMessage;
            boolean changed = false;
            Matcher regexMatcher = REGEX_PATTERN.matcher(eventFormattedMessage);

            while (regexMatcher.find()) {
                try {
                    String key = regexMatcher.group(1).replaceAll("\"", "").trim();
                    String originalValue = regexMatcher.group(3);
                    Object keyPatternValue = this.getKeyIgnoreCase(key);

                    if (keyPatternValue == null || originalValue == null || "null".equals(originalValue)) {
                        continue;
                    }

                    String value = originalValue.replaceAll("\"", "").trim();
                    if ("null".equals(value) || value.equalsIgnoreCase(key)) {
                        continue;
                    }

                    String patternValues = getMultiplePattern(keyPatternValue, value);
                    if (patternValues.isEmpty()) {
                        continue;
                    }

                    patternValues = patternValues.replaceAll(" ", "");

                    if (PASSWORD.equalsIgnoreCase(patternValues)) {
                        String origin = regexMatcher.group(1) + regexMatcher.group(2) + regexMatcher.group(3);
                        originalMessage = originalMessage.replace(origin,
                                regexMatcher.group(1) + regexMatcher.group(2) + "******");
                        changed = true;
                        continue;
                    }

                    String desensitizedValue = processDesensitization(value, patternValues);
                    if (StringUtils.isNotEmpty(desensitizedValue)) {
                        changed = true;
                        String origin = regexMatcher.group(1) + regexMatcher.group(2) + regexMatcher.group(3);
                        originalMessage = originalMessage.replace(origin,
                                regexMatcher.group(1) + regexMatcher.group(2) + desensitizedValue);
                    }
                } catch (Exception e) {
                    return "";
                }
            }
            return changed ? originalMessage : "";
        } catch (Exception e) {
            return "";
        }
    }
    /**
     * 处理脱敏逻辑
     */
    private String processDesensitization(String value, String patternValues) {
        String originalPatternValues = patternValues;
        String filterData = this.getBracketPattern(patternValues);
        if (!filterData.isEmpty()) {
            patternValues = filterData;
        }

        String[] split = patternValues.split(",");
        return getReplaceValue(value, patternValues, split, originalPatternValues);
    }
    /**
     * 获取替换后的 value
     *
     * @param value                 原始值
     * @param patternValues         核心规则
     * @param split                 分割后的规则数组
     * @param originalPatternValues 原始规则
     * @return 脱敏后的值
     */
    private String getReplaceValue(String value, String patternValues, String[] split, String originalPatternValues) {
        if (split.length >= 2 && !patternValues.isEmpty()) {
            String start = REGEX_NUM.matcher(split[0]).replaceAll("");
            String end = REGEX_NUM.matcher(split[1]).replaceAll("");
            int startSub = Integer.parseInt(start) - 1;
            int endSub = Integer.parseInt(end) - 1;
            int valueLength = value.length();

            if (originalPatternValues.contains(">")) {
                return processEndMarker(value, originalPatternValues, startSub, endSub);
            } else if (originalPatternValues.contains("<")) {
                return processStartMarker(value, originalPatternValues, startSub, endSub);
            } else if (originalPatternValues.contains(",")) {
                return this.dataDesensitization(
                        Math.max(startSub, 0),
                        endSub >= 0 ? Math.min(endSub, valueLength - 1) : 0,
                        value);
            }
        } else if (!patternValues.isEmpty()) {
            int beforeIndexOf = patternValues.indexOf("*");
            int last = patternValues.length() - patternValues.lastIndexOf("*");
            int lastIndexOf = value.length() - last;
            return this.dataDesensitization(beforeIndexOf, lastIndexOf, value);
        }
        return value;
    }
    /**
     * 处理结尾标记符脱敏
     */
    private String processEndMarker(String value, String pattern, int startSub, int endSub) {
        int index = pattern.indexOf(">");
        String flagSub = pattern.substring(0, index);
        int indexOf = value.indexOf(flagSub);
        String newValue = value.substring(0, indexOf);
        int newValueLength = newValue.length();
        String append = value.substring(indexOf);

        return this.dataDesensitization(
                Math.max(startSub, 0),
                endSub >= 0 ? Math.min(endSub, newValueLength - 1) : 0,
                newValue) + append;
    }
    /**
     * 处理起始标记符脱敏
     */
    private String processStartMarker(String value, String pattern, int startSub, int endSub) {
        int index = pattern.indexOf("<");
        String flagSub = pattern.substring(0, index);
        int indexOf = value.indexOf(flagSub);
        String newValue = value.substring(indexOf + 1);
        int newValueLength = newValue.length();
        String append = value.substring(0, indexOf + 1);

        return append + this.dataDesensitization(
                Math.max(startSub, 0),
                endSub >= 0 ? Math.min(endSub, newValueLength - 1) : 0,
                newValue);
    }
    /**
     * 根据 key 获取对应的规则（可能是 Map 或 String）
     */
    private Object getKeyIgnoreCase(String key) {
        if (MapUtils.isEmpty(allPattern)) {
            allPattern = YmlUtils.getAllPattern();
        }

        if (!initIgnoreFlag) {
            initIgnoreFlag = true;
            ignoreFlag = YmlUtils.getIgnore();
            if (ignoreFlag) {
                lowerCaseAllPattern = this.transformUpperCase(allPattern);
            }
        }

        if (ignoreFlag) {
            return lowerCaseAllPattern.get(key.toLowerCase());
        } else {
            return allPattern.get(key);
        }
    }
    /**
     * 将 pattern 的 key 值全部转换为小写
     */
    public Map<String, Object> transformUpperCase(Map<String, Object> pattern) {
        Map<String, Object> resultMap = new HashMap<>();
        if (pattern == null || pattern.isEmpty()) {
            return resultMap;
        }

        Set<String> keySet = pattern.keySet();
        for (String key : keySet) {
            resultMap.put(key.toLowerCase(), pattern.get(key));
        }
        return resultMap;
    }
    /**
     * 获取规则字符串
     */
    private String getMultiplePattern(Object patternValue, String newValue) {
        if (patternValue instanceof String stringPattern) {
            return stringPattern;
        } else if (patternValue instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> mapPattern = (Map<String, Object>) patternValue;
            return this.getPatternByMap(mapPattern, newValue);
        } else if (patternValue instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> list = (List<Map<String, Object>>) patternValue;
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> map : list) {
                    String patternStr = this.getPatternByMap(map, newValue);
                    if (!patternStr.isEmpty()) {
                        return patternStr;
                    }
                }
            }
        }
        return "";
    }
    /**
     * 从 Map 中获取规则
     */
    private String getPatternByMap(Map<String, Object> map, String value) {
        if (MapUtils.isEmpty(map)) {
            return "";
        }

        Object customRegexObj = map.get("customRegex");
        Object positionObj = map.get("position");
        String customRegex = customRegexObj instanceof String str ? str : "";
        String position = positionObj instanceof String str ? str : "";

        if (!customRegex.isEmpty() && value.matches(customRegex)) {
            return position;
        }

        Object defaultRegexObj = map.get("defaultRegex");
        String defaultRegex = defaultRegexObj instanceof String str ? str : "";

        if (!defaultRegex.isEmpty()) {
            if (IDENTITY.equals(defaultRegex) && isIdentity(value)) {
                return position;
            } else if (EMAIL.equals(defaultRegex) && isEmail(value)) {
                return position;
            } else if (PHONE.equals(defaultRegex) && isMobile(value)) {
                return position;
            } else if (OTHER.equals(defaultRegex)) {
                return position;
            }
        }
        return "";
    }
    /**
     * 获取括号内的规则
     */
    private String getBracketPattern(String patternValues) {
        if (patternValues.contains("(")) {
            int startCons = patternValues.indexOf("(");
            int endCons = patternValues.indexOf(")");
            return patternValues.substring(startCons + 1, endCons);
        }
        return "";
    }

    /**
     * 检查是否开启脱敏
     */
    private Boolean checkOpen(Map<String, Object> pattern) {
        if (!initOpenFlag) {
            initOpenFlag = true;
            openFlag = YmlUtils.getOpen();
        }
        return openFlag;
    }
    /**
     * 脱敏处理
     *
     * @param start 脱敏开始下标
     * @param end   脱敏结束下标
     * @param value 原始值
     * @return 脱敏后的值
     */
    public String dataDesensitization(int start, int end, String value) {
        if (start < 0 || value == null || value.isEmpty()) {
            return value;
        }

        char[] chars = value.toCharArray();
        int length = chars.length;
        int actualEnd = Math.min(end + 1, length);

        for (int i = start; i < actualEnd && i < length; i++) {
            chars[i] = '*';
        }
        return new String(chars);
    }

}
