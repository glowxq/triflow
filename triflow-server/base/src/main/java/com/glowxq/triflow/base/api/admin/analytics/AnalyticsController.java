package com.glowxq.triflow.base.api.admin.analytics;

import com.glowxq.common.core.common.api.BaseApi;
import com.glowxq.common.core.common.entity.ApiResult;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsDistributionVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsOverviewVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsTrendVO;
import com.glowxq.triflow.base.analytics.service.AnalyticsService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.glowxq.common.core.common.annotation.AdminApi;
/**
 * 数据分析控制器
 *
 * @author glowxq
 * @since 2025-01-29
 */
@Tag(name = "数据分析")
@AdminApi
@RestController
@RequestMapping("/base/admin/analytics")
@RequiredArgsConstructor
public class AnalyticsController extends BaseApi {

    private final AnalyticsService analyticsService;

    @Operation(summary = "获取概览数据")
    @SaCheckPermission("analytics:dashboard:query")
    @GetMapping("/overview")
    public ApiResult<AnalyticsOverviewVO> getOverview() {
        return ApiResult.success(analyticsService.getOverview());
    }

    @Operation(summary = "获取趋势数据")
    @SaCheckPermission("analytics:dashboard:query")
    @GetMapping("/trends")
    public ApiResult<AnalyticsTrendVO> getTrends(
            @Parameter(description = "天数，默认7天") @RequestParam(defaultValue = "7") int days) {
        return ApiResult.success(analyticsService.getTrends(days));
    }

    @Operation(summary = "获取分布数据")
    @SaCheckPermission("analytics:dashboard:query")
    @GetMapping("/distribution")
    public ApiResult<AnalyticsDistributionVO> getDistribution() {
        return ApiResult.success(analyticsService.getDistribution());
    }
}
