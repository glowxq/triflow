package com.glowxq.triflow.base.api.admin.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.glowxq.common.core.common.annotation.OperationLog;
import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.common.core.common.enums.ModuleEnum;
import com.glowxq.triflow.base.ai.pojo.query.AiCallLogQuery;
import com.glowxq.triflow.base.ai.pojo.vo.AiCallLogVO;
import com.glowxq.triflow.base.ai.service.AiCallLogService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * AI 调用记录控制器
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Tag(name = "AI 调用记录")
@AdminApi
@RestController
@RequestMapping("/base/admin/ai/call-log")
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.AI)
public class AiCallLogController extends BaseApi {

    private final AiCallLogService aiCallLogService;

    @Operation(summary = "分页查询")
    @SaCheckPermission("ai:call-log:query")
    @GetMapping("/page")
    public ApiResult<Page<AiCallLogVO>> page(AiCallLogQuery query) {
        return ApiResult.success(aiCallLogService.page(query));
    }

    @Operation(summary = "获取详情")
    @SaCheckPermission("ai:call-log:query")
    @GetMapping("/{id}")
    public ApiResult<AiCallLogVO> getById(@PathVariable Long id) {
        return ApiResult.success(aiCallLogService.getById(id));
    }

    @Operation(summary = "删除记录")
    @SaCheckPermission("ai:call-log:delete")
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        aiCallLogService.delete(id);
        return ApiResult.success();
    }
}
