package com.glowxq.triflow.base.system.pojo.vo;

import com.glowxq.common.core.common.entity.base.BaseVO;
import com.glowxq.triflow.base.system.entity.SysRole;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色响应 VO
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "角色响应")
@AutoMapper(target = SysRole.class)
public class SysRoleVO implements BaseVO {

    /** 角色ID */
    @Schema(description = "角色ID", example = "1")
    private Long id;

    /** 角色编码 */
    @Schema(description = "角色编码", example = "admin")
    private String roleKey;

    /** 角色名称 */
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    /** 排序 */
    @Schema(description = "排序", example = "1")
    private Integer sort;

    /** 状态 */
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新时间 */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
