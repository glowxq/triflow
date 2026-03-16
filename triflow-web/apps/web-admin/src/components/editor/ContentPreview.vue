<script setup lang="ts">
/**
 * 内容预览组件
 * @description 支持 HTML 和 Markdown 内容的预览
 */

import { MdPreview } from 'md-editor-v3';

import 'md-editor-v3/lib/preview.css';

interface Props {
  /** 内容 */
  content?: string;
  /** 内容类型：html | markdown | text */
  contentType?: 'html' | 'markdown' | 'text';
}

withDefaults(defineProps<Props>(), {
  content: '',
  contentType: 'html',
});
</script>

<template>
  <div class="content-preview">
    <!-- Markdown 预览 -->
    <MdPreview
      v-if="contentType === 'markdown'"
      :id="`preview-${Date.now()}`"
      :model-value="content"
      preview-theme="default"
      code-theme="atom"
    />

    <!-- HTML 预览 -->
    <div
      v-else-if="contentType === 'html'"
      class="html-preview"
      v-html="content"
    ></div>

    <!-- 纯文本预览 -->
    <pre v-else class="text-preview">{{ content }}</pre>
  </div>
</template>

<style scoped>
.content-preview {
  width: 100%;
  min-height: 200px;
  padding: 16px;
  overflow: auto;
  background-color: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.html-preview {
  line-height: 1.8;
}

.html-preview :deep(img) {
  max-width: 100%;
  height: auto;
}

.html-preview :deep(a) {
  color: var(--el-color-primary);
}

.html-preview :deep(code) {
  padding: 2px 6px;
  font-family: Consolas, Monaco, 'Courier New', monospace;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

.html-preview :deep(pre) {
  padding: 12px;
  overflow-x: auto;
  background-color: var(--el-fill-color-light);
  border-radius: 4px;
}

.html-preview :deep(blockquote) {
  padding: 8px 16px;
  margin: 0;
  background-color: var(--el-fill-color-lighter);
  border-left: 4px solid var(--el-border-color);
}

.html-preview :deep(table) {
  width: 100%;
  border-collapse: collapse;
}

.html-preview :deep(th),
.html-preview :deep(td) {
  padding: 8px 12px;
  border: 1px solid var(--el-border-color);
}

.html-preview :deep(th) {
  font-weight: 600;
  background-color: var(--el-fill-color-light);
}

.text-preview {
  margin: 0;
  font-family: inherit;
  line-height: 1.8;
  word-wrap: break-word;
  white-space: pre-wrap;
}
</style>
