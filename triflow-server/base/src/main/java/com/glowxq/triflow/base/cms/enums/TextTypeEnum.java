package com.glowxq.triflow.base.cms.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文本类型枚举
 * <p>
 * 定义文本内容的类型。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum TextTypeEnum implements BaseEnum {

    /** 文章 - 完整的文章内容 */
    ARTICLE("article", "文章"),

    /** 公告 - 系统公告内容 */
    NOTICE("notice", "公告"),

    /** 帮助 - 帮助文档内容 */
    HELP("help", "帮助"),

    /** 协议 - 用户协议、隐私政策等 */
    AGREEMENT("agreement", "协议"),

    /** 关于 - 关于我们等说明 */
    ABOUT("about", "关于"),

    /** 其他 - 其他类型内容 */
    OTHER("other", "其他"),

    /** FAQ - 常见问题解答 */
    FAQ("faq", "FAQ"),
    ;

    /** 类型编码 */
    private final String code;

    /** 类型名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static TextTypeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (TextTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
