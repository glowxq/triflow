package com.glowxq.common.excel.demo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.CellMerge;
import com.glowxq.common.excel.annotation.DictFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户 Excel 导入导出示例实体
 * <p>
 * 演示 common-excel 模块的各种功能：
 * <ul>
 *     <li>基本的 Excel 列映射</li>
 *     <li>字典转换（使用表达式）</li>
 *     <li>动态下拉选项</li>
 *     <li>单元格合并</li>
 * </ul>
 * </p>
 *
 * @author glowxq
 * @since 2024/01/01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExcelVO {

    /**
     * 用户ID
     */
    @ExcelProperty("用户ID")
    @ColumnWidth(15)
    private Long id;

    /**
     * 用户名
     */
    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    /**
     * 邮箱
     */
    @ExcelProperty("邮箱")
    @ColumnWidth(30)
    private String email;

    /**
     * 手机号
     */
    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    /**
     * 性别
     * <p>
     * 使用表达式进行转换：0=男,1=女,2=未知
     * isSelected=true 表示导出时生成下拉选项
     * </p>
     */
    @ExcelProperty("性别")
    @ColumnWidth(10)
    @DictFormat(readConverterExp = "0=男,1=女,2=未知", isSelected = true)
    private String gender;

    /**
     * 状态
     * <p>
     * 使用动态数据源生成下拉选项
     * </p>
     */
    @ExcelProperty("状态")
    @ColumnWidth(12)
    @DictFormat(sourceClass = StatusDynamicSelect.class, isSelected = true)
    private String status;

    /**
     * 部门
     * <p>
     * 相同部门的单元格会自动合并
     * </p>
     */
    @ExcelProperty("部门")
    @ColumnWidth(15)
    @CellMerge
    private String department;

    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

}
