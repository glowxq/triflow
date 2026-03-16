<script setup lang="ts">
/**
 * 状态选择器组件
 * @description 用于在表单中选择状态
 */

import { ElOption, ElSelect } from 'element-plus';

interface Props {
  /** 选择器占位符 */
  placeholder?: string;
  /** 是否可清除 */
  clearable?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 宽度 */
  width?: string;
  /** 启用选项文本 */
  enabledText?: string;
  /** 禁用选项文本 */
  disabledText?: string;
}

withDefaults(defineProps<Props>(), {
  placeholder: '请选择状态',
  clearable: true,
  disabled: false,
  width: '100%',
  enabledText: '正常',
  disabledText: '禁用',
});

const modelValue = defineModel<number | undefined>();
</script>

<template>
  <ElSelect
    v-model="modelValue"
    :placeholder="placeholder"
    :clearable="clearable"
    :disabled="disabled"
    :style="{ width }"
  >
    <ElOption :value="1" :label="enabledText" />
    <ElOption :value="0" :label="disabledText" />
  </ElSelect>
</template>
