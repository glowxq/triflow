package com.glowxq.common.core.common.entity;

import com.glowxq.common.core.common.enums.DataScopeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 基础用户信息
 * <p>
 * 用于表示登录用户的基本信息，不包含敏感数据。
 * </p>
 *
 * @author glowxq
 * @version 1.1
 * @since 2023-12-12
 */
@Data
@Schema(description = "基础用户信息")
public class BaseUserInfo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "所属部门ID")
    private Long deptId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "微信小程序用户唯一标识")
    private String openid;

    @Schema(description = "微信主体唯一标识")
    private String unionid;

    @Schema(description = "唯一业务code")
    private String code;

    @Schema(description = "绑定code")
    private String bindCode;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "数据权限范围")
    private DataScopeEnum dataScope;

    @Schema(description = "租户ID")
    private String tenantId;

}
