package com.glowxq.common.core.util;

import com.glowxq.common.core.common.enums.base.NumberGen;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 编号生成工具类
 * <p>
 * 用于生成各类业务编号，如订单号、流水号等。支持自定义前缀和长度。
 * </p>
 *
 * @author glowxq
 * @since 2025/6/16
 */
public class NumberUtils {

    /**
     * 默认随机字符串长度
     */
    private static final int DEFAULT_LENGTH = 6;

    /**
     * 时间戳格式
     */
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final String UPPER_ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String DIGITS = "0123456789";

    private NumberUtils() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * 根据枚举前缀生成业务编号。
     * <p>
     * 生成格式：前缀 + 6位随机大写字母数字
     * </p>
     *
     * @param prefix 编号前缀枚举
     *
     * @return 生成的业务编号
     */
    public static String generateNumber(NumberGen prefix) {
        return prefix.numberPrefix() + randomStringUpper(DEFAULT_LENGTH);
    }

    /**
     * 根据自定义前缀和长度生成业务编号。
     * <p>
     * 生成格式：前缀 + 指定长度的随机大写字母数字
     * </p>
     *
     * @param prefix 编号前缀字符串
     * @param length 随机部分的长度
     * @return 生成的业务编号
     */
    public static String generateNumber(String prefix, int length) {
        return prefix + randomStringUpper(length);
    }

    /**
     * 生成纯数字随机编号。
     * <p>
     * 生成格式：前缀 + 指定长度的随机数字
     * </p>
     *
     * @param prefix 编号前缀字符串
     * @param length 随机数字的长度
     * @return 生成的业务编号
     */
    public static String generateNumericNumber(String prefix, int length) {
        return prefix + randomNumbers(length);
    }

    /**
     * 生成带时间戳的业务编号。
     * <p>
     * 生成格式：前缀 + 时间戳(yyyyMMddHHmmss) + 4位随机数字
     * </p>
     *
     * @param prefix 编号前缀字符串
     * @return 生成的业务编号
     */
    public static String generateTimestampNumber(String prefix) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        return prefix + timestamp + randomNumbers(4);
    }

    /**
     * 生成指定长度的随机大写字母数字字符串
     */
    private static String randomStringUpper(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(UPPER_ALPHANUMERIC.charAt(random.nextInt(UPPER_ALPHANUMERIC.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的随机数字字符串
     */
    private static String randomNumbers(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
}
