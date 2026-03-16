package com.glowxq.triflow.base.file.pojo.dto;

import com.glowxq.common.core.common.entity.PageQuery;
import com.glowxq.triflow.base.file.enums.FileCategoryEnum;
import com.glowxq.triflow.base.file.enums.FileStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件信息查询请求")
public class FileInfoQueryDTO extends PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（原始文件名模糊匹配） */
    @Schema(description = "搜索关键词", example = "报告")
    private String keyword;

    /**
     * 文件分类筛选
     *
     * @see FileCategoryEnum
     */
    @Schema(description = "文件分类（avatar:头像, document:文档, image:图片, video:视频, audio:音频, other:其他）", example = "image")
    private String category;

    /** 文件扩展名筛选 */
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExt;

    /** 业务类型筛选 */
    @Schema(description = "业务类型", example = "article")
    private String bizType;

    /** 业务ID筛选 */
    @Schema(description = "业务ID", example = "1")
    private Long bizId;

    /** 上传者ID筛选 */
    @Schema(description = "上传者ID", example = "1")
    private Long uploaderId;

    /**
     * 状态筛选
     *
     * @see FileStatusEnum
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1", "2", "3"})
    private Integer status;

    /** 上传时间-开始 */
    @Schema(description = "上传时间-开始")
    private LocalDateTime startTime;

    /** 上传时间-结束 */
    @Schema(description = "上传时间-结束")
    private LocalDateTime endTime;

}
