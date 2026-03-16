package com.glowxq.triflow.base.ai.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AI 调用状态枚举
 * <p>
 * 用于表示 AI 调用的成功/失败状态。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Getter
@AllArgsConstructor
public enum AiCallStatusEnum implements BaseEnum {

    /** 失败 - AI 调用失败 */
    FAILED("0", "失败"),

    /** 成功 - AI 调用成功 */
    SUCCESS("1", "成功"),
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
    public static AiCallStatusEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (AiCallStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 判断是否成功
     *
     * @param value 状态值
     * @return true 表示成功
     */
    public static boolean isSuccess(Integer value) {
        return SUCCESS.getValue().equals(value);
    }

    /**
     * 判断是否失败
     *
     * @param value 状态值
     * @return true 表示失败
     */
    public static boolean isFailed(Integer value) {
        return FAILED.getValue().equals(value);
    }
}
