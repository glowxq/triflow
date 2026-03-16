package com.glowxq.triflow.base.file.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.triflow.base.file.entity.FileInfo;
import com.glowxq.triflow.base.file.enums.FileCategoryEnum;
import com.glowxq.triflow.base.file.enums.FileStatusEnum;
import com.glowxq.triflow.base.file.enums.StorageTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件信息导出 VO
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "文件信息导出")
@AutoMapper(target = FileInfo.class)
public class FileInfoExcelVO {

    /** 原始文件名 */
    @ExcelProperty("原始文件名")
    @ColumnWidth(30)
    @Schema(description = "原始文件名", example = "photo.jpg")
    private String originalName;

    /** 文件大小（字节） */
    @ExcelProperty("文件大小(字节)")
    @ColumnWidth(15)
    @Schema(description = "文件大小(字节)", example = "1024000")
    private Long fileSize;

    /** 文件MIME类型 */
    @ExcelProperty("文件类型")
    @ColumnWidth(20)
    @Schema(description = "文件MIME类型", example = "image/jpeg")
    private String fileType;

    /** 文件扩展名 */
    @ExcelProperty("扩展名")
    @ColumnWidth(10)
    @Schema(description = "文件扩展名", example = "jpg")
    private String fileExt;

    /**
     * 文件分类
     *
     * @see FileCategoryEnum
     */
    @ExcelProperty("分类")
    @ColumnWidth(12)
    @Schema(description = "文件分类", example = "image")
    private String category;

    /**
     * 存储类型
     *
     * @see StorageTypeEnum
     */
    @ExcelProperty("存储类型")
    @ColumnWidth(12)
    @Schema(description = "存储类型", example = "oss")
    private String storageType;

    /** 上传者名称 */
    @ExcelProperty("上传者")
    @ColumnWidth(15)
    @Schema(description = "上传者名称", example = "admin")
    private String uploaderName;

    /** 上传时间 */
    @ExcelProperty("上传时间")
    @ColumnWidth(20)
    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

    /** 文件访问URL */
    @ExcelProperty("文件URL")
    @ColumnWidth(50)
    @Schema(description = "文件访问URL")
    private String fileUrl;

    /**
     * 状态
     *
     * @see FileStatusEnum
     */
    @ExcelProperty("状态")
    @ColumnWidth(10)
    @Schema(description = "状态", example = "1")
    private Integer status;

}
