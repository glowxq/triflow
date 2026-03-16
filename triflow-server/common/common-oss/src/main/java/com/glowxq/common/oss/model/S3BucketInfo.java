package com.glowxq.common.oss.model;

import com.glowxq.common.oss.enums.AccessControlType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 存储桶信息
 * <p>
 * 存储桶的详细信息，包括名称、创建时间、访问控制等。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@Schema(description = "存储桶信息")
public class S3BucketInfo {

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "my-bucket")
    private String name;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Instant creationDate;

    /**
     * 区域
     */
    @Schema(description = "区域", example = "us-east-1")
    private String region;

    /**
     * 访问控制类型
     */
    @Schema(description = "访问控制类型")
    private AccessControlType accessControl;

    /**
     * 是否启用版本控制
     */
    @Schema(description = "是否启用版本控制")
    private Boolean versioningEnabled;

    /**
     * 存储桶访问 URL
     */
    @Schema(description = "存储桶访问 URL")
    private String bucketUrl;

}
