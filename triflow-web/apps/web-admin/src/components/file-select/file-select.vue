<script lang="ts" setup>
import type { FileInfoApi } from '#/api/file/types';

/**
 * 文件选择组件
 * @description 从已上传的文件中选择，支持分类筛选和预览
 * @example
 * <FileSelect v-model="form.fileId" />
 * <FileSelect v-model="form.fileIds" multiple />
 * <FileSelect v-model="form.imageId" file-type="image" />
 */
import { computed, onMounted, ref, watch } from 'vue';

import {
  ElButton,
  ElDialog,
  ElEmpty,
  ElImage,
  ElInput,
  ElOption,
  ElPagination,
  ElSelect,
  ElTag,
} from 'element-plus';

import { getFilePage } from '#/api/file/info';

interface Props {
  /** 文件类型筛选 (image, video, audio, document, other) */
  fileType?: string;
  /** 业务类型筛选 */
  bizType?: string;
  /** 是否多选 */
  multiple?: boolean;
  /** 是否禁用 */
  disabled?: boolean;
  /** 占位文本 */
  placeholder?: string;
  /** 对话框标题 */
  dialogTitle?: string;
  /** 每页显示数量 */
  pageSize?: number;
  /** 返回值类型：id 或 object */
  valueType?: 'id' | 'object';
}

const props = withDefaults(defineProps<Props>(), {
  fileType: '',
  bizType: '',
  multiple: false,
  disabled: false,
  placeholder: '选择文件',
  dialogTitle: '选择文件',
  pageSize: 12,
  valueType: 'id',
});

const emit = defineEmits<{
  change: [value: FileInfoApi.FileVO | FileInfoApi.FileVO[] | null];
}>();

/** v-model 双向绑定 */
const modelValue = defineModel<null | number | number[]>();

/** 对话框显示状态 */
const dialogVisible = ref(false);

/** 文件列表 */
const fileList = ref<FileInfoApi.FileVO[]>([]);

/** 总数 */
const total = ref(0);

/** 当前页 */
const currentPage = ref(1);

/** 加载状态 */
const loading = ref(false);

/** 搜索关键词 */
const searchKeyword = ref('');

/** 文件类型筛选 */
const filterFileType = ref('');

/** 业务类型筛选 */
const filterBizType = ref('');

/** 选中的文件 */
const selectedFiles = ref<FileInfoApi.FileVO[]>([]);

/** 已选文件的显示信息 */
const selectedDisplay = computed(() => {
  if (props.multiple) {
    return selectedFiles.value.map((f) => f.originalName).join(', ');
  }
  return selectedFiles.value[0]?.originalName || '';
});

/** 文件类型选项 */
const fileTypeOptions = [
  { label: '全部', value: '' },
  { label: '图片', value: 'image' },
  { label: '视频', value: 'video' },
  { label: '音频', value: 'audio' },
  { label: '文档', value: 'document' },
  { label: '其他', value: 'other' },
];

/**
 * 加载文件列表
 */
