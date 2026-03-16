package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.triflow.base.system.entity.SysDept;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 部门树形响应 VO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "部门树形响应")
@AutoMapper(target = SysDept.class)
public class SysDeptTreeVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @Schema(description = "部门ID", example = "1")
    private Long id;

    /** 父部门ID */
    @Schema(description = "父部门ID", example = "0")
    private Long parentId;

    /** 部门名称 */
    @Schema(description = "部门名称", example = "研发部")
    private String deptName;

    /** 排序 */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /** 负责人 */
    @Schema(description = "负责人", example = "张三")
    private String leader;

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 子部门 */
    @Schema(description = "子部门")
    private List<SysDeptTreeVO> children;

}
