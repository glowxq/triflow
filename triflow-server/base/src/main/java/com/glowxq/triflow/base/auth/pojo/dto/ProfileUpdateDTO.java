package com.glowxq.triflow.base.auth.pojo.dto;

import com.glowxq.triflow.base.system.enums.GenderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新个人资料 DTO
 * <p>
 * 用户可自行修改的个人信息字段。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "更新个人资料请求")
public class ProfileUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 昵称 */
    @Schema(description = "昵称", example = "小明")
    private String nickname;

    /** 真实姓名 */
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /** 头像 URL */
    @Schema(description = "头像 URL")
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
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "test@example.com")
    private String email;

    /** 个人简介 */
    @Schema(description = "个人简介", example = "这是我的个人简介")
    private String introduction;

}
