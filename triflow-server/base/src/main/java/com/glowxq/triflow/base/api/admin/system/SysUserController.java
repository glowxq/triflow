package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.common.mysql.datascope.DataScopeHandler;
import com.glowxq.common.security.core.service.SessionService;
import com.glowxq.triflow.base.system.entity.SysUserSocial;
import com.glowxq.triflow.base.system.pojo.dto.SysUserCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysUserUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserDetailVO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SysUserVO;
import com.glowxq.triflow.base.system.service.SysUserService;
import com.glowxq.triflow.base.system.service.SysUserSocialService;
import com.mybatisflex.core.paginate.Page;
import io.github.linpeilie.Converter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 用户管理控制器
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "用户管理", description = "用户的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/user")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysUserController extends BaseApi {

    private final SysUserService userService;

    private final SysUserSocialService userSocialService;
    private final SessionService sessionService;
    private final Converter converter;

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户", description = "创建一个新用户")
    @SaCheckPermission("system:user:create")
    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody SysUserCreateDTO dto) {
        Long id = userService.create(dto);
        return ApiResult.success(id);
    }

    /**
     * 更新用户
     */
    @Operation(summary = "更新用户", description = "根据ID更新用户信息")
    @SaCheckPermission("system:user:update")
    @PutMapping
    public ApiResult<Boolean> update(@Valid @RequestBody SysUserUpdateDTO dto) {
        boolean success = userService.update(dto);
        return success ? ApiResult.success(true) : ApiResult.error(100001, "更新失败，用户不存在");
    }

    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情", description = "根据ID获取用户完整信息")
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}")
    public ApiResult<SysUserDetailVO> getById(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id) {
        SysUserDetailVO detail = userService.getById(id);
        if (detail == null) {
            return ApiResult.error(100002, "用户不存在");
        }
        return ApiResult.success(detail);
    }

    /**
     * 删除用户
     */
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id) {
        boolean success = userService.deleteById(id);
        return success ? ApiResult.success(true) : ApiResult.error(100003, "删除失败，用户不存在");
    }

    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户", description = "根据ID列表批量删除用户")
    @SaCheckPermission("system:user:delete")
    @DeleteMapping("/batch")
    public ApiResult<Boolean> deleteBatch(
        @Parameter(description = "用户ID列表", required = true)
        @RequestBody List<Long> ids) {
        boolean success = userService.deleteByIds(ids);
        return success ? ApiResult.success(true) : ApiResult.error(100004, "批量删除失败");
    }

    /**
     * 查询用户列表
     */
    @Operation(summary = "查询用户列表", description = "根据条件查询用户列表")
    @SaCheckPermission("system:user:query")
    @GetMapping("/list")
    public ApiResult<List<SysUserVO>> list(SysUserQueryDTO query) {
        List<SysUserVO> list = userService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户", description = "分页获取用户列表")
    @SaCheckPermission("system:user:query")
    @DataScopeHandler
    @GetMapping("/page")
    public ApiResult<Page<SysUserVO>> page(SysUserQueryDTO query) {
        Page<SysUserVO> page = userService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 重置密码
     */
    @Operation(summary = "重置密码", description = "重置用户密码")
    @SaCheckPermission("system:user:resetPwd")
    @PutMapping("/{id}/reset-password")
    public ApiResult<Boolean> resetPassword(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "新密码", required = true)
        @RequestParam String newPassword) {
        boolean success = userService.resetPassword(id, newPassword);
        return success ? ApiResult.success(true) : ApiResult.error(100005, "重置密码失败");
    }

    /**
     * 分配角色
     */
    @Operation(summary = "分配角色", description = "为用户分配角色")
    @SaCheckPermission("system:user:update")
    @PutMapping("/{id}/roles")
    public ApiResult<Void> assignRoles(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id,
        @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return ApiResult.success(null);
    }

    /**
     * 导出用户
     */
    @Operation(summary = "导出用户", description = "导出用户数据到Excel")
    @SaCheckPermission("system:user:export")
    @GetMapping("/export")
    public void export(SysUserQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysUserVO> list = userService.list(query);
        List<SysUserExcelVO> excelList = converter.convert(list, SysUserExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "用户数据", SysUserExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入用户
     */
    @Operation(summary = "导入用户", description = "从Excel导入用户数据")
    @SaCheckPermission("system:user:import")
    @PostMapping("/import")
    public ApiResult<Map<String, Object>> importData(
        @Parameter(description = "Excel文件", required = true)
        @RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysUserExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysUserExcelVO.class, true);
        List<SysUserExcelVO> excelList = result.getList();
        int success = 0;
        int fail = 0;
        List<String> errorMessages = new ArrayList<>();
        for (SysUserExcelVO excelVO : excelList) {
            try {
                SysUserCreateDTO dto = converter.convert(excelVO, SysUserCreateDTO.class);
                // 设置默认密码
                dto.setPassword("123456");
                userService.create(dto);
                success++;
            } catch (Exception e) {
                fail++;
                errorMessages.add("用户[" + excelVO.getUsername() + "]导入失败: " + e.getMessage());
            }
        }
        return ApiResult.success(Map.of(
            "success", success,
            "fail", fail,
            "message", String.join("; ", errorMessages)
        ));
    }

    /**
     * 下载用户导入模板
     */
    @Operation(summary = "下载导入模板", description = "下载用户导入Excel模板")
    @SaCheckPermission("system:user:import")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("用户导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(List.of(), "用户数据", SysUserExcelVO.class, response.getOutputStream());
    }

    /**
     * 踢用户下线
     */
    @Operation(summary = "踢用户下线", description = "强制指定用户下线")
    @SaCheckPermission("system:user:kickout")
    @PostMapping("/{id}/kickout")
    public ApiResult<Void> kickout(
        @Parameter(description = "用户ID", required = true, example = "1")
        @PathVariable Long id) {
        sessionService.kickoutUser(id);
        return ApiResult.success(null);
    }

    /**
     * 批量踢用户下线
     */
    @Operation(summary = "批量踢用户下线", description = "强制批量用户下线")
    @SaCheckPermission("system:user:kickout")
    @PostMapping("/kickout/batch")
    public ApiResult<Void> kickoutBatch(
        @Parameter(description = "用户ID列表", required = true)
        @RequestBody List<Long> ids) {
        sessionService.kickoutUsers(ids);
        return ApiResult.success(null);
    }

    /**
     * 批量设置数据权限
     */
    @Operation(summary = "批量设置数据权限", description = "批量修改用户的数据权限范围")
    @SaCheckPermission("system:user:update")
    @PutMapping("/batch/data-scope")
    public ApiResult<Integer> batchUpdateDataScope(
        @Parameter(description = "用户ID列表", required = true)
        @RequestParam List<Long> userIds,
        @Parameter(description = "数据权限范围", required = true)
        @RequestParam String dataScope) {
        int affected = userService.batchUpdateDataScope(userIds, dataScope);
        return ApiResult.success(affected);
    }

    /**
     * 获取用户第三方绑定列表
     */
    @Operation(summary = "获取第三方绑定", description = "获取用户绑定的第三方账号列表")
    @SaCheckPermission("system:user:query")
    @GetMapping("/{id}/socials")
    public ApiResult<List<SysUserSocial>> getSocials(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id) {
        List<SysUserSocial> socials = userSocialService.getByUserId(id);
        return ApiResult.success(socials);
    }

    /**
     * 解绑第三方账号
     */
    @Operation(summary = "解绑第三方账号", description = "解除用户与第三方平台的绑定")
    @SaCheckPermission("system:user:update")
    @DeleteMapping("/{id}/socials/{socialType}")
    public ApiResult<Boolean> unbindSocial(
        @Parameter(description = "用户ID", required = true)
        @PathVariable Long id,
        @Parameter(description = "第三方平台类型", required = true)
        @PathVariable String socialType) {
        boolean success = userSocialService.unbind(id, socialType);
        return success ? ApiResult.success(true) : ApiResult.error(100010, "解绑失败");
    }

}
