package com.glowxq.triflow.base.api.admin.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.mybatisflex.core.paginate.Page;
import com.glowxq.triflow.base.ai.pojo.dto.AiConfigSaveDTO;
import com.glowxq.triflow.base.ai.pojo.dto.PromptTemplateSaveDTO;
import com.glowxq.triflow.base.ai.pojo.query.PromptTemplateQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiConfigVO;
import com.glowxq.triflow.base.ai.pojo.vo.PromptTemplateVO;
import com.glowxq.triflow.base.ai.service.SysAiConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * AI 配置管理接口
 *
 * @author glowxq
 * @since 2025-01-28
 */
@Slf4j
@AdminApi
@RestController
@RequestMapping("/base/admin/ai/config")
@RequiredArgsConstructor
@Tag(name = "AI 配置管理", description = "AI 配置和 Prompt 模板管理接口")
@OperationLog(module = ModuleEnum.AI)
public class SysAiConfigController extends BaseApi {

    private final SysAiConfigService aiConfigService;

    // ==================== AI 配置 ====================

    @SaCheckPermission("ai:config:query")
    @GetMapping("/config/list")
    @Operation(summary = "获取 AI 配置列表", description = "返回所有 AI 配置")
    public ApiResult<List<AiConfigVO>> getConfigList() {
        return ApiResult.success(aiConfigService.getConfigList());
    }

    @SaCheckPermission("ai:config:query")
    @GetMapping("/config/{id}")
    @Operation(summary = "获取 AI 配置详情")
    public ApiResult<AiConfigVO> getConfig(@PathVariable Long id) {
        return ApiResult.success(aiConfigService.getConfig(id));
    }

    @SaCheckPermission("ai:config:update")
    @PostMapping("/config/save")
    @Operation(summary = "保存 AI 配置", description = "新增或更新 AI 配置")
    public ApiResult<Void> saveConfig(@Valid @RequestBody AiConfigSaveDTO dto) {
        aiConfigService.saveConfig(dto);
        return ApiResult.success();
    }

    @SaCheckPermission("ai:config:update")
    @DeleteMapping("/config/{id}")
    @Operation(summary = "删除 AI 配置")
    public ApiResult<Void> deleteConfig(@PathVariable Long id) {
        aiConfigService.deleteConfig(id);
        return ApiResult.success();
    }

    @SaCheckPermission("ai:config:update")
    @PostMapping("/config/{id}/set-default")
    @Operation(summary = "设置默认提供商")
    public ApiResult<Void> setDefaultProvider(@PathVariable Long id) {
        aiConfigService.setDefaultProvider(id);
        return ApiResult.success();
    }

    @SaCheckPermission("ai:config:test")
    @PostMapping("/config/{id}/test")
    @Operation(summary = "测试 AI 配置")
    public ApiResult<String> testConfig(@PathVariable Long id) {
        return ApiResult.success(aiConfigService.testConfig(id));
    }

    // ==================== Prompt 模板 ====================

    @SaCheckPermission("ai:prompt:query")
    @GetMapping("/prompt/page")
    @Operation(summary = "分页查询 Prompt 模板")
    public ApiResult<Page<PromptTemplateVO>> pagePromptTemplate(PromptTemplateQuery query) {
        return ApiResult.success(aiConfigService.pagePromptTemplate(query));
    }

    @SaCheckPermission("ai:prompt:query")
    @GetMapping("/prompt/{id}")
    @Operation(summary = "获取 Prompt 模板详情")
    public ApiResult<PromptTemplateVO> getPromptTemplate(@PathVariable Long id) {
        return ApiResult.success(aiConfigService.getPromptTemplate(id));
    }

    @SaCheckPermission("ai:prompt:query")
    @GetMapping("/prompt/code/{code}")
    @Operation(summary = "根据代码获取 Prompt 模板")
    public ApiResult<PromptTemplateVO> getPromptTemplateByCode(@PathVariable String code) {
        return ApiResult.success(aiConfigService.getPromptTemplateByCode(code));
    }

    @SaCheckPermission("ai:prompt:create")
    @PostMapping("/prompt/save")
    @Operation(summary = "保存 Prompt 模板", description = "新增或更新 Prompt 模板")
    public ApiResult<Void> savePromptTemplate(@Valid @RequestBody PromptTemplateSaveDTO dto) {
        aiConfigService.savePromptTemplate(dto);
        return ApiResult.success();
    }

    @SaCheckPermission("ai:prompt:delete")
    @DeleteMapping("/prompt/{id}")
    @Operation(summary = "删除 Prompt 模板")
    public ApiResult<Void> deletePromptTemplate(@PathVariable Long id) {
        aiConfigService.deletePromptTemplate(id);
        return ApiResult.success();
    }

    @SaCheckPermission("ai:prompt:query")
    @GetMapping("/prompt/categories")
    @Operation(summary = "获取 Prompt 分类列表")
    public ApiResult<List<String>> getPromptCategories() {
        return ApiResult.success(aiConfigService.getPromptCategories());
    }

    @SaCheckPermission("ai:prompt:query")
    @PostMapping("/prompt/{id}/test")
    @Operation(summary = "测试 Prompt 模板")
    public ApiResult<String> testPromptTemplate(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> variables
    ) {
        return ApiResult.success(aiConfigService.testPromptTemplate(id, variables));
    }
}
