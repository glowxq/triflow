package com.glowxq.common.security.pojo;

import com.glowxq.common.core.common.valid.annotation.InEnum;
import com.glowxq.common.security.enums.GrantType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录信息
 *
 * @author glowxq
 * @version 1.0
 * @since 2024/1/22 9:38
 */
@Data
public class LoginDTO {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "客户端id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String clientId;

    @Schema(description = "授权类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @InEnum(enumClass = GrantType.class)
    @NotBlank
    private GrantType grantType;

    @Schema(description = "openId")
    private String openId;

    @Schema(description = "微信小程序手机号快速认证组件 https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html")
    private String phoneNumber;

    @Schema(description = "租户key，租户id 或 租户code都可以")
    private String tenantKey;

}
