package com.glowxq.triflow.base.file.pojo.dto;

import com.glowxq.common.core.common.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件存储配置查询请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文件存储配置查询请求")
public class FileConfigQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 搜索关键词（配置名称模糊匹配） */
    @Schema(description = "搜索关键词", example = "本地")
    private String keyword;

    /** 存储类型筛选 */
    @Schema(description = "存储类型", example = "local")
    private String storageType;

    /**
     * 状态筛选
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    // ========== 分页参数 ==========

    /** 页码 */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    /** 每页数量 */
    @Schema(description = "每页数量", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

}
