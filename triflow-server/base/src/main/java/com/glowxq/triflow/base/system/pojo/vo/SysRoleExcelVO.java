package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.DictFormat;
import com.glowxq.triflow.base.system.entity.SysRole;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 角色 Excel 导入导出 VO
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
    @AutoMapper(target = SysRole.class),
    @AutoMapper(target = SysRoleVO.class)
})
public class SysRoleExcelVO {

    /** 角色ID（导出时显示，导入时可忽略） */
    @ExcelProperty("角色ID")
    @ColumnWidth(12)
    private Long id;

    /** 角色编码 */
    @ExcelProperty("角色编码")
    @ColumnWidth(20)
    private String roleKey;

    /** 角色名称 */
    @ExcelProperty("角色名称")
    @ColumnWidth(20)
    private String roleName;

    /** 显示排序 */
    @ExcelProperty("排序")
    @ColumnWidth(10)
    private Integer sort;

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
