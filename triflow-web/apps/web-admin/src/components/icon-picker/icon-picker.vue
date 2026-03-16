<script lang="ts" setup>
import { computed } from 'vue';

import { IconifyIcon } from '@vben/icons';

import { ElInput } from 'element-plus';

import IconSelect from '#/components/icon-select/icon-select.vue';

interface Props {
  modelValue?: string;
  iconType?: 'iconfont' | 'image' | 'uiLib' | 'unocss';
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  iconType: 'unocss',
  placeholder: '请选择图标',
});

const emit = defineEmits<{
  change: [value: string];
  'update:modelValue': [value: string];
}>();

const isIconClass = computed(() =>
  ['iconfont', 'uiLib', 'unocss'].includes(props.iconType || 'unocss'),
);

function toIconify(value: string) {
  if (!value) {
    return '';
  }
  if (value.startsWith('i-')) {
    const raw = value.slice(2);
    const parts = raw.split('-');
    if (parts.length >= 2) {
      return `${parts[0]}:${parts.slice(1).join('-')}`;
    }
  }
  return value;
}

function toIconClass(value: string) {
  if (!value) {
    return '';
  }
  if (value.startsWith('i-')) {
    return value;
  }
  if (value.includes(':')) {
    return `i-${value.replace(':', '-')}`;
  }
  return value;
}

const iconifyValue = computed({
  get: () => {
    return isIconClass.value ? toIconify(props.modelValue) : props.modelValue;
  },
  set: (value: string) => {
    const output = isIconClass.value ? toIconClass(value) : value;
    emit('update:modelValue', output);
    emit('change', output);
  },
});

const previewIcon = computed(() => toIconify(props.modelValue));
</script>

<template>
  <div class="icon-picker">
    <IconSelect
      v-if="isIconClass"
      v-model="iconifyValue"
      :placeholder="placeholder"
    />
    <ElInput
      v-else
      v-model="iconifyValue"
      :placeholder="placeholder"
      clearable
    />

    <div v-if="previewIcon && isIconClass" class="icon-preview">
      <IconifyIcon :icon="previewIcon" class="icon-preview__icon" />
      <span class="icon-preview__text">{{ modelValue }}</span>
    </div>
  </div>
</template>

<style scoped>
.icon-picker {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.icon-preview {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.icon-preview__icon {
  font-size: 16px;
  color: var(--el-color-primary);
}
</style>
