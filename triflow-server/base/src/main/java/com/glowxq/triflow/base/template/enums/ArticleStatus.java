package com.glowxq.triflow.base.template.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文章状态枚举
 * <p>
 * 定义文章的生命周期状态，用于控制文章的可见性和发布流程。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus implements BaseEnum {

    /** 草稿 - 文章尚未发布，仅作者可见 */
    DRAFT("0", "草稿"),

    /** 已发布 - 文章已公开发布，所有用户可见 */
    PUBLISHED("1", "已发布"),

    /** 已下架 - 文章已从公开列表移除，仅作者可见 */
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
    public static ArticleStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static ArticleStatus matchCode(String code) {
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
