package com.glowxq.common.wechat.applet.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信用户手机号信息
 * <p>
 * 通过 getPhoneNumber 接口获取的用户手机号相关信息。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "微信用户手机号信息")
public class WechatPhoneInfo {

    /**
     * 用户绑定的手机号
     * <p>
     * 国外手机号会包含区号，格式如：+86 13800138000
     * </p>
     */
    @Schema(description = "手机号（含区号）", example = "+86 13800138000")
    private String phoneNumber;

    /**
     * 没有区号的手机号
     * <p>
     * 纯数字格式的手机号，不包含区号前缀。
     * </p>
     */
    @Schema(description = "纯手机号（不含区号）", example = "13800138000")
    private String purePhoneNumber;

    /**
     * 区号
     * <p>
     * 国家/地区代码，如中国为 86。
     * </p>
     */
    @Schema(description = "国家/地区区号", example = "86")
    private String countryCode;
}
