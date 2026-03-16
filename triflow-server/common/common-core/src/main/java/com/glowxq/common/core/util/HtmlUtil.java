package com.glowxq.common.core.util;

/**
 * HTML 工具类
 * <p>
 * 提供 HTML 标签清理、转义和反转义等功能，用于 XSS 防护。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
public class HtmlUtil {

    public static final String NBSP = "&nbsp;";

    public static final String AMP = "&amp;";

    public static final String QUOTE = "&quot;";

    public static final String APOS = "&apos;";

    public static final String LT = "&lt;";

    public static final String GT = "&gt;";

    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";

    public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

    private static final char[][] TEXT = new char[256][];

    static {
        for (int i = 0; i < 256; ++i) {
            TEXT[i] = new char[]{(char) i};
        }

        TEXT[39] = "&#039;".toCharArray();
        TEXT[34] = "&quot;".toCharArray();
        TEXT[38] = "&amp;".toCharArray();
        TEXT[60] = "&lt;".toCharArray();
        TEXT[62] = "&gt;".toCharArray();
        TEXT[160] = "&nbsp;".toCharArray();
    }

    private HtmlUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * HTML 转义
     */
    public static String escape(String text) {
        return encode(text);
    }

    /**
     * HTML 反转义
     */
    public static String unescape(String htmlStr) {
        if (htmlStr == null || htmlStr.isBlank()) {
            return htmlStr;
        }
        return htmlStr
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&apos;", "'")
                .replace("&nbsp;", "\u00A0");
    }

    /**
     * 清除所有 HTML 标签
     */
    public static String cleanHtmlTag(String content) {
        if (content == null) {
            return null;
        }
        return content.replaceAll(RE_HTML_MARK, "");
    }

    /**
     * 移除指定 HTML 标签（包含标签内容）
     */
    public static String removeHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, true, tagNames);
    }

    /**
     * 移除指定 HTML 标签（保留标签内容）
     */
    public static String unwrapHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, false, tagNames);
    }

    /**
     * 移除指定 HTML 标签
     *
     * @param content        内容
     * @param withTagContent 是否同时移除标签内容
     * @param tagNames       标签名称列表
     * @return 处理后的内容
     */
    public static String removeHtmlTag(String content, boolean withTagContent, String... tagNames) {
        if (content == null || tagNames == null) {
            return content;
        }
        for (String tagName : tagNames) {
            if (tagName != null && !tagName.isBlank()) {
                tagName = tagName.trim();
                String regex;
                if (withTagContent) {
                    regex = "(?i)<" + tagName + "(\\s+[^>]*?)?/?>(.*?</" + tagName + ">)?";
                } else {
                    regex = "(?i)<" + tagName + "(\\s+[^>]*?)?/?>|</?+" + tagName + ">";
                }
                content = content.replaceAll(regex, "");
            }
        }
        return content;
    }

    /**
     * 移除指定 HTML 属性
     */
    public static String removeHtmlAttr(String content, String... attrs) {
        if (content == null || attrs == null) {
            return content;
        }
        for (String attr : attrs) {
            String regex = "(?i)(\\s*" + attr + "\\s*=\\s*)(([\"][^\"]+?[\"]\\s*)|([^>]+?\\s+(?=>))|([^>]+?(?=\\s|>)))";
            content = content.replaceAll(regex, "");
        }
        return content;
    }

    /**
     * 移除指定标签的所有属性
     */
    public static String removeAllHtmlAttr(String content, String... tagNames) {
        if (content == null || tagNames == null) {
            return content;
        }
        for (String tagName : tagNames) {
            String regex = "(?i)<" + tagName + "[^>]*?>";
            content = content.replaceAll(regex, "<" + tagName + ">");
        }
        return content;
    }

    private static String encode(String text) {
        int len;
        if (text != null && (len = text.length()) != 0) {
            StringBuilder buffer = new StringBuilder(len + (len >> 2));

            for (int i = 0; i < len; ++i) {
                char c = text.charAt(i);
                if (c < 256) {
                    buffer.append(TEXT[c]);
                } else {
                    buffer.append(c);
                }
            }

            return buffer.toString();
        } else {
            return "";
        }
    }
}
