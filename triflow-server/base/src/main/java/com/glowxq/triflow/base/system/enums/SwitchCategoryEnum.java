package com.glowxq.triflow.base.system.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 开关分类枚举
 * <p>
 * 定义系统开关的分类，用于分组管理。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum SwitchCategoryEnum implements BaseEnum {

    /** 系统设置 - 系统级别的配置开关 */
    SYSTEM("system", "系统设置"),

    /** 用户相关 - 用户功能相关的开关 */
    USER("user", "用户相关"),

    /** 订单相关 - 订单业务相关的开关 */
    ORDER("order", "订单相关"),

    /** 支付相关 - 支付功能相关的开关 */
    PAYMENT("payment", "支付相关"),

    /** 营销相关 - 营销活动相关的开关 */
    MARKETING("marketing", "营销相关"),

    /** 通知相关 - 消息通知相关的开关 */
    NOTIFICATION("notification", "通知相关"),

    /** 安全相关 - 安全功能相关的开关 */
    SECURITY("security", "安全相关"),

    /** 界面相关 - UI/UX相关的开关 */
    UI("ui", "界面相关"),

    /** 集成相关 - 第三方集成相关的开关 */
    INTEGRATION("integration", "集成相关"),

    /** 其他 - 未分类的开关 */
    OTHER("other", "其他"),
    ;

    /** 分类编码 */
    private final String code;

    /** 分类名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static SwitchCategoryEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (SwitchCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}
