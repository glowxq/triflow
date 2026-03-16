<script setup lang="ts">
/**
 * 智能内容编辑器组件
 * @description 根据内容类型自动切换富文本/Markdown/纯文本编辑器
 */

import { ElInput } from 'element-plus';

import MarkdownEditor from './MarkdownEditor.vue';
import RichTextEditor from './RichTextEditor.vue';

interface Props {
  /** 内容类型：html | markdown | text */
  contentType?: 'html' | 'markdown' | 'text';
  /** 占位符文本 */
  placeholder?: string;
  /** 是否只读 */
  readonly?: boolean;
  /** 编辑器高度 */
  height?: string;
}

withDefaults(defineProps<Props>(), {
  contentType: 'html',
  placeholder: '请输入内容...',
  readonly: false,
  height: '400px',
});

const modelValue = defineModel<string>({ default: '' });
</script>

<template>
  <div class="content-editor">
    <!-- 富文本编辑器 -->
    <RichTextEditor
      v-if="contentType === 'html'"
      v-model="modelValue"
      :placeholder="placeholder"
      :readonly="readonly"
      :height="height"
    />

    <!-- Markdown 编辑器 -->
    <MarkdownEditor
      v-else-if="contentType === 'markdown'"
      v-model="modelValue"
      :placeholder="placeholder"
      :readonly="readonly"
      :height="height"
    />

    <!-- 纯文本编辑器 -->
    <ElInput
      v-else
      v-model="modelValue"
      type="textarea"
      :placeholder="placeholder"
      :readonly="readonly"
      :rows="15"
      resize="vertical"
    />
  </div>
</template>

<style scoped>
.content-editor {
  width: 100%;
}
</style>
