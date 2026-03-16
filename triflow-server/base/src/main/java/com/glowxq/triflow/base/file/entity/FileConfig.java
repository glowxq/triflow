package com.glowxq.triflow.base.file.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.file.enums.StorageTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件存储配置实体类
 * <p>
 * 对应数据库表 file_config，支持多种存储方式配置。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("file_config")
@Schema(description = "文件存储配置")
public class FileConfig implements BaseEntity {

    /**
     * 配置ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "配置ID")
    private Long id;

    /**
     * 配置名称
     */
    @Schema(description = "配置名称")
    private String configName;

    /**
     * 配置标识（唯一，如: aliyun-oss, tencent-cos）
     */
    @Schema(description = "配置标识")
    private String configKey;

    /**
     * 存储类型
     *
     * @see StorageTypeEnum
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 服务端点（如: oss-cn-hangzhou.aliyuncs.com）
     */
    @Schema(description = "服务端点")
    private String endpoint;

    /**
     * AccessKey（加密存储）
     */
    @Schema(description = "AccessKey")
    private String accessKey;

    /**
     * SecretKey（加密存储）
     */
    @Schema(description = "SecretKey")
    private String secretKey;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 存储区域
     */
    @Schema(description = "存储区域")
    private String region;

    /**
     * 自定义域名（CDN加速域名）
     */
    @Schema(description = "自定义域名")
    private String domain;

    /**
     * 基础路径（文件存储的根目录）
     */
    @Schema(description = "基础路径")
    private String basePath;

    /**
     * 是否使用HTTPS
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否使用HTTPS")
    private Integer useHttps;

    /**
     * 是否默认配置（仅一个）
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否默认配置")
    private Integer defaulted;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态")
    private Integer status;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String remark;

    // ========== 必需标准字段 ==========

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

    /** 数据权限部门ID */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

    /** 创建者ID */
    @Schema(description = "创建者ID")
    private Long createBy;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新者ID */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识
     *
     * @see BooleanEnum
     */
    @Schema(description = "删除标识")
    private Integer deleted;

}
