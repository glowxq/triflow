<script lang="ts" setup>
import type { EnumItem } from '#/api/base/enum';

/**
 * 枚举下拉选择组件
 * @description 通用枚举选择器，自动加载指定枚举的选项
 * @example
 * <EnumSelect v-model="form.status" enum-class="StatusEnum" />
 * <EnumSelect v-model="form.type" enum-class="TypeEnum" placeholder="请选择类型" />
 */
import { onMounted, ref, watch } from 'vue';

import { ElOption, ElSelect } from 'element-plus';

import { getEnumOptions } from '#/api/base/enum';

interface Props {
  /** 枚举类简称（如 StatusEnum） */
  enumClass: string;
  /** 占位文本 */
  placeholder?: string;
  /** 是否可清除 */
  clearable?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 选择器大小 */
  size?: 'default' | 'large' | 'small';
  /** 是否多选 */
  multiple?: boolean;
  /** 是否可过滤 */
  filterable?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请选择',
  clearable: true,
  disabled: false,
  size: 'default',
  multiple: false,
  filterable: false,
});

const emit = defineEmits<{
  change: [value: string | string[]];
}>();

/** v-model 双向绑定 */
const modelValue = defineModel<string | string[]>();

/** 枚举选项列表 */
const options = ref<EnumItem[]>([]);

/** 是否正在加载 */
const loading = ref(false);

/** 加载错误信息 */
const errorMessage = ref('');

/**
 * 获取枚举选项
 */
async function fetchOptions() {
  if (!props.enumClass) {
    return;
  }

  loading.value = true;
  errorMessage.value = '';

  try {
    options.value = await getEnumOptions(props.enumClass);
  } catch (error: any) {
    errorMessage.value = error?.message || '加载枚举选项失败';
    console.error(`Failed to load enum options for ${props.enumClass}:`, error);
  } finally {
    loading.value = false;
  }
}

/**
 * 处理选择变更
 */
function handleChange(value: string | string[]) {
  emit('change', value);
}

// 组件挂载时加载选项
onMounted(fetchOptions);

// 监听枚举类名变化，重新加载选项
watch(() => props.enumClass, fetchOptions);
</script>

<template>
  <ElSelect
    v-model="modelValue"
    :placeholder="errorMessage || placeholder"
    :clearable="clearable"
    :disabled="disabled || loading"
    :size="size"
    :multiple="multiple"
    :filterable="filterable"
    :loading="loading"
    class="w-full"
    @change="handleChange"
  >
    <ElOption
      v-for="item in options"
      :key="item.code"
      :label="item.name"
      :value="item.code"
    />
  </ElSelect>
</template>
