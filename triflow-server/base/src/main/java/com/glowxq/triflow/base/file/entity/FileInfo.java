package com.glowxq.triflow.base.file.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.file.enums.FileCategoryEnum;
import com.glowxq.triflow.base.file.enums.FileStatusEnum;
import com.glowxq.triflow.base.file.enums.StorageTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息实体类
 * <p>
 * 对应数据库表 file_info，记录所有上传文件的元数据信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("file_info")
@Schema(description = "文件信息")
public class FileInfo implements BaseEntity {

    /**
     * 文件ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "文件ID")
    private Long id;

    /**
     * 存储配置ID
     */
    @Schema(description = "存储配置ID")
    private Long configId;

    /**
     * 文件名（存储名，如: 20250124/abc123.jpg）
     */
    @Schema(description = "文件名")
    private String fileName;

    /**
     * 原始文件名（上传时的文件名）
     */
    @Schema(description = "原始文件名")
    private String originalName;

    /**
     * 文件路径（完整的存储路径）
     */
    @Schema(description = "文件路径")
    private String filePath;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 文件MIME类型（如: image/jpeg, application/pdf）
     */
    @Schema(description = "文件MIME类型")
    private String fileType;

    /**
     * 文件扩展名（如: jpg, pdf）
     */
    @Schema(description = "文件扩展名")
    private String fileExt;

    /**
     * 存储类型
     *
     * @see StorageTypeEnum
     */
    @Schema(description = "存储类型")
    private String storageType;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /**
     * 对象键（云存储中的唯一标识）
     */
    @Schema(description = "对象键")
    private String objectKey;

    /**
     * 文件MD5值（用于秒传和去重）
     */
    @Schema(description = "文件MD5值")
    private String md5;

    /**
     * 文件SHA256值（安全校验）
     */
    @Schema(description = "文件SHA256值")
    private String sha256;

    /**
     * 文件分类
     *
     * @see FileCategoryEnum
     */
    @Schema(description = "文件分类")
    private String category;

    /**
     * 业务类型（关联的业务模块，如: user, article）
     */
    @Schema(description = "业务类型")
    private String bizType;

    /**
     * 业务ID（关联的业务记录ID）
     */
    @Schema(description = "业务ID")
    private Long bizId;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称")
    private String uploaderName;

    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    /**
     * 是否公开访问
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否公开访问")
    private Integer publicAccess;

    /**
     * 过期时间（临时文件使用）
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数")
    private Integer downloadCount;

    /**
     * 最后访问时间
     */
    @Schema(description = "最后访问时间")
    private LocalDateTime lastAccessTime;

    /**
     * 状态
     *
     * @see FileStatusEnum
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
