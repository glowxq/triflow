<script setup lang="ts">
/**
 * 状态开关组件
 * @description 用于在表单中切换状态
 */

import { ElSwitch } from 'element-plus';

interface Props {
  /** 是否禁用 */
  disabled?: boolean;
  /** 启用时显示的文本 */
  activeText?: string;
  /** 禁用时显示的文本 */
  inactiveText?: string;
  /** 是否在开关上显示文字 */
  inlinePrompt?: boolean;
}

withDefaults(defineProps<Props>(), {
  disabled: false,
  activeText: '启用',
  inactiveText: '禁用',
  inlinePrompt: false,
});

const modelValue = defineModel<number>({ default: 1 });
</script>

<template>
  <ElSwitch
    v-model="modelValue"
    :active-value="1"
    :inactive-value="0"
    :active-text="activeText"
    :inactive-text="inactiveText"
    :inline-prompt="inlinePrompt"
    :disabled="disabled"
  />
</template>
