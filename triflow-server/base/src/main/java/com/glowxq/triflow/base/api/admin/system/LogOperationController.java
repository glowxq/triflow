package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.pojo.dto.LogOperationQueryDTO;
import com.glowxq.triflow.base.system.pojo.vo.LogOperationVO;
import com.glowxq.triflow.base.system.service.LogOperationService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 操作日志控制器
 *
 * @author glowxq
 * @since 2026-01-29
 */
@Tag(name = "操作日志", description = "操作日志的查询、删除、导出接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/log")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.Log)
public class LogOperationController extends BaseApi {

    private final LogOperationService logOperationService;

    /**
     * 分页查询操作日志
     */
    @Operation(summary = "分页查询操作日志", description = "分页获取操作日志列表")
    @SaCheckPermission("system:log:query")
    @GetMapping("/page")
    public ApiResult<Page<LogOperationVO>> page(LogOperationQueryDTO query) {
        Page<LogOperationVO> page = logOperationService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 获取日志详情
     */
    @Operation(summary = "获取日志详情", description = "根据ID获取操作日志完整信息")
    @SaCheckPermission("system:log:query")
    @GetMapping("/{id}")
    public ApiResult<LogOperationVO> getById(
            @Parameter(description = "日志ID", required = true, example = "1")
            @PathVariable Long id) {
        LogOperationVO detail = logOperationService.getById(id);
        if (detail == null) {
            return ApiResult.error(100010, "日志不存在");
        }
        return ApiResult.success(detail);
    }

    /**
     * 批量删除操作日志
     */
    @Operation(summary = "批量删除操作日志", description = "根据ID列表批量删除操作日志")
    @SaCheckPermission("system:log:delete")
    @DeleteMapping("/batch")
    public ApiResult<Boolean> deleteBatch(
            @Parameter(description = "日志ID列表", required = true)
            @RequestBody List<Long> ids) {
        boolean success = logOperationService.deleteByIds(ids);
        return success ? ApiResult.success(true) : ApiResult.error(100011, "批量删除失败");
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志", description = "清空所有操作日志数据")
    @SaCheckPermission("system:log:clear")
    @DeleteMapping("/clear")
    public ApiResult<Long> clear() {
        long count = logOperationService.clear();
        return ApiResult.success(count);
    }

    /**
     * 导出操作日志
     */
    @Operation(summary = "导出操作日志", description = "导出操作日志数据到Excel")
    @SaCheckPermission("system:log:export")
    @GetMapping("/export")
    public void export(LogOperationQueryDTO query, HttpServletResponse response) throws IOException {
        List<LogOperationVO> list = logOperationService.list(query);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("操作日志", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(list, "操作日志", LogOperationVO.class, response.getOutputStream());
    }

}
