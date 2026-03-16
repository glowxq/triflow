package com.glowxq.common.oss.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * OSS 服务提供商枚举
 * <p>
 * 支持的 S3 兼容对象存储服务提供商。
 * 所有提供商都遵循 S3 标准 API，但在某些配置上存在差异。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Getter
@RequiredArgsConstructor
public enum OssProvider {

    /**
     * Amazon Web Services S3
     */
    AWS("Amazon S3", false),

    /**
     * 阿里云 OSS
     */
    ALIYUN("阿里云 OSS", false),

    /**
     * 腾讯云 COS
     */
    TENCENT("腾讯云 COS", false),

    /**
     * 七牛云 Kodo
     */
    QINIU("七牛云 Kodo", false),

    /**
     * MinIO
     * <p>
     * MinIO 使用路径风格访问（Path-Style）
     * </p>
     */
    MINIO("MinIO", true),

    /**
     * 华为云 OBS
     */
    HUAWEI("华为云 OBS", false);

    /**
     * 提供商名称
     */
    private final String displayName;

    /**
     * 是否使用路径风格访问
     * <p>
     * true: http://endpoint/bucket/object (Path-Style)
     * false: http://bucket.endpoint/object (Virtual-Hosted-Style)
     * </p>
     */
    private final boolean pathStyleAccess;
}
