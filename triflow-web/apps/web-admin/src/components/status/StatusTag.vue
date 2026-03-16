<script setup lang="ts">
/**
 * 状态标签组件
 * @description 用于在表格中显示状态标签
 */

import { computed } from 'vue';

import { ElTag } from 'element-plus';

interface Props {
  /** 状态值（0: 禁用, 1: 启用） */
  status: number;
  /** 启用时显示的文本 */
  enabledText?: string;
  /** 禁用时显示的文本 */
  disabledText?: string;
  /** 标签大小 */
  size?: 'default' | 'large' | 'small';
}

const props = withDefaults(defineProps<Props>(), {
  enabledText: '正常',
  disabledText: '禁用',
  size: 'small',
});

const tagType = computed(() => (props.status === 1 ? 'success' : 'danger'));
const tagText = computed(() =>
  props.status === 1 ? props.enabledText : props.disabledText,
);
</script>

<template>
  <ElTag :type="tagType" :size="size">{{ tagText }}</ElTag>
</template>
