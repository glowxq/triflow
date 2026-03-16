package com.glowxq.core.common.conver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glowxq.common.core.common.conver.StringToLocalDateConverter;
import com.glowxq.common.core.common.conver.StringToLocalDateTimeConverter;
import com.glowxq.common.core.common.conver.TimestampToLocalDateConverter;
import com.glowxq.common.core.common.conver.TimestampToLocalDateTimeConverter;
import com.glowxq.common.core.util.DateUtils;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 日期时间转换器测试类
 *
 * @author glowxq
 * @version 1.0
 * @date 2025/10/19
 */
@SpringBootTest
public class DateTimeConverterTest {

    @Test
    public void testStringToLocalDateConverter() {
        StringToLocalDateConverter converter = new StringToLocalDateConverter();

        // 测试日期字符串
        LocalDate date1 = converter.convert("2025-10-19");
        assertNotNull(date1);
        assertEquals(2025, date1.getYear());
        assertEquals(10, date1.getMonthValue());
        assertEquals(19, date1.getDayOfMonth());

        // 测试日期时间字符串
        LocalDate date2 = converter.convert("2025-10-19 15:30:45");
        assertNotNull(date2);
        assertEquals(2025, date2.getYear());
        assertEquals(10, date2.getMonthValue());
        assertEquals(19, date2.getDayOfMonth());

        // 测试13位时间戳（毫秒）
        LocalDate date3 = converter.convert("1729332645000"); // 2024-10-19 15:30:45
        assertNotNull(date3);

        // 测试10位时间戳（秒）
        LocalDate date4 = converter.convert("1729332645"); // 2024-10-19 15:30:45
        assertNotNull(date4);

        // 测试空值
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
        assertNull(converter.convert("   "));
    }

    @Test
    public void testStringToLocalDateTimeConverter() {
        StringToLocalDateTimeConverter converter = new StringToLocalDateTimeConverter();

        // 测试日期时间字符串
        LocalDateTime dateTime1 = converter.convert("2025-10-19 15:30:45");
        assertNotNull(dateTime1);
        assertEquals(2025, dateTime1.getYear());
        assertEquals(10, dateTime1.getMonthValue());
        assertEquals(19, dateTime1.getDayOfMonth());
        assertEquals(15, dateTime1.getHour());
        assertEquals(30, dateTime1.getMinute());
        assertEquals(45, dateTime1.getSecond());

        // 测试日期字符串（应该转换为00:00:00）
        LocalDateTime dateTime2 = converter.convert("2025-10-19");
        assertNotNull(dateTime2);
        assertEquals(0, dateTime2.getHour());
        assertEquals(0, dateTime2.getMinute());
        assertEquals(0, dateTime2.getSecond());

        // 测试13位时间戳（毫秒）
        LocalDateTime dateTime3 = converter.convert("1729332645000");
        assertNotNull(dateTime3);

        // 测试10位时间戳（秒）
        LocalDateTime dateTime4 = converter.convert("1729332645");
        assertNotNull(dateTime4);

        // 测试空值
        assertNull(converter.convert(null));
        assertNull(converter.convert(""));
        assertNull(converter.convert("   "));
    }

    @Test
    public void testTimestampToLocalDateConverter() {
        TimestampToLocalDateConverter converter = new TimestampToLocalDateConverter();

        // 测试13位时间戳（毫秒）
        LocalDate date1 = converter.convert(1729332645000L);
        assertNotNull(date1);

        // 测试10位时间戳（秒）
        LocalDate date2 = converter.convert(1729332645L);
        assertNotNull(date2);

        // 测试空值
        assertNull(converter.convert(null));
    }

    @Test
    public void testTimestampToLocalDateTimeConverter() {
        TimestampToLocalDateTimeConverter converter = new TimestampToLocalDateTimeConverter();

        // 测试13位时间戳（毫秒）
        LocalDateTime dateTime1 = converter.convert(1729332645000L);
        assertNotNull(dateTime1);

        // 测试10位时间戳（秒）
        LocalDateTime dateTime2 = converter.convert(1729332645L);
        assertNotNull(dateTime2);

        // 测试空值
        assertNull(converter.convert(null));
    }

    @Test
    public void testDateUtilsTimestampMethods() {
        // 测试时间戳转LocalDate
        LocalDate date1 = DateUtils.timestampToLocalDate(1729332645000L);
        assertNotNull(date1);

        LocalDate date2 = DateUtils.timestampToLocalDate(1729332645L);
        assertNotNull(date2);

        // 测试时间戳转LocalDateTime
        LocalDateTime dateTime1 = DateUtils.timestampToLocalDateTime(1729332645000L);
        assertNotNull(dateTime1);

        LocalDateTime dateTime2 = DateUtils.timestampToLocalDateTime(1729332645L);
        assertNotNull(dateTime2);

        // 测试LocalDate转时间戳
        LocalDate testDate = LocalDate.of(2025, 10, 19);
        Long timestamp1 = DateUtils.localDateToTimestamp(testDate);
        assertNotNull(timestamp1);

        // 测试LocalDateTime转时间戳
        LocalDateTime testDateTime = LocalDateTime.of(2025, 10, 19, 15, 30, 45);
        Long timestamp2 = DateUtils.localDateTimeToTimestamp(testDateTime);
        assertNotNull(timestamp2);

        // 测试空值
        assertNull(DateUtils.timestampToLocalDate(null));
        assertNull(DateUtils.timestampToLocalDateTime(null));
        assertNull(DateUtils.localDateToTimestamp(null));
        assertNull(DateUtils.localDateTimeToTimestamp(null));
    }

    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册自定义反序列化器
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

        // 测试时间戳JSON反序列化
        String json1 = "{\"localDate\":\"1729332645000\",\"localDateTime\":\"1729332645000\"}";
        TestDTO dto1 = objectMapper.readValue(json1, TestDTO.class);
        assertNotNull(dto1.getLocalDate());
        assertNotNull(dto1.getLocalDateTime());

        // 测试日期字符串JSON反序列化
        String json2 = "{\"localDate\":\"2025-10-19\",\"localDateTime\":\"2025-10-19 15:30:45\"}";
        TestDTO dto2 = objectMapper.readValue(json2, TestDTO.class);
        assertNotNull(dto2.getLocalDate());
        assertNotNull(dto2.getLocalDateTime());
    }

    @Data
    static class TestDTO {

        private LocalDate localDate;

        private LocalDateTime localDateTime;
    }
}
