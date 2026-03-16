package com.glowxq.triflow.base.cms.entity;

import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文本分类实体类
 * <p>
 * 对应数据库表 cms_text_category，用于管理文本内容的分类。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Data
@Table("cms_text_category")
@Schema(description = "文本分类")
public class CmsTextCategory implements BaseEntity {

    /**
     * 分类ID（主键，自增）
     */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "分类ID")
    private Long id;

    /**
     * 父分类ID（顶级分类为0）
     */
    @Schema(description = "父分类ID")
    private Long pid;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String categoryName;

    /**
     * 分类标识（唯一键）
     */
    @Schema(description = "分类标识")
    private String categoryKey;

    /**
     * 分类图标
     */
    @Schema(description = "分类图标")
    private String icon;

    /**
     * 显示排序
     */
    @Schema(description = "显示排序")
    private Integer sort;

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

    /**
     * 租户ID
     */
    @Schema(description = "租户ID")
    private String tenantId;

    /**
     * 数据权限部门ID
     */
    @Schema(description = "数据权限部门ID")
    private Long deptId;

    /**
     * 创建者ID
     */
    @Schema(description = "创建者ID")
    private Long createBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新者ID
     */
    @Schema(description = "更新者ID")
    private Long updateBy;

    /**
     * 更新时间
     */
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
