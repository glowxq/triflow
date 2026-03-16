package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 第三方社交平台类型枚举
 * <p>
 * 用于统一管理第三方登录和绑定的平台类型，避免魔法值
 * </p>
 *
 * @author glowxq
 * @since 2025-01-26
 */
@Getter
@AllArgsConstructor
public enum SocialType implements BaseEnum {

    /** 微信公众号 */
    WECHAT_MP("wechat_mp", "微信公众号"),

    /** 微信小程序 */
    WECHAT_MINIAPP("wechat_miniapp", "微信小程序"),

    /** 微信开放平台 */
    WECHAT_OPEN("wechat_open", "微信开放平台"),

    /** QQ */
    QQ("qq", "QQ"),

    /** GitHub */
    GITHUB("github", "GitHub"),

    /** Apple */
    APPLE("apple", "Apple"),

    /** Google */
    GOOGLE("google", "Google"),

    /** 钉钉 */
    DINGTALK("dingtalk", "钉钉"),

    /** 微博 */
    WEIBO("weibo", "微博"),

    /** 飞书 */
    FEISHU("feishu", "飞书"),
    ;

    /** 平台代码（存入数据库的值） */
    private final String code;

    /** 平台名称 */
    private final String name;

    /**
     * 根据代码匹配枚举
     *
     * @param code 平台代码
     * @return 匹配的枚举，未找到返回 null
     */
    public static SocialType matchCode(String code) {
        if (code == null) {
            return null;
        }
        for (SocialType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为微信平台（公众号、小程序、开放平台）
     *
     * @return 是否为微信平台
     */
    public boolean isWechat() {
        return this == WECHAT_MP || this == WECHAT_MINIAPP || this == WECHAT_OPEN;
    }
}
