package com.glowxq.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * STS 临时安全凭证
 * <p>
 * 用于前端直传场景，客户端使用临时凭证直接访问 OSS。
 * 相比预签名 URL，STS 凭证更加灵活，支持：
 * <ul>
 *     <li>多文件上传</li>
 *     <li>自定义上传路径</li>
 *     <li>更细粒度的权限控制</li>
 * </ul>
 * </p>
 *
 * <p>前端使用示例（使用 AWS SDK for JavaScript）：</p>
 * <pre>{@code
 * import { S3Client, PutObjectCommand } from "@aws-sdk/client-s3";
 *
 * const s3Client = new S3Client({
 *   region: stsCredentials.region,
 *   endpoint: stsCredentials.endpoint,
 *   credentials: {
 *     accessKeyId: stsCredentials.accessKeyId,
 *     secretAccessKey: stsCredentials.secretAccessKey,
 *     sessionToken: stsCredentials.sessionToken
 *   }
 * });
 *
 * const command = new PutObjectCommand({
 *   Bucket: stsCredentials.bucketName,
 *   Key: 'path/to/file.jpg',
 *   Body: file
 * });
 *
 * await s3Client.send(command);
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "STS 临时安全凭证")
public class StsCredentials {

    /**
     * 临时访问密钥 ID
     */
    @Schema(description = "临时访问密钥 ID")
    private String accessKeyId;

    /**
     * 临时访问密钥密文
     */
    @Schema(description = "临时访问密钥密文")
    private String secretAccessKey;

    /**
     * 安全令牌
     */
    @Schema(description = "安全令牌")
    private String sessionToken;

    /**
     * 凭证过期时间
     */
    @Schema(description = "凭证过期时间")
    private Instant expiration;

    /**
     * S3 端点
     */
    @Schema(description = "S3 端点", example = "https://s3.amazonaws.com")
    private String endpoint;

    /**
     * 区域
     */
    @Schema(description = "区域", example = "us-east-1")
    private String region;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 允许上传的路径前缀
     * <p>
     * STS 策略中限制的路径前缀，客户端只能上传到此前缀下。
     * </p>
     */
    @Schema(description = "允许上传的路径前缀", example = "uploads/user123/")
    private String allowedPathPrefix;

    /**
     * 凭证剩余有效时间（秒）
     */
    @Schema(description = "剩余有效时间（秒）")
    private Long expiresInSeconds;

}
