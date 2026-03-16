package com.glowxq.triflow.base.file.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.file.entity.FileConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文件存储配置更新请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文件存储配置更新请求")
@AutoMapper(target = FileConfig.class)
public class FileConfigUpdateDTO implements BaseDTO {

    /** 配置ID */
    @Schema(description = "配置ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置ID不能为空")
    private Long id;

    /** 配置名称 */
    @Schema(description = "配置名称", example = "本地存储")
    @Size(max = 100, message = "配置名称长度不能超过100字符")
    private String configName;

    /** 配置标识 */
    @Schema(description = "配置标识", example = "local")
    @Size(max = 50, message = "配置标识长度不能超过50字符")
    private String configKey;

    /** 存储类型 */
    @Schema(description = "存储类型", example = "local")
    private String storageType;

    /** 服务端点 */
    @Schema(description = "服务端点")
    private String endpoint;

    /** AccessKey */
    @Schema(description = "AccessKey")
    private String accessKey;

    /** SecretKey */
    @Schema(description = "SecretKey")
    private String secretKey;

    /** 存储桶名称 */
    @Schema(description = "存储桶名称")
    private String bucketName;

    /** 存储区域 */
    @Schema(description = "存储区域")
    private String region;

    /** 自定义域名 */
    @Schema(description = "自定义域名")
    private String domain;

    /** 基础路径 */
    @Schema(description = "基础路径")
    private String basePath;

    /** 是否使用HTTPS */
    @Schema(description = "是否使用HTTPS", example = "1")
    private Integer useHttps;

    /** 是否默认配置 */
    @Schema(description = "是否默认配置", example = "0")
    private Integer defaulted;

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注说明")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, FileConfig.class);
    }

}
