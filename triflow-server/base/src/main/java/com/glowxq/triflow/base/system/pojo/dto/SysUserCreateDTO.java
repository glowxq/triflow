package com.glowxq.triflow.base.system.pojo.dto;

import com.glowxq.common.core.common.entity.base.BaseDTO;
import com.glowxq.common.core.common.entity.base.BaseEntity;
import com.glowxq.common.core.common.enums.BooleanEnum;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.triflow.base.system.entity.SysUser;
import com.glowxq.triflow.base.system.enums.GenderEnum;
import com.glowxq.triflow.base.system.enums.UserStatusEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户创建请求 DTO
 * <p>
 * 用于创建新用户时的参数传递。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Schema(description = "用户创建请求")
@AutoMapper(target = SysUser.class)
public class SysUserCreateDTO implements BaseDTO {

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50字符")
    private String username;

    /** 密码（明文，服务端加密） */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20字符之间")
    private String password;

    /**
     * 是否设置密码
     *
     * @see BooleanEnum
     */
    @Schema(description = "是否设置密码", example = "1")
    private Integer passwordSet;

    /** 用户昵称 */
    @Schema(description = "昵称", example = "管理员")
    @Size(max = 50, message = "昵称长度不能超过50字符")
    private String nickname;

    /** 真实姓名 */
    @Schema(description = "真实姓名", example = "张三")
    @Size(max = 50, message = "真实姓名长度不能超过50字符")
    private String realName;

    /** 头像URL */
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    /**
     * 性别
     *
     * @see GenderEnum
     */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    @Size(max = 20, message = "手机号长度不能超过20字符")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@example.com")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    private String email;

    /** 所属部门ID */
    @Schema(description = "所属部门ID", example = "1")
    private Long deptId;

    /**
     * 数据权限范围
     *
     * @see com.glowxq.common.core.common.enums.DataScopeEnum
     */
    @Schema(description = "数据权限范围", example = "UserCreate")
    private String dataScope;

    /**
     * 状态
     *
     * @see UserStatusEnum
     */
    @Schema(description = "状态", example = "1", defaultValue = "1")
    private Integer status;

    /** 角色ID列表 */
    @Schema(description = "角色ID列表", example = "[1, 2]")
    private List<Long> roleIds;

    /** 备注 */
    @Schema(description = "备注")
    @Size(max = 500, message = "备注长度不能超过500字符")
    private String remark;

    @Override
    public BaseEntity buildEntity() {
        return MapStructUtils.convert(this, SysUser.class);
    }

}
