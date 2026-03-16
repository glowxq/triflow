package com.glowxq.common.core.common.conver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 时间戳到 LocalDateTime 的转换器
 * 支持秒级和毫秒级时间戳
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/10/19
 */
@Slf4j
public class TimestampToLocalDateTimeConverter implements Converter<Long, LocalDateTime> {

    @Override
    public LocalDateTime convert(Long source) {
        if (source == null) {
            return null;
        }

        try {
            long timestamp = source;

            // 如果是10位时间戳（秒级），转换为13位（毫秒级）
            if (String.valueOf(source).length() == 10) {
                timestamp = source * 1000;
            }

            return Instant.ofEpochMilli(timestamp)
                          .atZone(ZoneId.systemDefault())
                          .toLocalDateTime();
        } catch (Exception e) {
            log.warn("时间戳转换LocalDateTime失败: {}, 错误: {}", source, e.getMessage(), e);
            return null;
        }
    }
}
