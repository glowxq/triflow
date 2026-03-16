package com.glowxq.common.oss.support;

import com.glowxq.common.oss.config.S3Configuration;
import com.glowxq.common.oss.enums.FileNamingStrategy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件 Key 生成器
 * <p>
 * 根据配置的命名策略生成文件的存储路径（Object Key）。
 * </p>
 *
 * <p>生成的 Key 格式：{prefix}/{extension}/{date}/{filename}</p>
 * <ul>
 *     <li>prefix: 自定义前缀，如 "uploads", "images" 等</li>
 *     <li>extension: 文件扩展名目录，便于分类管理</li>
 *     <li>date: 日期目录，格式 yyyy/MM/dd</li>
 *     <li>filename: 根据命名策略生成的文件名</li>
 * </ul>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Component
@RequiredArgsConstructor
public class FileKeyGenerator {

    private static final DateTimeFormatter DATE_PATH_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final S3Configuration properties;

    /**
     * 生成文件的 Object Key
     *
     * @param originalFilename 原始文件名
     *
     * @return 生成的 Object Key
     */
    public String generate(String originalFilename) {
        return generate(null, originalFilename);
    }

    /**
     * 生成文件的 Object Key
     *
     * @param prefix           自定义前缀（可选）
     * @param originalFilename 原始文件名
     *
     * @return 生成的 Object Key
     */
    public String generate(String prefix, String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename);
        String datePath = LocalDate.now().format(DATE_PATH_FORMATTER);
        String filename = generateFilename(originalFilename, extension);

        StringBuilder keyBuilder = new StringBuilder();

        if (prefix != null && !prefix.isBlank()) {
            keyBuilder.append(normalizePrefix(prefix)).append("/");
        }

        if (extension != null && !extension.isBlank()) {
            keyBuilder.append(extension.toLowerCase()).append("/");
        }

        keyBuilder.append(datePath).append("/").append(filename);

        return keyBuilder.toString();
    }

    /**
     * 生成带环境标识的 Object Key
     *
     * @param environment      环境标识（如 dev, test, prod）
     * @param prefix           自定义前缀
     * @param originalFilename 原始文件名
     *
     * @return 生成的 Object Key
     */
    public String generateWithEnvironment(String environment, String prefix, String originalFilename) {
        String baseKey = generate(prefix, originalFilename);
        if (environment != null && !environment.isBlank()) {
            return environment + "/" + baseKey;
        }
        return baseKey;
    }

    /**
     * 根据命名策略生成文件名
     */
    private String generateFilename(String originalFilename, String extension) {
        FileNamingStrategy strategy = properties.getNamingStrategy();

        return switch (strategy) {
            case UUID -> generateUuidFilename(extension);
            case ORIGINAL -> originalFilename;
            case ORIGINAL_WITH_TIMESTAMP -> generateTimestampFilename(originalFilename, extension);
        };
    }

    /**
     * 生成 UUID 文件名
     */
    private String generateUuidFilename(String extension) {
        String uuid = UUID.randomUUID().toString();
        if (extension != null && !extension.isBlank()) {
            return uuid + "." + extension;
        }
        return uuid;
    }

    /**
     * 生成带时间戳的文件名
     */
    private String generateTimestampFilename(String originalFilename, String extension) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);

        if (extension != null && !extension.isBlank()) {
            return baseName + "_" + timestamp + "." + extension;
        }
        return baseName + "_" + timestamp;
    }

    /**
     * 规范化前缀
     * <p>
     * 移除首尾斜杠，确保格式统一。
     * </p>
     */
    private String normalizePrefix(String prefix) {
        String normalized = prefix.trim();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

}
