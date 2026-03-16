package com.glowxq.triflow.base.system.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 开关枚举对比结果 VO
 * <p>
 * 用于对比 SwitchKeyEnum 枚举定义与数据库中实际开关的差异。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-27
 */
@Data
@Schema(description = "开关枚举对比结果")
public class SwitchEnumCompareVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 枚举中定义但数据库不存在的开关（建议添加到数据库） */
    @Schema(description = "枚举中有但数据库没有的开关")
    private List<EnumSwitchItem> missingInDatabase;

    /** 数据库中存在但枚举未定义的开关（建议添加到枚举或删除） */
    @Schema(description = "数据库中有但枚举没有的开关")
    private List<String> missingInEnum;

    /** 枚举与数据库都存在的开关 */
    @Schema(description = "枚举与数据库同步的开关")
    private List<String> synced;

    /** 枚举定义总数 */
    @Schema(description = "枚举定义总数")
    private int enumCount;

    /** 数据库开关总数 */
    @Schema(description = "数据库开关总数")
    private int databaseCount;

    /**
     * 枚举开关项（包含枚举名和描述）
     */
    @Data
    @Schema(description = "枚举开关项")
    public static class EnumSwitchItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /** 枚举名称 */
        @Schema(description = "枚举名称", example = "USER_REGISTER")
        private String enumName;

        /** 开关键 */
        @Schema(description = "开关键", example = "user.register.enabled")
        private String switchKey;

        /** 开关描述 */
        @Schema(description = "开关描述", example = "用户注册开关")
        private String description;

        public EnumSwitchItem() {}

        public EnumSwitchItem(String enumName, String switchKey, String description) {
            this.enumName = enumName;
            this.switchKey = switchKey;
            this.description = description;
        }
    }
}
