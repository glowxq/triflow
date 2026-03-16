package com.glowxq.triflow.base.cms.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本状态枚举
 * <p>
 * 定义文本内容的发布状态。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum TextStatusEnum implements BaseEnum {

    /** 草稿 - 文本尚未发布，仅作者可见 */
    DRAFT("0", "草稿"),

    /** 已发布 - 文本已公开发布，所有用户可见 */
    PUBLISHED("1", "已发布"),

    /** 已下架 - 文本已从公开列表移除 */
    OFFLINE("2", "已下架"),
    ;

    /** 状态编码 */
    private final String code;

    /** 状态名称 */
    private final String name;

    /**
     * 获取状态值（数据库存储值）
     *
     * @return 状态值
     */
    public Integer getValue() {
        return Integer.valueOf(code);
    }

    /**
     * 根据状态值获取枚举
     *
     * @param value 状态值
     * @return 对应的枚举，未匹配则返回 null
     */
    public static TextStatusEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (TextStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否已发布
     *
     * @param value 状态值
     * @return true 表示已发布
     */
    public static boolean isPublished(Integer value) {
        return PUBLISHED.getValue().equals(value);
    }
}
