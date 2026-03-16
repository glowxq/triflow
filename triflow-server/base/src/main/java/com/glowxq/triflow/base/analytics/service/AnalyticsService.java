package com.glowxq.triflow.base.analytics.service;

import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsDistributionVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsOverviewVO;
import com.glowxq.triflow.base.analytics.pojo.vo.AnalyticsTrendVO;

/**
 * 数据分析服务接口
 *
 * @author glowxq
 * @since 2025-01-29
 */
public interface AnalyticsService {

    /**
     * 获取概览统计数据
     *
     * @return 概览数据
     */
    AnalyticsOverviewVO getOverview();

    /**
     * 获取趋势数据
     *
     * @param days 天数（7 或 30）
     * @return 趋势数据
     */
    AnalyticsTrendVO getTrends(int days);

    /**
     * 获取分布统计数据
     *
     * @return 分布数据
     */
    AnalyticsDistributionVO getDistribution();
}
