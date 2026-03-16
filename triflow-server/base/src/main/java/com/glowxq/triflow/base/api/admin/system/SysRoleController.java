package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysRoleUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysRoleExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SysRoleVO;
import com.glowxq.triflow.base.system.service.SysRoleService;
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
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 角色管理控制器
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "角色管理", description = "角色的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/role")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysRoleController extends BaseApi {

    private final SysRoleService roleService;
    private final Converter converter;

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色", description = "创建一个新角色")
    @SaCheckPermission("system:role:create")
    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody SysRoleCreateDTO dto) {
        Long id = roleService.create(dto);
        return ApiResult.success(id);
    }

    /**
     * 更新角色
     */
    @Operation(summary = "更新角色", description = "根据ID更新角色信息")
    @SaCheckPermission("system:role:update")
    @PutMapping
    public ApiResult<Boolean> update(@Valid @RequestBody SysRoleUpdateDTO dto) {
        boolean success = roleService.update(dto);
        return success ? ApiResult.success(true) : ApiResult.error(100001, "更新失败，角色不存在");
    }

    /**
     * 获取角色详情
     */
    @Operation(summary = "获取角色详情", description = "根据ID获取角色信息")
    @SaCheckPermission("system:role:query")
    @GetMapping("/{id}")
    public ApiResult<SysRoleVO> getById(
        @Parameter(description = "角色ID", required = true, example = "1")
        @PathVariable Long id) {
        SysRoleVO detail = roleService.getById(id);
        if (detail == null) {
            return ApiResult.error(100002, "角色不存在");
        }
        return ApiResult.success(detail);
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(
        @Parameter(description = "角色ID", required = true, example = "1")
        @PathVariable Long id) {
        boolean success = roleService.deleteById(id);
        return success ? ApiResult.success(true) : ApiResult.error(100003, "删除失败");
    }

    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色", description = "根据ID列表批量删除角色")
    @SaCheckPermission("system:role:delete")
    @DeleteMapping("/batch")
    public ApiResult<Boolean> deleteBatch(
        @Parameter(description = "角色ID列表", required = true)
        @RequestBody List<Long> ids) {
        boolean success = roleService.deleteByIds(ids);
        return success ? ApiResult.success(true) : ApiResult.error(100004, "批量删除失败");
    }

    /**
     * 查询角色列表
     */
    @Operation(summary = "查询角色列表", description = "根据条件查询角色列表")
    @SaCheckPermission("system:role:query")
    @GetMapping("/list")
    public ApiResult<List<SysRoleVO>> list(SysRoleQueryDTO query) {
        List<SysRoleVO> list = roleService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色", description = "分页获取角色列表")
    @SaCheckPermission("system:role:query")
    @GetMapping("/page")
    public ApiResult<Page<SysRoleVO>> page(SysRoleQueryDTO query) {
        Page<SysRoleVO> page = roleService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 获取所有启用的角色
     */
    @Operation(summary = "获取所有角色", description = "获取所有启用的角色列表")
    @SaCheckPermission("system:role:query")
    @GetMapping("/all")
    public ApiResult<List<SysRoleVO>> listAll() {
        List<SysRoleVO> list = roleService.listAllEnabled();
        return ApiResult.success(list);
    }

    /**
     * 分配菜单权限
     */
    @Operation(summary = "分配菜单权限", description = "为角色分配菜单权限")
    @SaCheckPermission("system:role:update")
    @PutMapping("/{id}/menus")
    public ApiResult<Void> assignMenus(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long id,
        @RequestBody List<Long> menuIds) {
        roleService.assignMenus(id, menuIds);
        return ApiResult.success(null);
    }

    /**
     * 获取角色菜单ID
     */
    @Operation(summary = "获取角色菜单", description = "获取角色已分配的菜单ID列表")
    @SaCheckPermission("system:role:query")
    @GetMapping("/{id}/menus")
    public ApiResult<List<Long>> getRoleMenus(
        @Parameter(description = "角色ID", required = true)
        @PathVariable Long id) {
        List<Long> menuIds = roleService.getRoleMenuIds(id);
        return ApiResult.success(menuIds);
    }

    /**
     * 导出角色
     */
    @Operation(summary = "导出角色", description = "导出角色数据到Excel")
    @SaCheckPermission("system:role:export")
    @GetMapping("/export")
    public void export(SysRoleQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysRoleVO> list = roleService.list(query);
        List<SysRoleExcelVO> excelList = converter.convert(list, SysRoleExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("角色数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "角色数据", SysRoleExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入角色
     */
    @Operation(summary = "导入角色", description = "从Excel导入角色数据")
    @SaCheckPermission("system:role:import")
    @PostMapping("/import")
    public ApiResult<Map<String, Object>> importData(
        @Parameter(description = "Excel文件", required = true)
        @RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysRoleExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysRoleExcelVO.class, true);
        List<SysRoleExcelVO> excelList = result.getList();
        int success = 0;
        int fail = 0;
        List<String> errorMessages = new ArrayList<>();
        for (SysRoleExcelVO excelVO : excelList) {
            try {
                SysRoleCreateDTO dto = converter.convert(excelVO, SysRoleCreateDTO.class);
                roleService.create(dto);
                success++;
            } catch (Exception e) {
                fail++;
                errorMessages.add("角色[" + excelVO.getRoleName() + "]导入失败: " + e.getMessage());
            }
        }
        return ApiResult.success(Map.of(
            "success", success,
            "fail", fail,
            "message", String.join("; ", errorMessages)
        ));
    }

    /**
     * 下载角色导入模板
     */
    @Operation(summary = "下载导入模板", description = "下载角色导入Excel模板")
    @SaCheckPermission("system:role:export")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("角色导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(List.of(), "角色数据", SysRoleExcelVO.class, response.getOutputStream());
    }

}
