package com.glowxq.common.oss.enums;

/**
 * 文件命名策略枚举
 * <p>
 * 定义上传文件时的命名规则。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
public enum FileNamingStrategy {

    /**
     * 使用 UUID 命名
     * <p>
     * 每个文件都将被分配一个唯一的 UUID，
     * 有助于避免命名冲突，保证文件名的唯一性。
     * </p>
     * <p>
     * 示例: 550e8400-e29b-41d4-a716-446655440000.jpg
     * </p>
     */
    UUID,

    /**
     * 保留原始文件名
     * <p>
     * 保持文件的原始名称，直观且易于识别。
     * 如果出现同名文件，后上传的会覆盖先前的文件。
     * </p>
     */
    ORIGINAL,

    /**
     * 原始文件名 + 时间戳
     * <p>
     * 在原始文件名基础上添加时间戳后缀，
     * 既保留可读性又避免命名冲突。
     * </p>
     * <p>
     * 示例: document_20250121143052.pdf
     * </p>
     */
    ORIGINAL_WITH_TIMESTAMP
}
