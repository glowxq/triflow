package com.glowxq.common.security.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum GrantType implements BaseEnum {

    test("test", "Test模式"),
    password("password", "密码模式"),
    sms("sms", "短信模式"),
    wechat_miniapp("wechat_miniapp", "微信小程序"),
    third("third", "第三方模式"),
    feishu("feishu", "飞书登录"),
    google("google", "Google登录"),
    ;

    private final String code;

    private final String name;

    public static GrantType matchCode(String grantType) {
        for (GrantType type : GrantType.values()) {
            if (type.getCode().equals(grantType)) {
                return type;
            }
        }
        return null;
    }
}
