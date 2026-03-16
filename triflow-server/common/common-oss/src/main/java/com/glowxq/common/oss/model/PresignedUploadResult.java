package com.glowxq.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 预签名上传结果
 * <p>
 * 用于前端直传场景，客户端使用返回的预签名 URL 直接上传文件到 OSS，
 * 无需经过服务端中转。
 * </p>
 *
 * <p>前端使用示例：</p>
 * <pre>{@code
 * // 使用 fetch API
 * const response = await fetch(uploadUrl, {
 *   method: 'PUT',
 *   body: file,
 *   headers: {
 *     'Content-Type': file.type
 *   }
 * });
 *
 * // 上传成功后，使用 accessUrl 访问文件
 * if (response.ok) {
 *   console.log('文件访问地址:', accessUrl);
 * }
 * }</pre>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "预签名上传结果")
public class PresignedUploadResult {

    /**
     * 预签名上传 URL
     * <p>
     * 客户端使用此 URL 进行 PUT 上传，
     * 支持直接将文件内容作为请求体发送。
     * </p>
     */
    @Schema(description = "预签名上传 URL")
    private String uploadUrl;

    /**
     * 对象键（文件存储路径）
     */
    @Schema(description = "对象键", example = "dev/img/2025/01/21/uuid.jpg")
    private String objectKey;

    /**
     * 文件访问 URL
     * <p>
     * 上传成功后可通过此 URL 访问文件。
     * 如果 Bucket 为私有访问，需要使用预签名下载 URL。
     * </p>
     */
    @Schema(description = "文件访问 URL")
    private String accessUrl;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 预签名 URL 有效期（秒）
     */
    @Schema(description = "有效期（秒）", example = "600")
    private Long expiresInSeconds;

}
