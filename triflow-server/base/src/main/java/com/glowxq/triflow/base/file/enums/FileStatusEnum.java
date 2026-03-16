package com.glowxq.triflow.base.file.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件状态枚举
 * <p>
 * 定义文件的处理状态。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum FileStatusEnum implements BaseEnum {

    /** 待处理 - 文件刚上传，等待后续处理 */
    PENDING("0", "待处理"),

    /** 正常 - 文件处理完成，可正常使用 */
    NORMAL("1", "正常"),

    /** 处理中 - 文件正在处理（如转码、压缩等） */
    PROCESSING("2", "处理中"),

    /** 处理失败 - 文件处理失败 */
    FAILED("3", "处理失败"),
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
    public static FileStatusEnum of(Integer value) {
        if (value == null) {
            return null;
        }
        String code = String.valueOf(value);
        for (FileStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
