package com.glowxq.common.logger.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 日志脱敏配置文件解析工具类
 * <p>
 * 用于解析 logback-desensitize.yml 配置文件，获取脱敏规则。
 * </p>
 *
 * @author glowxq
 * @since 2021/1/9
 */
public class YmlUtils {

    private YmlUtils() {
        throw new IllegalStateException("Utility class");
    }

    /** 默认脱敏配置文件名 */
    public static final String PROPERTY_NAME = "logback-desensitize.yml";

    /** 配置键：单规则 */
    public static final String PATTERN = "pattern";

    /** 配置键：多规则 */
    public static final String PATTERNS = "patterns";

    /** 配置键：是否开启脱敏 */
    public static final String OPEN_FLAG = "open";

    /** 配置键：是否忽略大小写匹配 */
    public static final String IGNORE = "ignore";

    /** 配置键：脱敏配置文件头 */
    public static final String YML_HEAD_KEY = "log-desensitize";

    /** 配置键：自定义规则 */
    public static final String CUSTOM = "custom";

    /** YAML 输出配置 */
    public static final DumperOptions OPTIONS = new DumperOptions();

    /** YAML 脱敏配置内容 */
    protected static final Map<String, Object> patternMap;

    static {
        OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        patternMap = getYmlByName(PROPERTY_NAME);
    }

    /**
     * 获取 YAML 配置文件内容
     *
     * @param fileName YAML 配置文件名
     * @return 配置信息（Map 格式）
     */
    private static Map<String, Object> getYmlByName(String fileName) {
        if (MapUtils.isEmpty(patternMap)) {
            try {
                Object fromYml = getFromYml(fileName, YML_HEAD_KEY);
                if (fromYml instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> result = (Map<String, Object>) fromYml;
                    return result;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return patternMap;
    }

    /**
     * 从 YAML 配置文件中获取指定 key 的值
     *
     * @param fileName YAML 文件名
     * @param key      配置键
     * @return 配置值或整个 Map
     */
    public static Object getFromYml(String fileName, String key) {
        Yaml yaml = new Yaml(OPTIONS);
        InputStream inputStream = YmlUtils.class.getClassLoader().getResourceAsStream(fileName);
        @SuppressWarnings("unchecked")
        HashMap<String, Object> map = yaml.loadAs(inputStream, HashMap.class);
        return Objects.nonNull(map) && !map.isEmpty() ? map.get(key) : map;
    }

    /**
     * 获取 key 为 pattern 的值
     *
     * @return pattern 对应的 Map，或者 null
     */
    public static Map<String, Object> getPattern() {
        Object pattern = patternMap.get(PATTERN);
        if (pattern instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) pattern;
            return result;
        }
        return null;
    }

    /**
     * 获取所有 pattern（包含 key 为 pattern 和 patterns 的配置）
     *
     * @return 合并后的 pattern Map
     */
    public static Map<String, Object> getAllPattern() {
        Map<String, Object> allPattern = new HashMap<>();
        Map<String, Object> pattern = getPattern();
        Map<String, Object> patterns = getPatterns();

        if (MapUtils.isNotEmpty(patterns)) {
            allPattern.putAll(patterns);
        }
        if (MapUtils.isNotEmpty(pattern)) {
            allPattern.putAll(pattern);
        }
        return allPattern;
    }

    /**
     * 获取 key 为 patterns 的值
     *
     * @return patterns 对应的 Map，或者 null
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getPatterns() {
        Map<String, Object> map = new HashMap<>();
        Object patterns = patternMap.get(PATTERNS);

        if (patterns instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) patterns;
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> maps : list) {
                    assembleMap(map, maps);
                }
                return map;
            }
        }

        if (patterns instanceof Map) {
            assembleMap(map, (Map<String, Object>) patterns);
            return map;
        }
        return null;
    }

    /**
     * 将 patterns 中每个 key 对应的规则组装到 Map 中
     *
     * @param map      目标 Map
     * @param patterns patterns 配置
     */
    private static void assembleMap(Map<String, Object> map, Map<String, Object> patterns) {
        Object key = patterns.get("key");
        if (key instanceof String keyString) {
            String keyWords = keyString.replace(" ", "");
            String[] keyArr = keyWords.split(",");
            for (String keyStr : keyArr) {
                map.put(keyStr, patterns.get(CUSTOM));
            }
        }
    }

    /**
     * 是否开启脱敏，默认不开启
     *
     * @return 是否开启脱敏
     */
    public static Boolean getOpen() {
        Object flag = patternMap.get(OPEN_FLAG);
        return flag instanceof Boolean boolFlag ? boolFlag : false;
    }

    /**
     * 是否忽略大小写匹配，默认开启
     *
     * @return 是否忽略大小写匹配
     */
    public static Boolean getIgnore() {
        Object flag = patternMap.get(IGNORE);
        return flag instanceof Boolean boolFlag ? boolFlag : true;
    }
}
