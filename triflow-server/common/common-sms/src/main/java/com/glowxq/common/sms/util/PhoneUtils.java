package com.glowxq.common.sms.util;

import java.util.regex.Pattern;

/**
 * 手机号工具类
 * <p>
 * 提供手机号相关的验证和脱敏功能。
 * </p>
 *
 * @author glowxq
 * @since 2025/01/21
 */
public final class PhoneUtils {

    /**
     * 中国大陆手机号正则
     */
    private static final Pattern CHINA_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 国际手机号正则（含国际区号）
     */
    private static final Pattern INTERNATIONAL_PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    private PhoneUtils() {
        // 工具类禁止实例化
    }

    /**
     * 脱敏手机号
     * <p>
     * 将手机号中间部分替换为 *，保留前3后4位。
     * </p>
     *
     * <pre>{@code
     * PhoneUtils.mask("13800138000") // "138****8000"
     * PhoneUtils.mask("+8613800138000") // "+86138****8000"
     * PhoneUtils.mask("123") // "***"
     * }</pre>
     *
     * @param phone 手机号
     *
     * @return 脱敏后的手机号
     */
    public static String mask(String phone) {
        if (phone == null || phone.isEmpty()) {
            return "***";
        }

        // 处理国际区号前缀
        String prefix = "";
        String numberPart = phone;
        if (phone.startsWith("+")) {
            int spaceIndex = phone.indexOf(' ');
            if (spaceIndex > 0) {
                prefix = phone.substring(0, spaceIndex + 1);
                numberPart = phone.substring(spaceIndex + 1);
            } else {
                // 假设 +86 这样的格式
                int numStart = 1;
                while (numStart < phone.length() && !Character.isDigit(phone.charAt(numStart))) {
                    numStart++;
                }
                // 找到数字开始位置后，假设国际区号最多4位
                int codeEnd = Math.min(numStart + 4, phone.length());
                for (int i = numStart; i < codeEnd; i++) {
                    if (phone.length() - i >= 11) {
                        prefix = phone.substring(0, i);
                        numberPart = phone.substring(i);
                        break;
                    }
                }
            }
        }

        // 对号码部分进行脱敏
        int length = numberPart.length();
        if (length <= 4) {
            return prefix + "****";
        } else if (length <= 7) {
            return prefix + numberPart.substring(0, 2) + "****" + numberPart.substring(length - 1);
        } else {
            // 标准11位手机号：保留前3后4
            return prefix + numberPart.substring(0, 3) + "****" + numberPart.substring(length - 4);
        }
    }

    /**
     * 验证是否为有效的中国大陆手机号
     *
     * @param phone 手机号
     *
     * @return 是否有效
     */
    public static boolean isValidChinaPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return CHINA_PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * 验证是否为有效的国际手机号
     *
     * @param phone 手机号（可包含 + 前缀）
     *
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // 移除空格和横杠
        String cleaned = phone.replaceAll("[\\s-]", "");
        return INTERNATIONAL_PHONE_PATTERN.matcher(cleaned).matches();
    }

    /**
     * 标准化手机号
     * <p>
     * 移除空格、横杠等分隔符。
     * </p>
     *
     * @param phone 原始手机号
     *
     * @return 标准化后的手机号
     */
    public static String normalize(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[\\s-]", "");
    }

}
