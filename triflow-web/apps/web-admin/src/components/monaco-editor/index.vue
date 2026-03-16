<script lang="ts" setup>
import type * as Monaco from 'monaco-editor';

import { onBeforeUnmount, onMounted, ref, shallowRef, watch } from 'vue';

import loader from '@monaco-editor/loader';

interface Props {
  /** 编辑器语言 */
  language?: string;
  /** 是否只读 */
  readonly?: boolean;
  /** 编辑器高度 */
  height?: number | string;
  /** 编辑器主题 */
  theme?: 'hc-black' | 'vs' | 'vs-dark';
  /** 占位符 */
  placeholder?: string;
}

const props = withDefaults(defineProps<Props>(), {
  language: 'json',
  readonly: false,
  height: 200,
  theme: 'vs',
  placeholder: '',
});

const emit = defineEmits<{
  (e: 'blur'): void;
  (e: 'focus'): void;
}>();

const modelValue = defineModel<string>({ default: '' });

const containerRef = ref<HTMLDivElement>();
const editorInstance = shallowRef<Monaco.editor.IStandaloneCodeEditor>();
const monacoRef = shallowRef<typeof Monaco>();
const isLoading = ref(true);

// 初始化编辑器
async function initEditor() {
  if (!containerRef.value) return;

  try {
    // 配置 Monaco CDN 源（使用 jsDelivr）
    loader.config({
      paths: {
        vs: 'https://cdn.jsdelivr.net/npm/monaco-editor@0.55.1/min/vs',
      },
    });

    const monaco = await loader.init();
    monacoRef.value = monaco;

    // 创建编辑器实例
    editorInstance.value = monaco.editor.create(containerRef.value, {
      value: modelValue.value,
      language: props.language,
      theme: props.theme,
      readOnly: props.readonly,
      minimap: { enabled: false },
      scrollBeyondLastLine: false,
      lineNumbers: 'on',
      fontSize: 13,
      tabSize: 2,
      automaticLayout: true,
      formatOnPaste: true,
      formatOnType: true,
      scrollbar: {
        vertical: 'auto',
        horizontal: 'auto',
        verticalScrollbarSize: 8,
        horizontalScrollbarSize: 8,
      },
      wordWrap: 'on',
      folding: true,
      lineDecorationsWidth: 8,
      renderLineHighlight: 'line',
    });

    // 监听内容变化
    editorInstance.value.onDidChangeModelContent(() => {
      const value = editorInstance.value?.getValue() || '';
      modelValue.value = value;
    });

    // 监听焦点事件
    editorInstance.value.onDidFocusEditorText(() => {
      emit('focus');
    });

    editorInstance.value.onDidBlurEditorText(() => {
      emit('blur');
    });

    isLoading.value = false;
  } catch (error) {
    console.error('Monaco Editor 加载失败:', error);
    isLoading.value = false;
  }
}

// 格式化代码
function formatCode() {
  editorInstance.value?.getAction('editor.action.formatDocument')?.run();
}

// 监听外部值变化
watch(
  () => modelValue.value,
  (newValue) => {
    const editor = editorInstance.value;
    if (editor && editor.getValue() !== newValue) {
      editor.setValue(newValue || '');
    }
  },
);

// 监听语言变化
watch(
  () => props.language,
  (newLanguage) => {
    const model = editorInstance.value?.getModel();
    if (model && monacoRef.value) {
      monacoRef.value.editor.setModelLanguage(model, newLanguage);
    }
  },
);

// 监听主题变化
watch(
  () => props.theme,
  (newTheme) => {
    monacoRef.value?.editor.setTheme(newTheme);
  },
);

// 监听只读状态变化
watch(
  () => props.readonly,
  (newReadonly) => {
    editorInstance.value?.updateOptions({ readOnly: newReadonly });
  },
);

onMounted(() => {
  initEditor();
});

onBeforeUnmount(() => {
  editorInstance.value?.dispose();
});

defineExpose({
  formatCode,
  getEditor: () => editorInstance.value,
});
</script>

<template>
  <div class="monaco-editor-wrapper">
    <div
      v-if="isLoading"
      class="flex items-center justify-center"
      :style="{ height: typeof height === 'number' ? `${height}px` : height }"
    >
      <span class="text-gray-400">编辑器加载中...</span>
    </div>
    <div
      ref="containerRef"
      class="monaco-editor-container"
      :style="{
        height: typeof height === 'number' ? `${height}px` : height,
        display: isLoading ? 'none' : 'block',
      }"
    ></div>
  </div>
</template>

<style scoped>
.monaco-editor-wrapper {
  overflow: hidden;
  border: 1px solid var(--el-border-color, #dcdfe6);
  border-radius: 4px;
}

.monaco-editor-container {
  width: 100%;
}
</style>
