package com.glowxq.triflow.base.cms.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.cms.entity.CmsTextCategory;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文本分类响应 VO
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Schema(description = "文本分类响应")
@AutoMapper(target = CmsTextCategory.class)
public class CmsTextCategoryVO implements BaseVO {

    @Schema(description = "分类ID")
    private Long id;

    @Schema(description = "父分类ID")
    private Long pid;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "分类标识")
    private String categoryKey;

    @Schema(description = "分类图标")
    private String icon;

    @Schema(description = "显示排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注说明")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "子分类列表")
    private List<CmsTextCategoryVO> children;

}
