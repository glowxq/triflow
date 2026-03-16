package com.glowxq.triflow.base.file.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公开上传业务类型枚举
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Getter
@AllArgsConstructor
public enum PublicUploadBizTypeEnum implements BaseEnum {

    /** 用户头像 */
    AVATAR("avatar", "用户头像"),

    /** 注册头像 */
    REGISTER_AVATAR("register_avatar", "注册头像"),

    /** 签名图片 */
    SIGNATURE("signature", "签名图片"),

    /** Demo测试 */
    DEMO("demo", "Demo测试"),

    /** 相册图片 */
    ALBUM("album", "相册图片"),
    ;

    /** 业务类型编码 */
    private final String code;

    /** 业务类型名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static PublicUploadBizTypeEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (PublicUploadBizTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
