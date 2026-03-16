package com.glowxq.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 文件上传结果
 * <p>
 * 服务端上传成功后返回的结果对象。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "文件上传结果")
public class UploadResult {

    /**
     * 文件访问 URL
     */
    @Schema(description = "文件访问 URL", example = "https://cdn.example.com/dev/img/2025/01/21/uuid.jpg")
    private String url;

    /**
     * 对象键（完整存储路径）
     */
    @Schema(description = "对象键", example = "dev/img/2025/01/21/550e8400-e29b-41d4-a716-446655440000.jpg")
    private String objectKey;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "photo.jpg")
    private String originalFilename;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "my-bucket")
    private String bucketName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long size;

    /**
     * 文件内容类型
     */
    @Schema(description = "文件内容类型", example = "image/jpeg")
    private String contentType;

    /**
     * ETag（对象的 MD5 哈希值）
     */
    @Schema(description = "ETag", example = "d41d8cd98f00b204e9800998ecf8427e")
    private String eTag;

}
