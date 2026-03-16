package com.glowxq.common.core.common.conver;

import com.glowxq.common.core.common.exception.common.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 自定义 String 到 LocalDate 的转换器
 * 支持多种日期格式的转换，包括时间戳
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/6/20
 */
@Slf4j
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate convert(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        source = source.trim();

        try {
            // 尝试解析时间戳（毫秒）
            if (isTimestamp(source)) {
                long timestamp = Long.parseLong(source);
                // 如果是10位时间戳，转换为13位（毫秒）
                if (source.length() == 10) {
                    timestamp = timestamp * 1000;
                }
                return Instant.ofEpochMilli(timestamp)
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate();
            }

            // 优先尝试解析带时间的格式 "yyyy-MM-dd HH:mm:ss"
            if (source.length() > 10 && source.contains(" ")) {
                return LocalDate.parse(source, DATE_TIME_FORMATTER);
            }
            // 其次尝试解析纯日期格式 "yyyy-MM-dd"
            else {
                return LocalDate.parse(source, DATE_FORMATTER);
            }
        } catch (Exception e) {
            log.warn("LocalDate 无法解析日期字符串: {}, 错误: {}", source, e.getMessage(), e);
            throw new BusinessException("LocalDate转换失败 date format: " + source);
        }
    }

    /**
     * 判断字符串是否为时间戳格式
     *
     * @param source 输入字符串
     * @return 是否为时间戳
     */
    private boolean isTimestamp(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        // 检查是否全为数字
        if (!source.matches("\\d+")) {
            return false;
        }

        // 检查长度是否为10位（秒）或13位（毫秒）
        return source.length() == 10 || source.length() == 13;
    }
}