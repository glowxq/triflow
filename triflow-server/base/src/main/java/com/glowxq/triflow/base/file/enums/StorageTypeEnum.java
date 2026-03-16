package com.glowxq.triflow.base.file.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储类型枚举
 * <p>
 * 定义文件存储的类型。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum StorageTypeEnum implements BaseEnum {

    /** 本地存储 */
    LOCAL("local", "本地存储"),

    /** 阿里云OSS */
    ALIYUN("aliyun", "阿里云OSS"),

    /** 腾讯云COS */
    TENCENT("tencent", "腾讯云COS"),

    /** MinIO */
    MINIO("minio", "MinIO"),

    /** 七牛云 */
    QINIU("qiniu", "七牛云"),
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
    public static StorageTypeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (StorageTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
