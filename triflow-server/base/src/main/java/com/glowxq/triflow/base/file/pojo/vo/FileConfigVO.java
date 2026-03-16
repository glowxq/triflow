package com.glowxq.triflow.base.file.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.file.entity.FileConfig;
import com.glowxq.triflow.base.file.enums.StorageTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件存储配置响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文件存储配置响应")
@AutoMapper(target = FileConfig.class)
public class FileConfigVO implements BaseVO {

    /**
     * 配置ID
     */
    @Schema(description = "配置ID", example = "1")
    private Long id;

    /**
     * 配置名称
     */
    @Schema(description = "配置名称", example = "本地存储")
    private String configName;

    /**
     * 配置标识
     */
    @Schema(description = "配置标识", example = "local")
    private String configKey;

    /**
     * 存储类型
     *
     * @see StorageTypeEnum
     */
    @Schema(description = "存储类型", example = "local")
    private String storageType;

    /**
     * 服务端点
     */
    @Schema(description = "服务端点", example = "oss-cn-hangzhou.aliyuncs.com")
    private String endpoint;

    /**
     * 存储桶名称
     */
    @Schema(description = "存储桶名称", example = "triflow")
    private String bucketName;

    /**
     * 存储区域
     */
    @Schema(description = "存储区域", example = "cn-hangzhou")
    private String region;

    /**
     * 自定义域名
     */
    @Schema(description = "自定义域名", example = "https://cdn.example.com")
    private String domain;

    /**
     * 基础路径
     */
    @Schema(description = "基础路径", example = "/upload")
    private String basePath;

    /**
     * 是否使用HTTPS
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否使用HTTPS", example = "1")
    private Integer useHttps;

    /**
     * 是否默认配置
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否默认配置", example = "1")
    private Integer defaulted;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /**
     * 备注
     */
    @Schema(description = "备注说明", example = "本地文件存储配置")
    private String remark;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
