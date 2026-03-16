/**
 * 数据分析 API
 */
import type {
  AnalyticsDistributionVO,
  AnalyticsOverviewVO,
  AnalyticsTrendVO,
} from './types';

import { requestClient } from '#/api/request';

/**
 * 获取概览数据
 */
export function getAnalyticsOverviewApi() {
  return requestClient.get<AnalyticsOverviewVO>('/base/admin/analytics/overview');
}

/**
 * 获取趋势数据
 * @param days 天数（7 或 30）
 */
export function getAnalyticsTrendsApi(days: number = 7) {
  return requestClient.get<AnalyticsTrendVO>('/base/admin/analytics/trends', {
    params: { days },
  });
}

/**
 * 获取分布数据
 */
export function getAnalyticsDistributionApi() {
  return requestClient.get<AnalyticsDistributionVO>('/base/admin/analytics/distribution');
}

export * from './types';
