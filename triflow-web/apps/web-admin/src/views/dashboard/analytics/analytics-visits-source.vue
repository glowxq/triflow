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

const colors = ['#019680', '#5ab1ef', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'];

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
      bottom: '2%',
      left: 'center',
    },
    series: [
      {
        animationDelay() {
          return Math.random() * 100;
        },
        animationEasing: 'exponentialInOut',
        animationType: 'scale',
        avoidLabelOverlap: false,
        center: ['50%', '45%'],
        data: chartData,
        emphasis: {
          label: {
            fontSize: '12',
            fontWeight: 'bold',
            show: true,
          },
        },
        itemStyle: {
          borderRadius: 10,
          borderWidth: 2,
        },
        label: {
          position: 'center',
          show: false,
        },
        labelLine: {
          show: false,
        },
        name: '交易类型',
        radius: ['40%', '65%'],
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
