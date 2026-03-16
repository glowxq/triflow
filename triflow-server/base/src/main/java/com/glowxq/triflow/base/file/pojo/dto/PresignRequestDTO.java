package com.glowxq.triflow.base.file.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 预签名上传请求 DTO
 * <p>
 * 用于获取 OSS 预签名上传 URL，支持前端直传。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "预签名上传请求")
public class PresignRequestDTO {

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "photo.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255字符")
    private String originalName;

    /**
     * 文件 MIME 类型
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件类型不能为空")
    private String contentType;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "1024000", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件大小不能为空")
    @Positive(message = "文件大小必须大于0")
    private Long fileSize;

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
     * 存储配置ID（可选，不传则使用默认配置）
     */
    @Schema(description = "存储配置ID")
    private Long configId;

}
