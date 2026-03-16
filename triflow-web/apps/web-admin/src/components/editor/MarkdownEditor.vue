<script setup lang="ts">
/**
 * Markdown 编辑器组件
 * @description 基于 md-editor-v3 的 Markdown 编辑器封装
 */

import { computed, ref } from 'vue';

import { MdEditor } from 'md-editor-v3';

import 'md-editor-v3/lib/style.css';

interface Props {
  /** 占位符文本 */
  placeholder?: string;
  /** 是否只读 */
  readonly?: boolean;
  /** 编辑器高度 */
  height?: string;
  /** 预览模式：'edit' | 'preview' | 'both' */
  previewMode?: 'both' | 'edit' | 'preview';
  /** 是否显示代码行号 */
  showCodeRowNumber?: boolean;
}

withDefaults(defineProps<Props>(), {
  placeholder: '请输入 Markdown 内容...',
  readonly: false,
  height: '450px',
  previewMode: 'both',
  showCodeRowNumber: true,
});

const modelValue = defineModel<string>({ default: '' });

// 编辑器 ID
const editorId = ref(`md-editor-${Date.now()}`);

// 计算预览主题
const previewTheme = computed(() => {
  // 可以根据系统主题自动切换
  return 'default';
});

// 计算代码主题
const codeTheme = computed(() => {
  // 可以根据系统主题自动切换
  return 'atom';
});

// 工具栏配置
const toolbars: string[] = [
  'bold',
  'underline',
  'italic',
  'strikeThrough',
  '-',
  'title',
  'sub',
  'sup',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  '-',
  'codeRow',
  'code',
  'link',
  'image',
  'table',
  'mermaid',
  'katex',
  '-',
  'revoke',
  'next',
  '=',
  'pageFullscreen',
  'preview',
  'previewOnly',
  'catalog',
];

// 内容变化回调
function handleChange(value: string) {
  modelValue.value = value;
}

// 图片上传回调（可以根据项目实际情况配置）
async function handleUploadImg(
  files: File[],
  callback: (urls: string[]) => void,
) {
  // 简单方案：使用 base64
  const urls = await Promise.all(
    files.map((file) => {
      return new Promise<string>((resolve) => {
        const reader = new FileReader();
        reader.addEventListener('load', () => resolve(reader.result as string));
        reader.readAsDataURL(file);
      });
    }),
  );
  callback(urls);
}

// 暴露方法
defineExpose({
  /** 获取 Markdown 内容 */
  getValue: () => modelValue.value,
  /** 设置 Markdown 内容 */
  setValue: (value: string) => {
    modelValue.value = value;
  },
  /** 清空内容 */
  clear: () => {
    modelValue.value = '';
  },
});
</script>

<template>
  <div class="markdown-editor">
    <MdEditor
      :id="editorId"
      v-model="modelValue"
      :placeholder="placeholder"
      :preview="!readonly"
      :preview-only="readonly"
      :preview-theme="previewTheme"
      :code-theme="codeTheme"
      :show-code-row-number="showCodeRowNumber"
      :toolbars="readonly ? [] : toolbars"
      :style="{ height }"
      @change="handleChange"
      @on-upload-img="handleUploadImg"
    />
  </div>
</template>

<style scoped>
.markdown-editor {
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

/* 深色模式适配 */
:deep(.md-editor) {
  --md-bk-color: var(--el-fill-color-blank);
}
</style>
