<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui';
import type { TabOption } from '@vben/types';

import { computed, onMounted, ref } from 'vue';

import {
  AnalysisChartCard,
  AnalysisChartsTabs,
  AnalysisOverview,
} from '@vben/common-ui';
import {
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
} from '@vben/icons';

import type {
  AnalyticsDistributionVO,
  AnalyticsOverviewVO,
  AnalyticsTrendVO,
} from '#/api/analytics';

import {
  getAnalyticsDistributionApi,
  getAnalyticsOverviewApi,
  getAnalyticsTrendsApi,
} from '#/api/analytics';

import AnalyticsTrends from './analytics-trends.vue';
import AnalyticsVisitsData from './analytics-visits-data.vue';
import AnalyticsVisitsSales from './analytics-visits-sales.vue';
import AnalyticsVisitsSource from './analytics-visits-source.vue';
import AnalyticsVisits from './analytics-visits.vue';

// 数据状态
const overview = ref<AnalyticsOverviewVO | null>(null);
const trends = ref<AnalyticsTrendVO | null>(null);
const distribution = ref<AnalyticsDistributionVO | null>(null);
const loading = ref(true);

// 概览数据转换为组件需要的格式
const overviewItems = computed<AnalysisOverviewItem[]>(() => {
  if (!overview.value) {
    return [];
  }
  return [
    {
      icon: SvgCardIcon,
      title: '今日新增用户',
      totalTitle: '总用户数',
      totalValue: overview.value.totalUsers,
      value: overview.value.todayNewUsers,
    },
    {
      icon: SvgCakeIcon,
      title: '今日订单',
      totalTitle: '总订单数',
      totalValue: overview.value.totalOrders,
      value: overview.value.todayOrders,
    },
    {
      icon: SvgDownloadIcon,
      title: '今日交易额',
      totalTitle: '总交易额',
      totalValue: overview.value.totalTransactionAmount,
      value: overview.value.todayTransactionAmount,
    },
    {
      icon: SvgBellIcon,
      title: '今日积分发放',
      totalTitle: '总积分发放',
      totalValue: overview.value.totalPointsIssued,
      value: overview.value.todayPointsIssued,
    },
    {
      icon: 'lucide:bot',
      title: '今日 AI 调用',
      totalTitle: '总 AI 调用',
      totalValue: overview.value.totalAiCalls,
      value: overview.value.todayAiCalls,
    },
    {
      icon: 'lucide:cpu',
      title: '今日 token 消耗',
      totalTitle: '总 token 消耗',
      totalValue: overview.value.totalAiTokens,
      value: overview.value.todayAiTokens,
    },
  ];
});

const chartTabs: TabOption[] = [
  {
    label: '数据趋势',
    value: 'trends',
  },
  {
    label: '月访问量',
    value: 'visits',
  },
];

// 加载数据
async function loadData() {
  loading.value = true;
  try {
    const [overviewRes, trendsRes, distributionRes] = await Promise.all([
      getAnalyticsOverviewApi(),
      getAnalyticsTrendsApi(7),
      getAnalyticsDistributionApi(),
    ]);
    overview.value = overviewRes;
    trends.value = trendsRes;
    distribution.value = distributionRes;
  } catch (error) {
    console.error('加载分析数据失败:', error);
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadData();
});
</script>

<template>
  <div class="p-5">
    <AnalysisOverview :items="overviewItems" :loading="loading" />
    <AnalysisChartsTabs :tabs="chartTabs" class="mt-5">
      <template #trends>
        <AnalyticsTrends :data="trends" :loading="loading" />
      </template>
      <template #visits>
        <AnalyticsVisits />
      </template>
    </AnalysisChartsTabs>

    <div class="mt-5 w-full md:flex">
      <AnalysisChartCard class="mt-5 md:mr-4 md:mt-0 md:w-1/3" title="用户角色分布">
        <AnalyticsVisitsData
          :data="distribution?.userRoleDistribution"
          :loading="loading"
        />
      </AnalysisChartCard>
      <AnalysisChartCard
        class="mt-5 md:mr-4 md:mt-0 md:w-1/3"
        title="交易类型分布"
      >
        <AnalyticsVisitsSource
          :data="distribution?.transactionTypeDistribution"
          :loading="loading"
        />
      </AnalysisChartCard>
      <AnalysisChartCard class="mt-5 md:mt-0 md:w-1/3" title="订单状态分布">
        <AnalyticsVisitsSales
          :data="distribution?.orderStatusDistribution"
          :loading="loading"
        />
      </AnalysisChartCard>
    </div>
  </div>
</template>
