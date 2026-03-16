package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysDeptUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptTreeVO;
import com.glowxq.triflow.base.system.pojo.vo.SysDeptVO;
import com.glowxq.triflow.base.system.service.SysDeptService;
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
 * 部门管理控制器
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Tag(name = "部门管理", description = "部门的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/dept")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysDeptController extends BaseApi {

    private final SysDeptService deptService;
    private final Converter converter;

    /**
     * 创建部门
     */
    @Operation(summary = "创建部门", description = "创建一个新部门")
    @SaCheckPermission("system:dept:create")
    @PostMapping
    public ApiResult<Long> create(@Valid @RequestBody SysDeptCreateDTO dto) {
        Long id = deptService.create(dto);
        return ApiResult.success(id);
    }

    /**
     * 更新部门
     */
    @Operation(summary = "更新部门", description = "根据ID更新部门信息")
    @SaCheckPermission("system:dept:update")
    @PutMapping
    public ApiResult<Boolean> update(@Valid @RequestBody SysDeptUpdateDTO dto) {
        boolean success = deptService.update(dto);
        return success ? ApiResult.success(true) : ApiResult.error(100001, "更新失败，部门不存在");
    }

    /**
     * 获取部门详情
     */
    @Operation(summary = "获取部门详情", description = "根据ID获取部门信息")
    @SaCheckPermission("system:dept:query")
    @GetMapping("/{id}")
    public ApiResult<SysDeptVO> getById(
        @Parameter(description = "部门ID", required = true, example = "1")
        @PathVariable Long id) {
        SysDeptVO detail = deptService.getById(id);
        if (detail == null) {
            return ApiResult.error(100002, "部门不存在");
        }
        return ApiResult.success(detail);
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门", description = "根据ID删除部门")
    @SaCheckPermission("system:dept:delete")
    @DeleteMapping("/{id}")
    public ApiResult<Boolean> delete(
        @Parameter(description = "部门ID", required = true, example = "1")
        @PathVariable Long id) {
        boolean success = deptService.deleteById(id);
        return success ? ApiResult.success(true) : ApiResult.error(100003, "删除失败");
    }

    /**
     * 查询部门列表
     */
    @Operation(summary = "查询部门列表", description = "根据条件查询部门列表")
    @SaCheckPermission("system:dept:query")
    @GetMapping("/list")
    public ApiResult<List<SysDeptVO>> list(SysDeptQueryDTO query) {
        List<SysDeptVO> list = deptService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 获取部门树
     */
    @Operation(summary = "获取部门树", description = "获取完整的部门树形结构")
    @SaCheckPermission("system:dept:query")
    @GetMapping("/tree")
    public ApiResult<List<SysDeptTreeVO>> getTree() {
        List<SysDeptTreeVO> tree = deptService.getDeptTree();
        return ApiResult.success(tree);
    }

    /**
     * 导出部门
     */
    @Operation(summary = "导出部门", description = "导出部门数据到Excel")
    @SaCheckPermission("system:dept:export")
    @GetMapping("/export")
    public void export(SysDeptQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysDeptVO> list = deptService.list(query);
        List<SysDeptExcelVO> excelList = converter.convert(list, SysDeptExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("部门数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "部门数据", SysDeptExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入部门
     */
    @Operation(summary = "导入部门", description = "从Excel导入部门数据")
    @SaCheckPermission("system:dept:import")
    @PostMapping("/import")
    public ApiResult<Map<String, Object>> importData(
        @Parameter(description = "Excel文件", required = true)
        @RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysDeptExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysDeptExcelVO.class, true);
        List<SysDeptExcelVO> excelList = result.getList();
        int success = 0;
        int fail = 0;
        List<String> errorMessages = new ArrayList<>();
        for (SysDeptExcelVO excelVO : excelList) {
            try {
                SysDeptCreateDTO dto = converter.convert(excelVO, SysDeptCreateDTO.class);
                deptService.create(dto);
                success++;
            } catch (Exception e) {
                fail++;
                errorMessages.add("部门[" + excelVO.getDeptName() + "]导入失败: " + e.getMessage());
            }
        }
        return ApiResult.success(Map.of(
            "success", success,
            "fail", fail,
            "message", String.join("; ", errorMessages)
        ));
    }

    /**
     * 下载部门导入模板
     */
    @Operation(summary = "下载导入模板", description = "下载部门导入Excel模板")
    @SaCheckPermission("system:dept:query")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("部门导入模板", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(List.of(), "部门数据", SysDeptExcelVO.class, response.getOutputStream());
    }

}
