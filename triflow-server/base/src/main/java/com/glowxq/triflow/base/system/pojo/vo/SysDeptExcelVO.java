package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.DictFormat;
import com.glowxq.triflow.base.system.entity.SysDept;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部门 Excel 导入导出 VO
 * <p>
 * 专用于 Excel 导入导出，与 API 响应的 VO 分离。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AutoMappers({
    @AutoMapper(target = SysDept.class),
    @AutoMapper(target = SysDeptVO.class)
})
public class SysDeptExcelVO {

    /** 部门ID（导出时显示，导入时可忽略） */
    @ExcelProperty("部门ID")
    @ColumnWidth(12)
    private Long id;

    /** 父部门ID */
    @ExcelProperty("父部门ID")
    @ColumnWidth(12)
    private Long parentId;

    /** 部门名称 */
    @ExcelProperty("部门名称")
    @ColumnWidth(20)
    private String deptName;

    /** 显示排序 */
    @ExcelProperty("排序")
    @ColumnWidth(10)
    private Integer sort;

    /** 负责人 */
    @ExcelProperty("负责人")
    @ColumnWidth(15)
    private String leader;

    /** 联系电话 */
    @ExcelProperty("联系电话")
    @ColumnWidth(15)
    private String phone;

    /** 邮箱 */
    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;

    /**
     * 状态
     * <p>
     * 使用表达式进行字典转换：0=禁用,1=正常
     * </p>
     */
    @ExcelProperty("状态")
    @ColumnWidth(10)
    @DictFormat(readConverterExp = "0=禁用,1=正常", isSelected = true)
    private Integer status;

    /** 备注 */
    @ExcelProperty("备注")
    @ColumnWidth(30)
    private String remark;

    /** 创建时间 */
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

}
