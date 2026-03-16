package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysRole;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 角色创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "角色创建请求")
@AutoMapper(target = SysRole.class)
public class SysRoleCreateDTO implements BaseDTO {

    /** 角色编码 */
    @Schema(description = "角色编码", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50字符")
    private String roleKey;

    /** 角色名称 */
    @Schema(description = "角色名称", example = "管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50字符")
    private String roleName;

    /** 显示排序 */
    @Schema(description = "显示排序", example = "1", defaultValue = "0")
    private Integer sort;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @Schema(description = "状态", example = "1", defaultValue = "1")
    private Integer status;

    /** 菜单ID列表 */
    @Schema(description = "菜单ID列表", example = "[1, 2, 3]")
    private List<Long> menuIds;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysRole.class);
    }

}