async function loadFiles() {
  loading.value = true;
  try {
    const result = await getFilePage({
      pageNum: currentPage.value,
      pageSize: props.pageSize,
      keyword: searchKeyword.value || undefined,
      category: filterFileType.value || props.fileType || undefined,
      bizType: filterBizType.value || props.bizType || undefined,
      status: 1,
    });
    fileList.value = result.records || [];
    total.value = result.totalRow || 0;
  } catch (error) {
    console.error('加载文件列表失败:', error);
    fileList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
}

/**
 * 打开对话框
 */
function openDialog() {
  if (props.disabled) return;
  dialogVisible.value = true;
  currentPage.value = 1;
  filterFileType.value = props.fileType;
  filterBizType.value = props.bizType;
  loadFiles();
}

/**
 * 关闭对话框
 */
function closeDialog() {
  dialogVisible.value = false;
}

/**
 * 判断文件是否被选中
 */
function isSelected(file: FileInfoApi.FileVO) {
  return selectedFiles.value.some((f) => f.id === file.id);
}

/**
 * 切换文件选择状态
 */
function toggleSelect(file: FileInfoApi.FileVO) {
  const index = selectedFiles.value.findIndex((f) => f.id === file.id);
  if (index === -1) {
    if (props.multiple) {
      selectedFiles.value.push(file);
    } else {
      selectedFiles.value = [file];
    }
  } else {
    selectedFiles.value.splice(index, 1);
  }
}

/**
 * 确认选择
 */
function confirmSelect() {
  if (props.valueType === 'id') {
    modelValue.value = props.multiple
      ? selectedFiles.value.map((f) => f.id)
      : selectedFiles.value[0]?.id || null;
  }

  if (props.multiple) {
    emit('change', [...selectedFiles.value]);
  } else {
    emit('change', selectedFiles.value[0] || null);
  }

  closeDialog();
}

/**
 * 清除选择
 */
function clearSelect() {
  selectedFiles.value = [];
  modelValue.value = props.multiple ? [] : null;
  emit('change', null);
}

/**
 * 获取文件图标
 */
function getFileIcon(file: FileInfoApi.FileVO): string {
  const ext = file.fileExt?.toLowerCase() || '';
  const type = file.fileType?.toLowerCase() || '';

  if (
    type === 'image' ||
    ['gif', 'jpeg', 'jpg', 'png', 'svg', 'webp'].includes(ext)
  ) {
    return 'i-carbon-image';
  }
  if (type === 'video' || ['avi', 'mov', 'mp4', 'wmv'].includes(ext)) {
    return 'i-carbon-video';
  }
  if (type === 'audio' || ['aac', 'flac', 'mp3', 'wav'].includes(ext)) {
    return 'i-carbon-music';
  }
  if (['pdf'].includes(ext)) {
    return 'i-carbon-document-pdf';
  }
  if (['doc', 'docx'].includes(ext)) {
    return 'i-carbon-document';
  }
  if (['xls', 'xlsx'].includes(ext)) {
    return 'i-carbon-table-split';
  }
  return 'i-carbon-document-blank';
}

/**
 * 判断是否是图片文件
 */
function isImageFile(file: FileInfoApi.FileVO): boolean {
  const ext = file.fileExt?.toLowerCase() || '';
  const type = file.fileType?.toLowerCase() || '';
  return (
    type === 'image' ||
    ['gif', 'jpeg', 'jpg', 'png', 'svg', 'webp'].includes(ext)
  );
}

/**
 * 格式化文件大小
 */
function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${Number.parseFloat((bytes / k ** i).toFixed(2))} ${sizes[i]}`;
}

/**
 * 处理搜索
 */
function handleSearch() {
  currentPage.value = 1;
  loadFiles();
}

/**
 * 处理分页
 */
function handlePageChange(page: number) {
  currentPage.value = page;
  loadFiles();
}

// 监听 modelValue 变化，同步选中状态
watch(
  () => modelValue.value,
  async (newVal) => {
    if (!newVal || (Array.isArray(newVal) && newVal.length === 0)) {
      selectedFiles.value = [];
      return;
    }

    // 如果已有选中的文件包含当前值，则不需要重新加载
    const ids = Array.isArray(newVal) ? newVal : [newVal];
    const hasAll = ids.every((id) =>
      selectedFiles.value.some((f) => f.id === id),
    );
    if (!hasAll) {
      // TODO: 根据 ID 加载文件详情
      // 可以调用 getFileById 或者在对话框打开时同步
    }
  },
  { immediate: true },
);

// 组件挂载时预加载
onMounted(() => {
  // 如果有初始值且需要显示，可以在这里加载文件详情
});
</script>

<template>
  <div class="file-select">
    <!-- 选择按钮 -->
    <div class="file-select-input flex items-center gap-2">
      <ElInput
        :model-value="selectedDisplay"
        :placeholder="placeholder"
        :disabled="disabled"
        readonly
        class="flex-1 cursor-pointer"
        @click="openDialog"
      >
        <template #suffix>
          <span
            v-if="selectedDisplay"
            class="i-carbon-close cursor-pointer text-gray-400 hover:text-gray-600"
            @click.stop="clearSelect"
          ></span>
          <span
            v-else
            class="i-carbon-folder cursor-pointer text-gray-400"
          ></span>
        </template>
      </ElInput>
    </div>

    <!-- 选择对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <!-- 筛选区域 -->
      <div class="mb-4 flex items-center gap-3">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索文件名"
          clearable
          class="w-48"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <span class="i-carbon-search"></span>
          </template>
        </ElInput>

        <ElSelect
          v-model="filterFileType"
          placeholder="文件类型"
          clearable
          class="w-32"
          @change="handleSearch"
        >
          <ElOption
            v-for="opt in fileTypeOptions"
            :key="opt.value"
            :label="opt.label"
            :value="opt.value"
          />
        </ElSelect>

        <ElButton type="primary" @click="handleSearch">搜索</ElButton>
      </div>

      <!-- 已选文件标签 -->
      <div v-if="selectedFiles.length > 0" class="mb-4 flex flex-wrap gap-2">
        <ElTag
          v-for="file in selectedFiles"
          :key="file.id"
          closable
          @close="toggleSelect(file)"
        >
          {{ file.originalName }}
        </ElTag>
      </div>

      <!-- 文件列表 -->
      <div v-loading="loading" class="file-grid min-h-64">
        <ElEmpty
          v-if="fileList.length === 0 && !loading"
          description="暂无文件"
        />

        <div v-else class="grid grid-cols-4 gap-4">
          <div
            v-for="file in fileList"
            :key="file.id"
            class="file-card cursor-pointer rounded-lg border-2 p-3 transition-all hover:shadow-md"
            :class="[
              isSelected(file)
                ? 'border-blue-500 bg-blue-50'
                : 'border-gray-200 hover:border-blue-300',
            ]"
            @click="toggleSelect(file)"
          >
            <!-- 图片预览 -->
            <div
              class="file-preview mb-2 flex h-24 items-center justify-center overflow-hidden rounded bg-gray-100"
            >
              <ElImage
                v-if="isImageFile(file)"
                :src="file.fileUrl"
                fit="contain"
                class="h-full w-full"
                lazy
              >
                <template #error>
                  <span
                    :class="getFileIcon(file)"
                    class="text-3xl text-gray-400"
                  ></span>
                </template>
              </ElImage>
              <span
                v-else
                :class="getFileIcon(file)"
                class="text-3xl text-gray-400"
              ></span>
            </div>

            <!-- 文件信息 -->
            <div class="file-info">
              <div
                class="mb-1 truncate text-sm font-medium"
                :title="file.originalName"
              >
                {{ file.originalName }}
              </div>
              <div class="text-xs text-gray-500">
                {{ formatSize(file.fileSize) }}
              </div>
            </div>

            <!-- 选中标记 -->
            <div
              v-if="isSelected(file)"
              class="absolute right-2 top-2 flex h-5 w-5 items-center justify-center rounded-full bg-blue-500 text-white"
            >
              <span class="i-carbon-checkmark text-xs"></span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="mt-4 flex justify-center">
        <ElPagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>

      <!-- 底部按钮 -->
      <template #footer>
        <ElButton @click="closeDialog">取消</ElButton>
        <ElButton
          type="primary"
          :disabled="selectedFiles.length === 0"
          @click="confirmSelect"
        >
          确定 {{ selectedFiles.length > 0 ? `(${selectedFiles.length})` : '' }}
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style scoped>
.file-card {
  position: relative;
}
</style>
