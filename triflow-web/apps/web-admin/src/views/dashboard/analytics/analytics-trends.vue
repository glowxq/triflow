<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';

import { ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import type { AnalyticsTrendVO } from '#/api/analytics';

interface Props {
  data?: AnalyticsTrendVO | null;
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  data: null,
  loading: false,
});

const chartRef = ref();
const { renderEcharts } = useEcharts(chartRef);

function renderChart() {
  if (!props.data) {
    return;
  }

  const { dates, userRegistrations, orderCounts, transactionAmounts } =
    props.data;

  // 计算交易金额的最大值以设置合理的Y轴
  const maxAmount = Math.max(...transactionAmounts, 1);
  const yAxisMax = Math.ceil(maxAmount * 1.2);

  renderEcharts({
    grid: {
      bottom: 30,
      containLabel: true,
      left: '1%',
      right: '1%',
      top: 40,
    },
    legend: {
      data: ['用户注册', '订单数量', '交易金额'],
      top: 0,
    },
    series: [
      {
        areaStyle: {
          opacity: 0.3,
        },
        data: userRegistrations,
        itemStyle: {
          color: '#5ab1ef',
        },
        name: '用户注册',
        smooth: true,
        type: 'line',
      },
      {
        areaStyle: {
          opacity: 0.3,
        },
        data: orderCounts,
        itemStyle: {
          color: '#019680',
        },
        name: '订单数量',
        smooth: true,
        type: 'line',
      },
      {
        areaStyle: {
          opacity: 0.3,
        },
        data: transactionAmounts,
        itemStyle: {
          color: '#f59e0b',
        },
        name: '交易金额',
        smooth: true,
        type: 'line',
        yAxisIndex: 1,
      },
    ],
    tooltip: {
      axisPointer: {
        lineStyle: {
          color: '#019680',
          width: 1,
        },
      },
      trigger: 'axis',
    },
    xAxis: {
      axisTick: {
        show: false,
      },
      boundaryGap: false,
      data: dates,
      splitLine: {
        lineStyle: {
          type: 'solid',
          width: 1,
        },
        show: true,
      },
      type: 'category',
    },
    yAxis: [
      {
        axisTick: {
          show: false,
        },
        name: '数量',
        splitArea: {
          show: true,
        },
        splitNumber: 4,
        type: 'value',
      },
      {
        axisTick: {
          show: false,
        },
        max: yAxisMax,
        name: '金额',
        position: 'right',
        splitLine: {
          show: false,
        },
        splitNumber: 4,
        type: 'value',
      },
    ],
  });
}

watch(
  () => props.data,
  () => {
    renderChart();
  },
  { immediate: true },
);
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
