package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.core.common.enums.StatusEnum;
import com.glowxq.triflow.base.system.entity.SysConfig;
import com.glowxq.triflow.base.system.enums.ValueTypeEnum;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置导入导出 VO
 *
 * @author glowxq
 * @since 2025-01-25
 */
@Data
@Schema(description = "系统配置导入导出")
@AutoMapper(target = SysConfig.class)
public class SysConfigExcelVO {

    /** 配置名称 */
    @ExcelProperty("配置名称")
    @ColumnWidth(25)
    @Schema(description = "配置名称", example = "用户初始密码")
    private String configName;

    /** 配置键 */
    @ExcelProperty("配置键")
    @ColumnWidth(30)
    @Schema(description = "配置键", example = "sys.user.initPassword")
    private String configKey;

    /** 配置值 */
    @ExcelProperty("配置值")
    @ColumnWidth(40)
    @Schema(description = "配置值", example = "123456")
    private String configValue;

    /**
     * 值类型
     *
     * @see ValueTypeEnum
     */
    @ExcelProperty("值类型")
    @ColumnWidth(15)
    @Schema(description = "值类型", example = "string")
    private String valueType;

    /** 配置分类 */
    @ExcelProperty("配置分类")
    @ColumnWidth(15)
    @Schema(description = "配置分类", example = "user")
    private String category;

    /**
     * 状态
     *
     * @see StatusEnum
     */
    @ExcelProperty("状态")
    @ColumnWidth(10)
    @Schema(description = "状态", example = "1")
    private Integer status;

    /** 备注说明 */
    @ExcelProperty("备注")
    @ColumnWidth(30)
    @Schema(description = "备注说明")
    private String remark;

}
