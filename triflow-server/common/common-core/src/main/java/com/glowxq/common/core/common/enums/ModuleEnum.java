package com.glowxq.common.core.common.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author weiweicode
 * @version 1.0
 * @since 2025/6/11 0:24
 */
@Getter
@AllArgsConstructor
public enum ModuleEnum implements BaseEnum {
    System("System", "系统管理"),
    Tenant("Tenant", "租户管理"),
    Auth("Auth", "认证授权"),
    CMS("CMS", "内容管理"),
    File("File", "文件管理"),
    Notify("Notify", "消息通知"),
    Wechat("Wechat", "微信服务"),
    AI("AI", "AI 服务"),
    Wallet("Wallet", "钱包服务"),
    Client("Client", "客户端公共"),
    Article("Article", "文章管理"),
    Log("Log", "日志管理"),
    ;

    /**
     * CODE
     */
    private final String code;

    /**
     * 模块名称
     */
    private final String name;

    /**
     * 根据code解析对应的枚举值
     *
     * @param code 枚举的code值
     *
     * @return 对应的枚举值，如果未找到则返回null
     */
    public static ModuleEnum parse(String code) {
        if (code == null) {
            return null;
        }
        for (ModuleEnum value : values()) {
            if (value.code != null && value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

}
