package com.glowxq.triflow.base.file.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件分类枚举
 * <p>
 * 定义文件的分类类型。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum FileCategoryEnum implements BaseEnum {

    /** 头像 */
    AVATAR("avatar", "头像"),

    /** 文档 */
    DOCUMENT("document", "文档"),

    /** 图片 */
    IMAGE("image", "图片"),

    /** 视频 */
    VIDEO("video", "视频"),

    /** 音频 */
    AUDIO("audio", "音频"),

    /** 其他 */
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
    public static FileCategoryEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (FileCategoryEnum category : values()) {
            if (category.getCode().equals(code)) {
                return category;
            }
        }
        return null;
    }
}
