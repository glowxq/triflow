package com.glowxq.common.core.common.conver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 灵活的 LocalDate JSON 反序列化器
 * 支持多种格式：时间戳、日期字符串、日期时间字符串
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/10/19
 */
@Slf4j
public class FlexibleLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        value = value.trim();

        try {
            // 尝试解析时间戳
            if (isTimestamp(value)) {
                long timestamp = Long.parseLong(value);
                // 如果是10位时间戳，转换为13位（毫秒）
                if (value.length() == 10) {
                    timestamp = timestamp * 1000;
                }
                return Instant.ofEpochMilli(timestamp)
                              .atZone(ZoneId.systemDefault())
                              .toLocalDate();
            }

            // 尝试解析带时间的格式 "yyyy-MM-dd HH:mm:ss"
            if (value.length() > 10 && value.contains(" ")) {
                return LocalDate.parse(value, DATE_TIME_FORMATTER);
            }

            // 尝试解析纯日期格式 "yyyy-MM-dd"
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (Exception e) {
            log.warn("LocalDate JSON反序列化失败: {}, 错误: {}", value, e.getMessage());
            throw new IOException("无法解析LocalDate: " + value, e);
        }
    }

    /**
     * 判断字符串是否为时间戳格式
     *
     * @param value 输入字符串
     * @return 是否为时间戳
     */
    private boolean isTimestamp(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        // 检查是否全为数字
        if (!value.matches("\\d+")) {
            return false;
        }

        // 检查长度是否为10位（秒）或13位（毫秒）
        return value.length() == 10 || value.length() == 13;
    }
}
