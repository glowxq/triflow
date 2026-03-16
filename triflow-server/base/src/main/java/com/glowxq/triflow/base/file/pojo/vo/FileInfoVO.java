package com.glowxq.triflow.base.file.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.triflow.base.file.entity.FileInfo;
import com.glowxq.triflow.base.file.enums.FileCategoryEnum;
import com.glowxq.triflow.base.file.enums.FileStatusEnum;
import com.glowxq.triflow.base.file.enums.StorageTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文件信息响应")
@AutoMapper(target = FileInfo.class)
public class FileInfoVO implements BaseVO {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID", example = "1")
    private Long id;

    /**
     * 文件名
     */
    @Schema(description = "文件名", example = "20250124/abc123.jpg")
    private String fileName;

    /**
     * 原始文件名
     */
    @Schema(description = "原始文件名", example = "photo.jpg")
    private String originalName;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径", example = "/upload/20250124/abc123.jpg")
    private String filePath;

    /**
     * 文件访问URL
     */
    @Schema(description = "文件访问URL", example = "https://cdn.example.com/upload/20250124/abc123.jpg")
    private String fileUrl;


    /**
     * 预览URL（预签名，可直接访问）
     */
    @Schema(description = "预览URL（预签名）", example = "https://bucket.oss.com/xxx?signature=xxx")
    private String previewUrl;

    /**
     * 文件大小
     */
    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long fileSize;

    /**
     * 文件MIME类型
     */
    @Schema(description = "文件MIME类型", example = "image/jpeg")
    private String fileType;

    /**
     * 文件扩展名
     */
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExt;

    /**
     * 存储类型
     *
     * @see StorageTypeEnum
     */
    @Schema(description = "存储类型", example = "local")
    private String storageType;

    /**
     * 文件分类
     *
     * @see FileCategoryEnum
     */
    @Schema(description = "文件分类", example = "image")
    private String category;

    /**
     * 业务类型
     */
    @Schema(description = "业务类型", example = "article")
    private String bizType;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID", example = "100")
    private Long bizId;

    /**
     * 上传者ID
     */
    @Schema(description = "上传者ID", example = "1")
    private Long uploaderId;

    /**
     * 上传者名称
     */
    @Schema(description = "上传者名称", example = "admin")
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
    @Schema(description = "是否公开访问", example = "1")
    private Integer publicAccess;

    /**
     * 下载次数
     */
    @Schema(description = "下载次数", example = "100")
    private Integer downloadCount;

    /**
     * 状态
     *
     * @see FileStatusEnum
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
