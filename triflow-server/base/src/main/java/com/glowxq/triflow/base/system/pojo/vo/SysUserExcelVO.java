package com.glowxq.triflow.base.system.pojo.vo;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.glowxq.common.excel.annotation.DictFormat;
import com.glowxq.triflow.base.system.entity.SysUser;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMappers;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户 Excel 导入导出 VO
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
    @AutoMapper(target = SysUser.class),
    @AutoMapper(target = SysUserVO.class)
})
public class SysUserExcelVO {

    /** 用户ID（导出时显示，导入时可忽略） */
    @ExcelProperty("用户ID")
    @ColumnWidth(12)
    private Long id;

    /** 用户名 */
    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    /** 昵称 */
    @ExcelProperty("昵称")
    @ColumnWidth(20)
    private String nickname;

    /** 真实姓名 */
    @ExcelProperty("真实姓名")
    @ColumnWidth(15)
    private String realName;

    /** 手机号 */
    @ExcelProperty("手机号")
    @ColumnWidth(15)
    private String phone;

    /** 邮箱 */
    @ExcelProperty("邮箱")
    @ColumnWidth(25)
    private String email;

    /**
     * 性别
     * <p>
     * 使用表达式进行字典转换：0=未知,1=男,2=女
     * </p>
     */
    @ExcelProperty("性别")
    @ColumnWidth(10)
    @DictFormat(readConverterExp = "0=未知,1=男,2=女", isSelected = true)
    private Integer gender;

    /** 部门ID */
    @ExcelProperty("部门ID")
    @ColumnWidth(12)
    private Long deptId;

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

    // ========== 以下字段不导出到 Excel ==========

    /** 密码（导入导出时忽略） */
    @ExcelIgnore
    private String password;

    /** 头像URL */
    @ExcelIgnore
    private String avatar;

}
