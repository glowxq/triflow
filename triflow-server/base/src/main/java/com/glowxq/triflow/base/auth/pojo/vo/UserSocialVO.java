package com.glowxq.triflow.base.auth.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户第三方绑定信息 VO
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Data
@Schema(description = "用户第三方绑定信息")
public class UserSocialVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** ID */
    @Schema(description = "ID")
    private Long id;

    /** 第三方平台类型 */
    @Schema(description = "第三方平台类型", example = "feishu")
    private String socialType;

    /** 第三方平台昵称 */
    @Schema(description = "第三方昵称")
    private String nickname;

    /** 第三方平台头像 */
    @Schema(description = "第三方头像")
    private String avatar;

    /** 绑定时间 */
    @Schema(description = "绑定时间")
    private LocalDateTime bindTime;

}
