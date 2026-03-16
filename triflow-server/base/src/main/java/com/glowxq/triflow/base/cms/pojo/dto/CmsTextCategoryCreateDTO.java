package com.glowxq.triflow.base.cms.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 文本分类创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本分类创建请求")
@AutoMapper(target = CmsTextCategory.class)
public class CmsTextCategoryCreateDTO implements BaseDTO {

    @Schema(description = "父分类ID", defaultValue = "0")
    private Long pid;

    @Schema(description = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 50, message = "分类名称长度不能超过50字符")
    private String categoryName;

    @Schema(description = "分类标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类标识不能为空")
    @Size(max = 50, message = "分类标识长度不能超过50字符")
    private String categoryKey;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "显示排序", defaultValue = "0")
    private Integer sort;

    @Schema(description = "状态", defaultValue = "1")
    private Integer status;

    @Schema(description = "备注说明")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, CmsTextCategory.class);
    }

}
