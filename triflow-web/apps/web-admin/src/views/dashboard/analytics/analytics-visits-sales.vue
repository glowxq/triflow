<script lang="ts" setup>
import { ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

import type { DistributionItem } from '#/api/analytics';

interface Props {
  data?: DistributionItem[] | null;
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  data: null,
  loading: false,
});

const chartRef = ref();
const { renderEcharts } = useEcharts(chartRef);

const colors = ['#67e0e3', '#5ab1ef', '#b6a2de', '#f59e0b', '#ef4444', '#2ec7c9'];

function renderChart() {
  if (!props.data || props.data.length === 0) {
    return;
  }

  const chartData = props.data
    .map((item, index) => ({
      itemStyle: {
        color: colors[index % colors.length],
      },
      name: item.name,
      value: item.value,
    }))
    .toSorted((a, b) => a.value - b.value);

  renderEcharts({
    series: [
      {
        animationDelay() {
          return Math.random() * 400;
        },
        animationEasing: 'exponentialInOut',
        animationType: 'scale',
        center: ['50%', '50%'],
        data: chartData,
        name: '订单状态',
        radius: '80%',
        roseType: 'radius',
        type: 'pie',
      },
    ],
    tooltip: {
      formatter: '{b}: {c} ({d}%)',
      trigger: 'item',
    },
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
