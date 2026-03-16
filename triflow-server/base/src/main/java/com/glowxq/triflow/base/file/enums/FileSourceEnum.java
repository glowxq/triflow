package com.glowxq.triflow.base.file.enums;

import com.glowxq.common.core.common.enums.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件来源枚举
 * <p>
 * 定义文件的来源类型，用于区分不同上传场景的文件。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Getter
@AllArgsConstructor
public enum FileSourceEnum implements BaseEnum {

    /** 后台上传 - 管理后台上传的文件 */
    ADMIN("admin", "后台上传"),

    /** 前台上传 - 用户端上传的文件 */
    FRONT("front", "前台上传"),

    /** 小程序上传 - 小程序端上传的文件 */
    MINIAPP("miniapp", "小程序上传"),

    /** API上传 - 通过API接口上传的文件 */
    API("api", "API上传"),

    /** 系统生成 - 系统自动生成的文件 */
    SYSTEM("system", "系统生成"),
    ;

    /** 来源编码 */
    private final String code;

    /** 来源名称 */
    private final String name;

    /**
     * 根据编码获取枚举
     *
     * @param code 编码
     * @return 对应的枚举，未匹配则返回 null
     */
    public static FileSourceEnum of(String code) {
        if (code == null) {
            return null;
        }
        for (FileSourceEnum source : values()) {
            if (source.getCode().equals(code)) {
                return source;
            }
        }
        return null;
    }
}
