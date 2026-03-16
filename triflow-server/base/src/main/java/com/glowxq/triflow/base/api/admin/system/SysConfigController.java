package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.enums.ConfigCategoryEnum;
import com.glowxq.triflow.base.system.enums.ConfigTypeEnum;
import com.glowxq.triflow.base.system.enums.ValueTypeEnum;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysConfigUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysConfigExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SysConfigVO;
import com.glowxq.triflow.base.system.service.SysConfigService;
import com.mybatisflex.core.paginate.Page;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 系统配置管理控制器
 * <p>
 * 提供系统配置的增删改查接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "系统配置管理", description = "系统配置的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/config")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysConfigController extends BaseApi {

    private final SysConfigService configService;

    /**
     * 创建配置
     *
     * @param dto 创建请求参数
     * @return 操作结果
     */
    @Operation(summary = "创建配置", description = "创建一个新的系统配置")
    @SaCheckPermission("system:config:create")
    @PostMapping("/create")
    public ApiResult<Void> create(@Valid @RequestBody SysConfigCreateDTO dto) {
        configService.create(dto);
        return ApiResult.success();
    }

    /**
     * 更新配置
     *
     * @param dto 更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新配置", description = "根据ID更新系统配置")
    @SaCheckPermission("system:config:update")
    @PutMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody SysConfigUpdateDTO dto) {
        configService.update(dto);
        return ApiResult.success();
    }

    /**
     * 获取配置详情
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @Operation(summary = "获取配置详情", description = "根据ID获取配置信息")
    @SaCheckPermission("system:config:query")
    @GetMapping("/detail")
    public ApiResult<SysConfigVO> detail(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        SysConfigVO detail = configService.getById(id);
        return ApiResult.success(detail);
    }

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Operation(summary = "获取配置值", description = "根据配置键获取配置值")
    @SaCheckPermission("system:config:query")
    @GetMapping("/getByKey")
    public ApiResult<String> getByKey(
            @Parameter(description = "配置键", required = true, example = "sys.user.initPassword")
            @RequestParam String configKey) {
        String value = configService.getValueByKey(configKey);
        return ApiResult.success(value);
    }

    /**
     * 删除配置
     *
     * @param id 配置ID
     * @return 操作结果
     */
    @Operation(summary = "删除配置", description = "根据ID删除配置")
    @SaCheckPermission("system:config:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "配置ID", required = true, example = "1")
            @RequestParam Long id) {
        configService.deleteById(id);
        return ApiResult.success();
    }

    /**
     * 批量删除配置
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除配置", description = "根据ID列表批量删除配置")
    @SaCheckPermission("system:config:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "配置ID列表", required = true)
            @RequestBody List<Long> ids) {
        configService.deleteByIds(ids);
        return ApiResult.success();
    }

    /**
     * 查询配置列表
     *
     * @param query 查询条件
     * @return 配置列表
     */
    @Operation(summary = "查询配置列表", description = "根据条件查询配置列表")
    @SaCheckPermission("system:config:query")
    @PostMapping("/list")
    public ApiResult<List<SysConfigVO>> list(@RequestBody SysConfigQueryDTO query) {
        List<SysConfigVO> list = configService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询配置
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询配置", description = "分页获取配置列表")
    @SaCheckPermission("system:config:query")
    @PostMapping("/page")
    public ApiResult<Page<SysConfigVO>> page(@RequestBody SysConfigQueryDTO query) {
        Page<SysConfigVO> page = configService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 根据分类获取配置
     *
     * @param category 配置分类
     * @return 配置列表
     */
    @Operation(summary = "根据分类获取配置", description = "获取指定分类下的所有配置")
    @SaCheckPermission("system:config:query")
    @GetMapping("/listByCategory")
    public ApiResult<List<SysConfigVO>> listByCategory(
            @Parameter(description = "配置分类", required = true, example = "user")
            @RequestParam String category) {
        List<SysConfigVO> list = configService.listByCategory(category);
        return ApiResult.success(list);
    }

    /**
     * 获取所有启用的配置
     *
     * @return 配置列表
     */
    @Operation(summary = "获取所有配置", description = "获取所有启用的配置列表")
    @SaCheckPermission("system:config:query")
    @GetMapping("/listAll")
    public ApiResult<List<SysConfigVO>> listAll() {
        List<SysConfigVO> list = configService.listAllEnabled();
        return ApiResult.success(list);
    }

    /**
     * 刷新配置缓存
     *
     * @return 操作结果
     */
    @Operation(summary = "刷新配置缓存", description = "刷新系统配置的缓存")
    @SaCheckPermission("system:config:refresh")
    @PostMapping("/refreshCache")
    public ApiResult<Void> refreshCache() {
        configService.refreshCache();
        return ApiResult.success();
    }

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否存在
     */
    @Operation(summary = "检查配置键", description = "检查配置键是否已存在")
    @SaCheckPermission("system:config:query")
    @GetMapping("/checkKey")
    public ApiResult<Boolean> checkConfigKey(
            @Parameter(description = "配置键", required = true)
            @RequestParam String configKey,
            @Parameter(description = "排除的ID")
            @RequestParam(required = false) Long excludeId) {
        var config = configService.getByConfigKey(configKey);
        boolean exists = config != null && (excludeId == null || !excludeId.equals(config.getId()));
        return ApiResult.success(exists);
    }

    // ==================== 枚举接口 ====================

    /**
     * 获取配置分类枚举列表
     *
     * @return 分类枚举列表
     */
    @Operation(summary = "获取分类枚举", description = "获取配置分类的枚举列表")
    @SaCheckPermission("system:config:query")
    @GetMapping("/enums/categories")
    public ApiResult<List<Map<String, String>>> getCategoryEnums() {
        List<Map<String, String>> result = Arrays.stream(ConfigCategoryEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取配置类型枚举列表
     *
     * @return 类型枚举列表
     */
    @Operation(summary = "获取类型枚举", description = "获取配置类型的枚举列表")
    @SaCheckPermission("system:config:query")
    @GetMapping("/enums/types")
    public ApiResult<List<Map<String, String>>> getTypeEnums() {
        List<Map<String, String>> result = Arrays.stream(ConfigTypeEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取值类型枚举列表
     *
     * @return 值类型枚举列表
     */
    @Operation(summary = "获取值类型枚举", description = "获取配置值类型的枚举列表")
    @SaCheckPermission("system:config:query")
    @GetMapping("/enums/valueTypes")
    public ApiResult<List<Map<String, String>>> getValueTypeEnums() {
        List<Map<String, String>> result = Arrays.stream(ValueTypeEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }


    // ==================== 导入导出 ====================

    /**
     * 导出配置列表
     *
     * @param query    查询条件
     * @param response HTTP响应
     */
    @Operation(summary = "导出配置列表", description = "导出配置数据到Excel文件")
    @SaCheckPermission("system:config:query")
    @PostMapping("/export")
    public void export(@RequestBody SysConfigQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysConfigVO> list = configService.list(query);
        List<SysConfigExcelVO> excelList = MapStructUtils.convert(list, SysConfigExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("系统配置", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "系统配置", SysConfigExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入配置
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @Operation(summary = "导入配置", description = "从Excel文件导入配置数据")
    @SaCheckPermission("system:config:create")
    @PostMapping("/import")
    public ApiResult<String> importData(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysConfigExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysConfigExcelVO.class, true);
        List<SysConfigExcelVO> list = result.getList();
        if (list.isEmpty()) {
            return ApiResult.error(500, "导入数据为空");
        }
        for (SysConfigExcelVO excel : list) {
            SysConfigCreateDTO dto = MapStructUtils.convert(excel, SysConfigCreateDTO.class);
            configService.create(dto);
        }
        return ApiResult.success("导入成功，共导入 " + list.size() + " 条数据");
    }

    /**
     * 下载导入模板
     *
     * @param response HTTP响应
     */
    @Operation(summary = "下载导入模板", description = "下载配置导入的Excel模板")
    @SaCheckPermission("system:config:query")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("配置导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(Collections.emptyList(), "配置导入模板", SysConfigExcelVO.class, response.getOutputStream());
    }

}
