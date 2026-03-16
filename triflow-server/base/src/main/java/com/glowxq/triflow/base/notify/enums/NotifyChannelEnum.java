package com.glowxq.triflow.base.notify.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知渠道枚举
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum NotifyChannelEnum implements BaseEnum {

    /** 微信小程序订阅消息 */
    WECHAT_MINIAPP("wechat_miniapp", "微信小程序"),

    /** 短信通知 */
    SMS("sms", "短信"),

    /** 邮件通知 */
    EMAIL("email", "邮件"),
    ;

    private final String code;
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 枚举
     */
    public static NotifyChannelEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (NotifyChannelEnum channel : values()) {
            if (channel.getCode().equals(code)) {
                return channel;
            }
        }
        return null;
    }
}
