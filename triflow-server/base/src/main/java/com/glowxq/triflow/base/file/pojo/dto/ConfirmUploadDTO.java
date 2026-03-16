package com.glowxq.triflow.base.file.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 确认上传完成请求 DTO
 * <p>
 * 前端直传 OSS 成功后，调用此接口确认上传并保存文件记录。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "确认上传完成请求")
public class ConfirmUploadDTO {

    /**
     * OSS 对象键
     */
    @Schema(description = "OSS对象键（存储路径）", example = "uploads/img/2025/01/25/uuid.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "对象键不能为空")
    @Size(max = 500, message = "对象键长度不能超过500字符")
    private String objectKey;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "photo.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255字符")
    private String originalName;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "1024000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件大小不能为空")
    @Positive(message = "文件大小必须大于0")
    private Long fileSize;

    /**
     * 文件 MIME 类型
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件类型不能为空")
    private String contentType;

    /**
     * 文件访问 URL
     */
    @Schema(description = "文件访问URL", example = "https://bucket.oss.com/uploads/img/2025/01/25/uuid.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "访问URL不能为空")
    private String accessUrl;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "my-bucket", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存储桶名称不能为空")
    private String bucketName;

    /**
     * 业务类型（可选）
     */
    @Schema(description = "业务类型", example = "article")
    @Size(max = 50, message = "业务类型长度不能超过50字符")
    private String bizType;

    /**
     * 业务ID（可选）
     */
    @Schema(description = "业务ID", example = "123")
    private Long bizId;

    /**
     * 存储配置ID（可选）
     */
    @Schema(description = "存储配置ID")
    private Long configId;

}
