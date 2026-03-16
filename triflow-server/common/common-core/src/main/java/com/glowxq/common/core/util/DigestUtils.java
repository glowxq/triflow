package com.glowxq.common.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要工具类
 * <p>
 * 提供常用的摘要算法方法，基于 Java 标准库 {@link MessageDigest} 实现。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
public class DigestUtils {

    private DigestUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 计算 MD5 摘要（返回 hex 字符串）
     *
     * @param data 原始数据
     * @return MD5 hex 字符串
     */
    public static String md5Hex(String data) {
        return digest("MD5", data);
    }

    /**
     * 计算 SHA-256 摘要（返回 hex 字符串）
     *
     * @param data 原始数据
     * @return SHA-256 hex 字符串
     */
    public static String sha256Hex(String data) {
        return digest("SHA-256", data);
    }

    /**
     * 计算 SHA-1 摘要（返回 hex 字符串）
     *
     * @param data 原始数据
     * @return SHA-1 hex 字符串
     */
    public static String sha1Hex(String data) {
        return digest("SHA-1", data);
    }

    /**
     * 使用指定算法计算摘要
     *
     * @param algorithm 算法名称
     * @param data      原始数据
     * @return hex 字符串
     */
    private static String digest(String algorithm, String data) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unsupported digest algorithm: " + algorithm, e);
        }
    }

    /**
     * 字节数组转 hex 字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
