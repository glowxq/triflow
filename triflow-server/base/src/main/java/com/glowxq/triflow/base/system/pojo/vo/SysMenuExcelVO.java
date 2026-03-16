package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.DictFormat;
import com.glowxq.triflow.base.system.entity.SysMenu;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 菜单 Excel 导入导出 VO
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
    @AutoMapper(target = SysMenu.class),
    @AutoMapper(target = SysMenuVO.class)
})
public class SysMenuExcelVO {

    /** 菜单ID（导出时显示，导入时可忽略） */
    @ExcelProperty("菜单ID")
    @ColumnWidth(12)
    private Long id;

    /** 父菜单ID */
    @ExcelProperty("父菜单ID")
    @ColumnWidth(12)
    private Long parentId;

    /** 菜单名称 */
    @ExcelProperty("菜单名称")
    @ColumnWidth(20)
    private String menuName;

    /**
     * 菜单类型
     * <p>
     * 使用表达式进行字典转换：M=目录,C=菜单,F=按钮
     * </p>
     */
    @ExcelProperty("菜单类型")
    @ColumnWidth(12)
    @DictFormat(readConverterExp = "M=目录,C=菜单,F=按钮", isSelected = true)
    private String menuType;

    /** 路由路径 */
    @ExcelProperty("路由路径")
    @ColumnWidth(25)
    private String path;

    /** 路由名称 */
    @ExcelProperty("路由名称")
    @ColumnWidth(20)
    private String name;

    /** 组件路径 */
    @ExcelProperty("组件路径")
    @ColumnWidth(30)
    private String component;

    /** 权限标识 */
    @ExcelProperty("权限标识")
    @ColumnWidth(25)
    private String permission;

    /** 菜单图标 */
    @ExcelProperty("图标")
    @ColumnWidth(20)
    private String icon;

    /** 显示排序 */
    @ExcelProperty("排序")
    @ColumnWidth(10)
    private Integer sort;

    /**
     * 是否可见
     * <p>
     * 使用表达式进行字典转换：0=隐藏,1=显示
     * </p>
     */
    @ExcelProperty("是否可见")
    @ColumnWidth(12)
    @DictFormat(readConverterExp = "0=隐藏,1=显示", isSelected = true)
    private Integer visible;

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

    /** 创建时间 */
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;

}
