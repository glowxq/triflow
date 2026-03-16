package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysMenuUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysMenuVO;
import com.glowxq.triflow.base.system.service.SysMenuService;
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
 * 菜单管理控制器
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "菜单管理", description = "菜单的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/menu")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysMenuController extends BaseApi {

    private final SysMenuService menuService;
    private final Converter converter;

    /**
     * 创建菜单
     */
    @Operation(summary = "创建菜单", description = "创建一个新菜单")
    @SaCheckPermission("system:menu:create")
    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody SysMenuCreateDTO dto) {
        Long id = menuService.create(dto);
        return ApiResult.success(id);
    }

    /**
     * 更新菜单
     */
    @Operation(summary = "更新菜单", description = "根据ID更新菜单信息")
    @SaCheckPermission("system:menu:update")
    @PutMapping
    public ApiResult<Boolean> update(@Valid @RequestBody SysMenuUpdateDTO dto) {
        boolean success = menuService.update(dto);
        return success ? ApiResult.success(true) : ApiResult.error(100001, "更新失败，菜单不存在");
    }

    /**
     * 获取菜单详情
     */
    @Operation(summary = "获取菜单详情", description = "根据ID获取菜单信息")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/{id}")
    public ApiResult<SysMenuVO> getById(
        @Parameter(description = "菜单ID", required = true, example = "1")
        @PathVariable Long id) {
        SysMenuVO detail = menuService.getById(id);
        if (detail == null) {
            return ApiResult.error(100002, "菜单不存在");
        }
        return ApiResult.success(detail);
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    @SaCheckPermission("system:menu:delete")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(
        @Parameter(description = "菜单ID", required = true, example = "1")
        @PathVariable Long id) {
        boolean success = menuService.deleteById(id);
        return success ? ApiResult.success(true) : ApiResult.error(100003, "删除失败");
    }

    /**
     * 查询菜单列表
     */
    @Operation(summary = "查询菜单列表", description = "根据条件查询菜单列表")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/list")
    public ApiResult<List<SysMenuVO>> list(SysMenuQueryDTO query) {
        List<SysMenuVO> list = menuService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 获取菜单树
     */
    @Operation(summary = "获取菜单树", description = "获取完整的菜单树形结构")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/tree")
    public ApiResult<List<SysMenuTreeVO>> getTree() {
        List<SysMenuTreeVO> tree = menuService.getMenuTree();
        return ApiResult.success(tree);
    }

    /**
     * 获取所有权限标识
     */
    @Operation(summary = "获取所有权限标识", description = "获取所有菜单的权限标识列表")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/permissions")
    public ApiResult<List<String>> getAllPermissions() {
        List<String> permissions = menuService.getAllPermissions();
        return ApiResult.success(permissions);
    }

    /**
     * 导出菜单
     */
    @Operation(summary = "导出菜单", description = "导出菜单数据到Excel")
    @SaCheckPermission("system:menu:export")
    @GetMapping("/export")
    public void export(SysMenuQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysMenuVO> list = menuService.list(query);
        List<SysMenuExcelVO> excelList = converter.convert(list, SysMenuExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("菜单数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "菜单数据", SysMenuExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入菜单
     */
    @Operation(summary = "导入菜单", description = "从Excel导入菜单数据")
    @SaCheckPermission("system:menu:import")
    @PostMapping("/import")
    public ApiResult<Map<String, Object>> importData(
        @Parameter(description = "Excel文件", required = true)
        @RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysMenuExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysMenuExcelVO.class, true);
        List<SysMenuExcelVO> excelList = result.getList();
        int success = 0;
        int fail = 0;
        List<String> errorMessages = new ArrayList<>();
        for (SysMenuExcelVO excelVO : excelList) {
            try {
                SysMenuCreateDTO dto = converter.convert(excelVO, SysMenuCreateDTO.class);
                menuService.create(dto);
                success++;
            } catch (Exception e) {
                fail++;
                errorMessages.add("菜单[" + excelVO.getMenuName() + "]导入失败: " + e.getMessage());
            }
        }
        return ApiResult.success(Map.of(
            "success", success,
            "fail", fail,
            "message", String.join("; ", errorMessages)
        ));
    }

    /**
     * 下载菜单导入模板
     */
    @Operation(summary = "下载导入模板", description = "下载菜单导入Excel模板")
    @SaCheckPermission("system:menu:query")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("菜单导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(List.of(), "菜单数据", SysMenuExcelVO.class, response.getOutputStream());
    }

}
