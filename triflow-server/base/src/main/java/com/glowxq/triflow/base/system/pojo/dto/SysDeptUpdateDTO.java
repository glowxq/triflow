package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysDept;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 部门更新请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "部门更新请求")
@AutoMapper(target = SysDept.class)
public class SysDeptUpdateDTO implements BaseDTO {

    /** 部门ID */
    @Schema(description = "部门ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门ID不能为空")
    private Long id;

    /** 父部门ID */
    @Schema(description = "父部门ID", example = "0")
    private Long parentId;

    /** 部门名称 */
    @Schema(description = "部门名称", example = "研发部")
    @Size(max = 50, message = "部门名称长度不能超过50字符")
    private String deptName;

    /** 显示排序 */
    @Schema(description = "显示排序", example = "1")
    private Integer sort;

    /** 负责人 */
    @Schema(description = "负责人", example = "张三")
    @Size(max = 50, message = "负责人长度不能超过50字符")
    private String leader;

    /** 联系电话 */
    @Schema(description = "联系电话", example = "13800138000")
    @Size(max = 20, message = "联系电话长度不能超过20字符")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "rd@example.com")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysDept.class);
    }

}
