<script setup lang="ts">
/**
 * 富文本编辑器组件
 * @description 基于 WangEditor 的富文本编辑器封装（手动 Vue 3 集成）
 */

import type {
  IDomEditor,
  IEditorConfig,
  IToolbarConfig,
} from '@wangeditor/editor';

import { onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue';

import { createEditor, createToolbar } from '@wangeditor/editor';

import '@wangeditor/editor/dist/css/style.css';

interface Props {
  /** 占位符文本 */
  placeholder?: string;
  /** 是否只读 */
  readonly?: boolean;
  /** 编辑器高度 */
  height?: string;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '请输入内容...',
  readonly: false,
  height: '400px',
});

const modelValue = defineModel<string>({ default: '' });

// DOM 引用
const toolbarRef = ref<HTMLElement>();
const editorRef = ref<HTMLElement>();

// 编辑器实例
const editor = shallowRef<IDomEditor>();

// 工具栏配置
const toolbarConfig: Partial<IToolbarConfig> = {
  excludeKeys: [
    'group-video', // 排除视频上传
    'fullScreen', // 排除全屏
  ],
};

// 编辑器配置
const editorConfig: Partial<IEditorConfig> = {
  placeholder: props.placeholder,
  readOnly: props.readonly,
  onChange: (editorInstance: IDomEditor) => {
    const html = editorInstance.getHtml();
    // 避免空内容时返回空标签
    modelValue.value = html === '<p><br></p>' || html === '<p></p>' ? '' : html;
  },
  MENU_CONF: {
    // 上传图片配置
    uploadImage: {
      // 使用 base64 格式（简单方案）
      base64LimitSize: 10 * 1024 * 1024, // 10MB 以下使用 base64
    },
  },
};

// 初始化编辑器
onMounted(() => {
  if (!editorRef.value || !toolbarRef.value) return;

  // 创建编辑器
  const editorInstance = createEditor({
    selector: editorRef.value,
    html: modelValue.value || '',
    config: editorConfig,
    mode: 'default',
  });

  // 创建工具栏
  createToolbar({
    editor: editorInstance,
    selector: toolbarRef.value,
    config: toolbarConfig,
    mode: 'default',
  });

  editor.value = editorInstance;
});

// 监听外部值变化
watch(
  () => modelValue.value,
  (newVal) => {
    if (editor.value && editor.value.getHtml() !== newVal) {
      editor.value.setHtml(newVal || '');
    }
  },
);

// 组件销毁时销毁编辑器
onBeforeUnmount(() => {
  if (editor.value) {
    editor.value.destroy();
  }
});

// 暴露方法
defineExpose({
  /** 获取编辑器实例 */
  getEditor: () => editor.value,
  /** 获取纯文本内容 */
  getText: () => editor.value?.getText() || '',
  /** 获取HTML内容 */
  getHtml: () => editor.value?.getHtml() || '',
  /** 设置HTML内容 */
  setHtml: (html: string) => editor.value?.setHtml(html),
  /** 清空内容 */
  clear: () => editor.value?.clear(),
  /** 聚焦 */
  focus: () => editor.value?.focus(),
});
</script>

<template>
  <div class="rich-text-editor">
    <div ref="toolbarRef" class="editor-toolbar"></div>
    <div ref="editorRef" class="editor-content" :style="{ height }"></div>
  </div>
</template>

<style scoped>
.rich-text-editor {
  overflow: hidden;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}

.editor-toolbar {
  border-bottom: 1px solid var(--el-border-color);
}

.editor-content {
  overflow-y: auto;
}

/* 深色模式适配 */
:deep(.w-e-text-container) {
  background-color: var(--el-fill-color-blank);
}

:deep(.w-e-toolbar) {
  background-color: var(--el-fill-color-light);
}

:deep(.w-e-text-placeholder) {
  font-style: normal;
  color: var(--el-text-color-placeholder);
}
</style>
