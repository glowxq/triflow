package com.glowxq.common.security.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应 VO
 * <p>
 * 用于封装用户登录成功后返回的信息，包含令牌、用户信息等。
 * </p>
 *
 * @author glowxq
 * @version 1.1
 * @since 2024/1/22
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "access_token")
    private String accessToken;

    @Schema(description = "授权令牌 access_token 的有效期")
    private Long expireIn;

    @Schema(description = "用户信息")
    private Object userInfo;
}
