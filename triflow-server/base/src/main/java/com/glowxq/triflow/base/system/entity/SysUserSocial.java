package com.glowxq.triflow.base.system.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户第三方账号关联实体类
 * <p>
 * 对应数据库表 sys_user_social，支持微信、QQ、GitHub、Apple、Google等第三方登录。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Table("sys_user_social")
@Schema(description = "用户第三方账号")
public class SysUserSocial implements Serializable {

    /** 主键ID（自增） */
    @Id(keyType = KeyType.Auto)
    @Schema(description = "主键ID")
    private Long id;

    /** 用户ID */
    @Schema(description = "用户ID")
    private Long userId;

    /** 第三方平台类型（wechat_mp, wechat_miniapp, wechat_open, qq, github, apple, google, dingtalk, weibo） */
    @Schema(description = "第三方平台类型")
    private String socialType;

    /** 第三方平台用户ID（openid） */
    @Schema(description = "第三方用户ID")
    private String socialId;

    /** 第三方平台统一ID（微信unionid） */
    @Schema(description = "统一ID")
    private String unionId;

    /** 访问令牌（加密存储） */
    @Schema(description = "访问令牌")
    private String accessToken;

    /** 刷新令牌（加密存储） */
    @Schema(description = "刷新令牌")
    private String refreshToken;

    /** 令牌过期时间 */
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    /** 第三方平台昵称 */
    @Schema(description = "第三方昵称")
    private String nickname;

    /** 第三方平台头像 */
    @Schema(description = "第三方头像")
    private String avatar;

    /** 原始用户数据（JSON格式） */
    @Schema(description = "原始数据")
    private String rawData;

    /** 绑定时间 */
    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;

    /** 租户ID */
    @Schema(description = "租户ID")
    private String tenantId;

}
