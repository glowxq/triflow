package com.glowxq.common.oss.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 对象信息
 * <p>
 * 存储对象的详细信息。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "对象信息")
public class ObjectInfo {

    /**
     * 对象键
     */
    @Schema(description = "对象键", example = "dev/img/2025/01/21/photo.jpg")
    private String objectKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 对象大小（字节）
     */
    @Schema(description = "对象大小（字节）")
    private Long size;

    /**
     * ETag
     */
    @Schema(description = "ETag")
    private String eTag;

    /**
     * 内容类型
     */
    @Schema(description = "内容类型", example = "image/jpeg")
    private String contentType;

    /**
     * 最后修改时间
     */
    @Schema(description = "最后修改时间")
    private Instant lastModified;

    /**
     * 访问 URL
     */
    @Schema(description = "访问 URL")
    private String url;

}
