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

const colors = ['#5ab1ef', '#b6a2de', '#67e0e3', '#2ec7c9', '#ffb980', '#d87a80'];

function renderChart() {
  if (!props.data || props.data.length === 0) {
    return;
  }

  const chartData = props.data.map((item, index) => ({
    itemStyle: {
      color: colors[index % colors.length],
    },
    name: item.name,
    value: item.value,
  }));

  renderEcharts({
    legend: {
      bottom: 0,
      type: 'scroll',
    },
    series: [
      {
        center: ['50%', '45%'],
        data: chartData,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowColor: 'rgba(0, 0, 0, 0.5)',
            shadowOffsetX: 0,
          },
        },
        itemStyle: {
          borderRadius: 10,
        },
        label: {
          formatter: '{b}: {c}',
          show: true,
        },
        radius: ['40%', '70%'],
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
