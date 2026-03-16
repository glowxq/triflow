package com.glowxq.common.oss.config;

import com.glowxq.common.oss.enums.FileNamingStrategy;
import com.glowxq.common.oss.enums.OssProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * S3 兼容对象存储配置属性
 * <p>
 * 基于 S3 标准协议的对象存储服务配置，支持 AWS S3、阿里云 OSS、腾讯云 COS、MinIO 等。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@ConfigurationProperties(prefix = "s3")
@Schema(description = "S3 兼容对象存储配置属性")
public class S3Configuration {

    // ===== 协议常量 =====
    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    // ===== 默认值常量 =====
    /** 默认区域 */
    public static final String DEFAULT_REGION = "us-east-1";
    /** 默认预签名 URL 有效期（分钟） */
    public static final int DEFAULT_PRESIGNED_URL_EXPIRE_MINUTES = 10;
    /** 默认 STS 临时凭证有效期（秒），1小时 */
    public static final int DEFAULT_STS_TOKEN_DURATION_SECONDS = 3600;
    /** 默认 STS 角色会话名称 */
    public static final String DEFAULT_STS_ROLE_SESSION_NAME = "s3-upload-session";

    /**
     * 是否启用 OSS 模块
     */
    @Schema(description = "是否启用", defaultValue = "true")
    private boolean enabled = true;

    /**
     * 服务提供商
     */
    @Schema(description = "服务提供商", example = "MINIO")
    private OssProvider provider = OssProvider.MINIO;

    /**
     * S3 兼容服务端点
     * <p>
     * 各提供商端点示例：
     * <ul>
     *     <li>AWS: s3.amazonaws.com 或 s3.{region}.amazonaws.com</li>
     *     <li>阿里云: oss-{region}.aliyuncs.com</li>
     *     <li>腾讯云: cos.{region}.myqcloud.com</li>
     *     <li>MinIO: your-minio-server:9000</li>
     * </ul>
     * </p>
     */
    @Schema(description = "服务端点", example = "s3.amazonaws.com")
    private String endpoint;

    /**
     * 区域
     * <p>
     * 某些提供商需要指定区域，如 AWS。
     * 对于不需要区域的提供商（如 MinIO），可使用默认值。
     * </p>
     */
    @Schema(description = "区域", example = "us-east-1")
    private String region = DEFAULT_REGION;

    /**
     * 访问密钥 ID
     */
    @Schema(description = "访问密钥 ID")
    private String accessKey;

    /**
     * 访问密钥密文
     */
    @Schema(description = "访问密钥密文")
    private String secretKey;

    /**
     * 默认存储桶名称
     */
    @Schema(description = "默认存储桶名称", example = "my-bucket")
    private String bucketName;

    /**
     * 自定义域名
     * <p>
     * 用于 CDN 加速或自定义域名访问。
     * 如果配置了自定义域名，生成的访问 URL 将使用此域名。
     * </p>
     */
    @Schema(description = "自定义域名", example = "https://cdn.example.com")
    private String domain;

    /**
     * 文件命名策略
     */
    @Schema(description = "文件命名策略", defaultValue = "UUID")
    private FileNamingStrategy namingStrategy = FileNamingStrategy.UUID;

    /**
     * 是否使用 HTTPS
     */
    @Schema(description = "是否使用 HTTPS", defaultValue = "true")
    private boolean useHttps = true;

    /**
     * 预签名 URL 默认有效期（分钟）
     */
    @Schema(description = "预签名 URL 默认有效期（分钟）", defaultValue = "10")
    private int presignedUrlExpireMinutes = DEFAULT_PRESIGNED_URL_EXPIRE_MINUTES;

    /**
     * STS 临时凭证有效期（秒）
     * <p>
     * 范围：900 ~ 43200（15分钟 ~ 12小时）
     * </p>
     */
    @Schema(description = "STS 临时凭证有效期（秒）", defaultValue = "3600")
    private int stsTokenDurationSeconds = DEFAULT_STS_TOKEN_DURATION_SECONDS;

    /**
     * STS 角色 ARN
     * <p>
     * 用于 AssumeRole 方式获取临时凭证。
     * 格式示例：arn:aws:iam::123456789012:role/S3UploadRole
     * </p>
     */
    @Schema(description = "STS 角色 ARN")
    private String stsRoleArn;

    /**
     * STS 角色会话名称
     */
    @Schema(description = "STS 角色会话名称", defaultValue = "s3-upload-session")
    private String stsRoleSessionName = DEFAULT_STS_ROLE_SESSION_NAME;

    /**
     * 构建完整的端点 URI
     *
     * @return 端点 URI
     */
    public String getEndpointUrl() {
        if (endpoint.startsWith(HTTP_PREFIX) || endpoint.startsWith(HTTPS_PREFIX)) {
            return endpoint;
        }
        String scheme = useHttps ? HTTPS_PREFIX : HTTP_PREFIX;
        return scheme + endpoint;
    }

}
