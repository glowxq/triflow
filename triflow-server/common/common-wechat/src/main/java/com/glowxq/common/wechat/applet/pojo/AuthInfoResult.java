package com.glowxq.common.wechat.applet.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信小程序登录认证信息
 * <p>
 * 包含用户的 openid、session_key 以及 unionid（如果已关联开放平台）。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信小程序登录认证信息")
public class AuthInfoResult {

    /**
     * 用户唯一标识（小程序内唯一）
     */
    @Schema(description = "用户 OpenID", example = "oXXXX-XXXX")
    private String openid;

    /**
     * 会话密钥
     * <p>
     * 用于解密用户数据，有效期较短，请妥善保管。
     * </p>
     */
    @Schema(description = "会话密钥")
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符
     * <p>
     * 仅当小程序已绑定到开放平台账号时返回。
     * </p>
     */
    @Schema(description = "UnionID（开放平台唯一标识）")
    private String unionId;
}
