package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.entity.SysSwitch;
import com.glowxq.triflow.base.system.enums.SwitchCategoryEnum;
import com.glowxq.triflow.base.system.enums.SwitchScopeEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统开关导入导出 VO
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "系统开关导入导出")
@AutoMapper(target = SysSwitch.class)
public class SysSwitchExcelVO {

    /** 开关名称 */
    @ExcelProperty("开关名称")
    @ColumnWidth(25)
    @Schema(description = "开关名称", example = "用户注册开关")
    private String switchName;

    /** 开关键 */
    @ExcelProperty("开关键")
    @ColumnWidth(30)
    @Schema(description = "开关键", example = "user.register.enabled")
    private String switchKey;

    /**
     * 开关状态
     *
     * @see StatusEnum
     */
    @ExcelProperty("开关状态")
    @ColumnWidth(12)
    @Schema(description = "开关状态", example = "1")
    private Integer switchValue;

    /**
     * 开关类型
     *
     * @see SwitchTypeEnum
     */
    @ExcelProperty("开关类型")
    @ColumnWidth(15)
    @Schema(description = "开关类型", example = "feature")
    private String switchType;

    /**
     * 开关分类
     *
     * @see SwitchCategoryEnum
     */
    @ExcelProperty("开关分类")
    @ColumnWidth(15)
    @Schema(description = "开关分类", example = "user")
    private String category;

    /**
     * 作用范围
     *
     * @see SwitchScopeEnum
     */
    @ExcelProperty("作用范围")
    @ColumnWidth(15)
    @Schema(description = "作用范围", example = "global")
    private String scope;

    /** 开关描述 */
    @ExcelProperty("开关描述")
    @ColumnWidth(30)
    @Schema(description = "开关描述")
    private String description;

    /** 备注说明 */
    @ExcelProperty("备注")
    @ColumnWidth(30)
    @Schema(description = "备注说明")
    private String remark;

}
