package com.glowxq.triflow.base.api.admin.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.excel.core.ExcelResult;
import com.glowxq.common.excel.utils.ExcelUtils;
import com.glowxq.triflow.base.system.enums.SwitchCategoryEnum;
import com.glowxq.triflow.base.system.enums.SwitchKeyEnum;
import com.glowxq.triflow.base.system.enums.SwitchScopeEnum;
import com.glowxq.triflow.base.system.enums.SwitchStrategyEnum;
import com.glowxq.triflow.base.system.enums.SwitchTypeEnum;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchCreateDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchQueryDTO;
import com.glowxq.triflow.base.system.pojo.dto.SysSwitchUpdateDTO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchExcelVO;
import com.glowxq.triflow.base.system.pojo.vo.SwitchEnumCompareVO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchLogVO;
import com.glowxq.triflow.base.system.pojo.vo.SysSwitchVO;
import com.glowxq.triflow.base.system.service.SysSwitchService;
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
import java.util.*;
import java.util.stream.Collectors;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 系统开关管理控制器
 * <p>
 * 提供系统开关的增删改查接口。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-24
 */
@Tag(name = "系统开关管理", description = "系统开关的增删改查接口")
@AdminApi
@RestController
@RequestMapping("/base/admin/system/switch")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysSwitchController extends BaseApi {

    private final SysSwitchService switchService;

    /**
     * 创建开关
     *
     * @param dto 创建请求参数
     * @return 操作结果
     */
    @Operation(summary = "创建开关", description = "创建一个新的系统开关")
    @SaCheckPermission("system:switch:create")
    @PostMapping("/create")
    public ApiResult<Void> create(@Valid @RequestBody SysSwitchCreateDTO dto) {
        switchService.create(dto);
        return ApiResult.success();
    }

    /**
     * 更新开关
     *
     * @param dto 更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新开关", description = "根据ID更新系统开关")
    @SaCheckPermission("system:switch:update")
    @PutMapping("/update")
    public ApiResult<Void> update(@Valid @RequestBody SysSwitchUpdateDTO dto) {
        switchService.update(dto);
        return ApiResult.success();
    }

    /**
     * 切换开关状态
     *
     * @param id          开关ID
     * @param body        请求体（包含switchValue和changeReason）
     * @return 操作结果
     */
    @Operation(summary = "切换开关状态", description = "快速切换开关的开启/关闭状态")
    @SaCheckPermission("system:switch:toggle")
    @PutMapping("/toggle")
    public ApiResult<Void> toggle(
            @Parameter(description = "开关ID", required = true)
            @RequestParam Long id,
            @RequestBody Map<String, Object> body) {
        Integer switchValue = (Integer) body.get("switchValue");
        String changeReason = (String) body.get("changeReason");
        switchService.toggle(id, switchValue, changeReason);
        return ApiResult.success();
    }

    /**
     * 获取开关详情
     *
     * @param id 开关ID
     * @return 开关详情
     */
    @Operation(summary = "获取开关详情", description = "根据ID获取开关信息")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/detail")
    public ApiResult<SysSwitchVO> detail(
            @Parameter(description = "开关ID", required = true, example = "1")
            @RequestParam Long id) {
        SysSwitchVO detail = switchService.getById(id);
        return ApiResult.success(detail);
    }

    /**
     * 检查开关是否开启
     *
     * @param switchKey 开关键
     * @param userId    用户ID（用于灰度判断）
     * @return 开关状态
     */
    @Operation(summary = "检查开关状态", description = "根据开关键检查开关是否开启")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/check")
    public ApiResult<Boolean> isEnabled(
            @Parameter(description = "开关键", required = true, example = "user.register.enabled")
            @RequestParam String switchKey,
            @Parameter(description = "用户ID（用于灰度判断）")
            @RequestParam(required = false) Long userId) {
        boolean enabled = switchService.isEnabled(switchKey, userId);
        return ApiResult.success(enabled);
    }

    /**
     * 删除开关
     *
     * @param id 开关ID
     * @return 操作结果
     */
    @Operation(summary = "删除开关", description = "根据ID删除开关")
    @SaCheckPermission("system:switch:delete")
    @DeleteMapping("/delete")
    public ApiResult<Void> delete(
            @Parameter(description = "开关ID", required = true, example = "1")
            @RequestParam Long id) {
        switchService.deleteById(id);
        return ApiResult.success();
    }

    /**
     * 批量删除开关
     *
     * @param ids ID列表
     * @return 操作结果
     */
    @Operation(summary = "批量删除开关", description = "根据ID列表批量删除开关")
    @SaCheckPermission("system:switch:delete")
    @DeleteMapping("/deleteBatch")
    public ApiResult<Void> deleteBatch(
            @Parameter(description = "开关ID列表", required = true)
            @RequestBody List<Long> ids) {
        switchService.deleteByIds(ids);
        return ApiResult.success();
    }

    /**
     * 查询开关列表
     *
     * @param query 查询条件
     * @return 开关列表
     */
    @Operation(summary = "查询开关列表", description = "根据条件查询开关列表")
    @SaCheckPermission("system:switch:query")
    @PostMapping("/list")
    public ApiResult<List<SysSwitchVO>> list(@RequestBody SysSwitchQueryDTO query) {
        List<SysSwitchVO> list = switchService.list(query);
        return ApiResult.success(list);
    }

    /**
     * 分页查询开关
     *
     * @param query 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询开关", description = "分页获取开关列表")
    @SaCheckPermission("system:switch:query")
    @PostMapping("/page")
    public ApiResult<Page<SysSwitchVO>> page(@RequestBody SysSwitchQueryDTO query) {
        Page<SysSwitchVO> page = switchService.page(query);
        return ApiResult.success(page);
    }

    /**
     * 根据分类获取开关
     *
     * @param category 开关分类
     * @return 开关列表
     */
    @Operation(summary = "根据分类获取开关", description = "获取指定分类下的所有开关")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/listByCategory")
    public ApiResult<List<SysSwitchVO>> listByCategory(
            @Parameter(description = "开关分类", required = true, example = "user")
            @RequestParam String category) {
        List<SysSwitchVO> list = switchService.listByCategory(category);
        return ApiResult.success(list);
    }

    /**
     * 根据类型获取开关
     *
     * @param switchType 开关类型
     * @return 开关列表
     */
    @Operation(summary = "根据类型获取开关", description = "获取指定类型的所有开关")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/listByType")
    public ApiResult<List<SysSwitchVO>> listBySwitchType(
            @Parameter(description = "开关类型", required = true, example = "feature")
            @RequestParam String switchType) {
        List<SysSwitchVO> list = switchService.listBySwitchType(switchType);
        return ApiResult.success(list);
    }

    /**
     * 获取所有开启的开关
     *
     * @return 开关列表
     */
    @Operation(summary = "获取所有开关", description = "获取所有开启的开关列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/listAll")
    public ApiResult<List<SysSwitchVO>> listAll() {
        List<SysSwitchVO> list = switchService.listAllEnabled();
        return ApiResult.success(list);
    }

    /**
     * 获取开关操作日志
     *
     * @param id 开关ID
     * @return 日志列表
     */
    @Operation(summary = "获取开关日志", description = "获取指定开关的操作日志")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/logs")
    public ApiResult<List<SysSwitchLogVO>> getLogs(
            @Parameter(description = "开关ID", required = true)
            @RequestParam Long id) {
        List<SysSwitchLogVO> logs = switchService.getLogsBySwitchId(id);
        return ApiResult.success(logs);
    }

    /**
     * 分页获取开关操作日志
     *
     * @param id       开关ID
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 分页结果
     */
    @Operation(summary = "分页获取开关日志", description = "分页获取指定开关的操作日志")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/logs/page")
    public ApiResult<Page<SysSwitchLogVO>> getLogPage(
            @Parameter(description = "开关ID", required = true)
            @RequestParam Long id,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysSwitchLogVO> page = switchService.getLogPage(id, pageNum, pageSize);
        return ApiResult.success(page);
    }

    /**
     * 刷新开关缓存
     *
     * @return 操作结果
     */
    @Operation(summary = "刷新开关缓存", description = "刷新系统开关的缓存")
    @SaCheckPermission("system:switch:refresh")
    @PostMapping("/refreshCache")
    public ApiResult<Void> refreshCache() {
        switchService.refreshCache();
        return ApiResult.success();
    }

    /**
     * 根据开关键获取开关详情
     *
     * @param switchKey 开关键
     * @return 开关详情
     */
    @Operation(summary = "根据键获取开关", description = "根据开关键获取开关信息")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/getByKey")
    public ApiResult<SysSwitchVO> getByKey(
            @Parameter(description = "开关键", required = true, example = "user.register.enabled")
            @RequestParam String switchKey) {
        var entity = switchService.getBySwitchKey(switchKey);
        if (entity == null) {
            return ApiResult.success(null);
        }
        return ApiResult.success(MapStructUtils.convert(entity, SysSwitchVO.class));
    }

    /**
     * 检查开关键是否存在
     *
     * @param switchKey 开关键
     * @param excludeId 排除的ID（更新时使用）
     * @return 是否存在
     */
    @Operation(summary = "检查开关键", description = "检查开关键是否已存在")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/checkKey")
    public ApiResult<Boolean> checkSwitchKey(
            @Parameter(description = "开关键", required = true)
            @RequestParam String switchKey,
            @Parameter(description = "排除的ID")
            @RequestParam(required = false) Long excludeId) {
        var entity = switchService.getBySwitchKey(switchKey);
        boolean exists = entity != null && (excludeId == null || !excludeId.equals(entity.getId()));
        return ApiResult.success(exists);
    }

    // ==================== 枚举接口 ====================

    /**
     * 获取开关分类枚举列表
     *
     * @return 分类枚举列表
     */
    @Operation(summary = "获取分类枚举", description = "获取开关分类的枚举列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/enums/categories")
    public ApiResult<List<Map<String, String>>> getCategoryEnums() {
        List<Map<String, String>> result = Arrays.stream(SwitchCategoryEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取开关类型枚举列表
     *
     * @return 类型枚举列表
     */
    @Operation(summary = "获取类型枚举", description = "获取开关类型的枚举列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/enums/types")
    public ApiResult<List<Map<String, String>>> getTypeEnums() {
        List<Map<String, String>> result = Arrays.stream(SwitchTypeEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取开关作用范围枚举列表
     *
     * @return 作用范围枚举列表
     */
    @Operation(summary = "获取作用范围枚举", description = "获取开关作用范围的枚举列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/enums/scopes")
    public ApiResult<List<Map<String, String>>> getScopeEnums() {
        List<Map<String, String>> result = Arrays.stream(SwitchScopeEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取开关生效策略枚举列表
     *
     * @return 生效策略枚举列表
     */
    @Operation(summary = "获取策略枚举", description = "获取开关生效策略的枚举列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/enums/strategies")
    public ApiResult<List<Map<String, String>>> getStrategyEnums() {
        List<Map<String, String>> result = Arrays.stream(SwitchStrategyEnum.values())
                .map(e -> Map.of("code", e.getCode(), "name", e.getName()))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    /**
     * 获取开关键枚举列表
     *
     * @return 开关键枚举列表
     */
    @Operation(summary = "获取开关键枚举", description = "获取系统预定义的开关键枚举列表")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/enums/keys")
    public ApiResult<List<Map<String, String>>> getSwitchKeyEnums() {
        List<Map<String, String>> result = Arrays.stream(SwitchKeyEnum.values())
                .map(e -> Map.of(
                        "enumName", e.name(),
                        "code", e.getCode(),
                        "name", e.getName()
                ))
                .collect(Collectors.toList());
        return ApiResult.success(result);
    }

    // ==================== 枚举对比 ====================

    /**
     * 对比枚举定义与数据库中的开关
     * <p>
     * 用于检查代码中定义的 SwitchKeyEnum 枚举与数据库中实际存在的开关的差异。
     * </p>
     *
     * @return 对比结果
     */
    @Operation(summary = "枚举对比", description = "对比 SwitchKeyEnum 枚举定义与数据库中的开关差异")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/compare")
    public ApiResult<SwitchEnumCompareVO> compareWithEnum() {
        // 获取所有枚举定义的开关键
        Set<String> enumKeys = Arrays.stream(SwitchKeyEnum.values())
                .map(SwitchKeyEnum::getKey)
                .collect(Collectors.toSet());

        // 获取数据库中所有开关键
        List<SysSwitchVO> dbSwitches = switchService.list(new SysSwitchQueryDTO());
        Set<String> dbKeys = dbSwitches.stream()
                .map(SysSwitchVO::getSwitchKey)
                .collect(Collectors.toSet());

        // 计算差异
        SwitchEnumCompareVO result = new SwitchEnumCompareVO();

        // 枚举中有但数据库没有的
        List<SwitchEnumCompareVO.EnumSwitchItem> missingInDb = Arrays.stream(SwitchKeyEnum.values())
                .filter(e -> !dbKeys.contains(e.getKey()))
                .map(e -> new SwitchEnumCompareVO.EnumSwitchItem(e.name(), e.getKey(), e.getName()))
                .collect(Collectors.toList());
        result.setMissingInDatabase(missingInDb);

        // 数据库有但枚举没有的
        List<String> missingInEnum = dbKeys.stream()
                .filter(key -> !enumKeys.contains(key))
                .sorted()
                .collect(Collectors.toList());
        result.setMissingInEnum(missingInEnum);

        // 同步的（两边都有的）
        List<String> synced = enumKeys.stream()
                .filter(dbKeys::contains)
                .sorted()
                .collect(Collectors.toList());
        result.setSynced(synced);

        // 统计数量
        result.setEnumCount(enumKeys.size());
        result.setDatabaseCount(dbKeys.size());

        return ApiResult.success(result);
    }

    /**
     * 一键删除枚举中不存在的开关
     * <p>
     * 删除数据库中存在但 SwitchKeyEnum 枚举中未定义的开关。
     * 用于清理废弃的开关配置。
     * </p>
     *
     * @return 删除的开关数量
     */
    @Operation(summary = "删除枚举外开关", description = "删除数据库中存在但枚举中未定义的开关")
    @SaCheckPermission("system:switch:delete")
    @DeleteMapping("/deleteNotInEnum")
    public ApiResult<Integer> deleteNotInEnum() {
        // 获取所有枚举定义的开关键
        Set<String> enumKeys = Arrays.stream(SwitchKeyEnum.values())
                .map(SwitchKeyEnum::getKey)
                .collect(Collectors.toSet());

        // 获取数据库中所有开关
        List<SysSwitchVO> dbSwitches = switchService.list(new SysSwitchQueryDTO());

        // 找出数据库有但枚举没有的开关ID
        List<Long> idsToDelete = dbSwitches.stream()
                .filter(sw -> !enumKeys.contains(sw.getSwitchKey()))
                .map(SysSwitchVO::getId)
                .collect(Collectors.toList());

        if (idsToDelete.isEmpty()) {
            return ApiResult.success(0);
        }

        // 批量删除
        switchService.deleteByIds(idsToDelete);
        return ApiResult.success(idsToDelete.size());
    }

    /**
     * 一键初始化枚举中的开关
     * <p>
     * 将 SwitchKeyEnum 枚举中定义但数据库不存在的开关批量创建到数据库。
     * </p>
     *
     * @return 创建的开关数量
     */
    @Operation(summary = "初始化枚举开关", description = "将枚举中定义但数据库不存在的开关批量创建")
    @SaCheckPermission("system:switch:create")
    @PostMapping("/initFromEnum")
    public ApiResult<Integer> initFromEnum() {
        // 获取数据库中所有开关键
        List<SysSwitchVO> dbSwitches = switchService.list(new SysSwitchQueryDTO());
        Set<String> dbKeys = dbSwitches.stream()
                .map(SysSwitchVO::getSwitchKey)
                .collect(Collectors.toSet());

        // 找出枚举中有但数据库没有的
        List<SwitchKeyEnum> toCreate = Arrays.stream(SwitchKeyEnum.values())
                .filter(e -> !dbKeys.contains(e.getKey()))
                .collect(Collectors.toList());

        if (toCreate.isEmpty()) {
            return ApiResult.success(0);
        }

        // 批量创建
        for (SwitchKeyEnum e : toCreate) {
            SysSwitchCreateDTO dto = new SysSwitchCreateDTO();
            dto.setSwitchKey(e.getKey());
            dto.setSwitchName(e.getName());
            dto.setSwitchValue(0); // 默认关闭
            dto.setCategory(extractCategory(e.getKey()));
            dto.setSwitchType("feature");
            dto.setDescription("从枚举自动创建");
            switchService.create(dto);
        }

        return ApiResult.success(toCreate.size());
    }

    /**
     * 从开关键提取分类
     */
    private String extractCategory(String switchKey) {
        if (switchKey == null || !switchKey.contains(".")) {
            return "other";
        }
        return switchKey.split("\\.")[0];
    }

    // ==================== 导入导出 ====================

    /**
     * 导出开关列表
     *
     * @param query    查询条件
     * @param response HTTP响应
     */
    @Operation(summary = "导出开关列表", description = "导出开关数据到Excel文件")
    @SaCheckPermission("system:switch:query")
    @PostMapping("/export")
    public void export(@RequestBody SysSwitchQueryDTO query, HttpServletResponse response) throws IOException {
        List<SysSwitchVO> list = switchService.list(query);
        List<SysSwitchExcelVO> excelList = MapStructUtils.convert(list, SysSwitchExcelVO.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("系统开关", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(excelList, "系统开关", SysSwitchExcelVO.class, response.getOutputStream());
    }

    /**
     * 导入开关
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @Operation(summary = "导入开关", description = "从Excel文件导入开关数据")
    @SaCheckPermission("system:switch:create")
    @PostMapping("/import")
    public ApiResult<String> importData(@RequestParam("file") MultipartFile file) throws IOException {
        ExcelResult<SysSwitchExcelVO> result = ExcelUtils.importExcel(file.getInputStream(), SysSwitchExcelVO.class, true);
        List<SysSwitchExcelVO> list = result.getList();
        if (list.isEmpty()) {
            return ApiResult.error(500, "导入数据为空");
        }
        for (SysSwitchExcelVO excel : list) {
            SysSwitchCreateDTO dto = MapStructUtils.convert(excel, SysSwitchCreateDTO.class);
            switchService.create(dto);
        }
        return ApiResult.success("导入成功，共导入 " + list.size() + " 条数据");
    }

    /**
     * 下载导入模板
     *
     * @param response HTTP响应
     */
    @Operation(summary = "下载导入模板", description = "下载开关导入的Excel模板")
    @SaCheckPermission("system:switch:query")
    @GetMapping("/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("开关导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        ExcelUtils.exportExcel(Collections.emptyList(), "开关导入模板", SysSwitchExcelVO.class, response.getOutputStream());
    }

}
