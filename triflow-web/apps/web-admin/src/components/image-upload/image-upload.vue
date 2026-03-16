<script lang="ts" setup>
import type { FileInfoApi } from '#/api/file/types';

/**
 * 图片上传组件
 * @description 支持预览、删除、进度显示的图片上传组件
 * @example
 * <ImageUpload v-model="form.coverImage" />
 * <ImageUpload v-model="form.images" multiple :limit="5" />
 */
import { computed, ref } from 'vue';

import { ElImage, ElMessage, ElProgress, ElUpload } from 'element-plus';

import { directUpload } from '#/api/file/info';

interface Props {
  /** 是否多图模式 */
  multiple?: boolean;
  /** 最大上传数量（多图模式） */
  limit?: number;
  /** 最大文件大小（MB） */
  maxSize?: number;
  /** 接受的图片格式 */
  accept?: string;
  /** 是否禁用 */
  disabled?: boolean;
  /** 业务类型 */
  bizType?: string;
  /** 业务ID */
  bizId?: number;
  /** 图片宽度 */
  width?: string;
  /** 图片高度 */
  height?: string;
  /** 提示文字 */
  tip?: string;
}

const props = withDefaults(defineProps<Props>(), {
  multiple: false,
  limit: 9,
  maxSize: 10,
  accept: 'image/jpeg,image/png,image/gif,image/webp',
  disabled: false,
  bizType: '',
  bizId: undefined,
  width: '150px',
  height: '150px',
  tip: '',
});

const emit = defineEmits<{
  change: [value: null | string | string[]];
  success: [file: FileInfoApi.FileVO];
}>();

/** 单图模式：URL字符串；多图模式：URL数组 */
const modelValue = defineModel<null | string | string[]>();

/** 上传中的文件列表 */
interface UploadingFile {
  uid: string;
  name: string;
  progress: number;
  status: 'error' | 'success' | 'uploading';
}

const uploadingFiles = ref<UploadingFile[]>([]);

/** 预览 URL 缓存：URL -> previewUrl */
const previewUrlCache = ref<Map<string, string>>(new Map());

/** 已上传的图片列表 */
const imageList = computed<string[]>(() => {
  if (!modelValue.value) return [];
  if (Array.isArray(modelValue.value)) {
    return modelValue.value;
  }
  return modelValue.value ? [modelValue.value] : [];
});

/** 是否显示上传按钮 */
const showUploadButton = computed(() => {
  if (props.disabled) return false;
  if (!props.multiple) {
    return imageList.value.length === 0 && uploadingFiles.value.length === 0;
  }
  return imageList.value.length + uploadingFiles.value.length < props.limit;
});

/** 预览相关 */
const previewVisible = ref(false);
const previewUrl = ref('');

/**
 * 获取图片显示 URL（优先使用缓存的 previewUrl）
 */
function getDisplayUrl(url: string): string {
  return previewUrlCache.value.get(url) || url;
}

/**
 * 上传前校验
 */
function beforeUpload(file: File): boolean {
  // 检查格式
  const acceptTypes = props.accept.split(',').map((t) => t.trim());
  const isValidType = acceptTypes.some((type) => {
    if (type.includes('*')) {
      return file.type.startsWith(type.replace('*', ''));
    }
    return file.type === type;
  });

  if (!isValidType) {
    ElMessage.error(`只能上传 ${props.accept} 格式的图片`);
    return false;
  }

  // 检查大小
  const isValidSize = file.size / 1024 / 1024 <= props.maxSize;
  if (!isValidSize) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB`);
    return false;
  }

  return true;
}

/**
 * 处理自定义上传
 */
async function handleUpload(options: { file: File }) {
  const { file } = options;

  // 校验
  if (!beforeUpload(file)) return;

  // 创建上传中的文件记录
  const uid = `${Date.now()}-${Math.random().toString(36).slice(2)}`;
  const uploadingFile: UploadingFile = {
    uid,
    name: file.name,
    progress: 0,
    status: 'uploading',
  };
  uploadingFiles.value.push(uploadingFile);

  try {
    // 执行上传
    const result = await directUpload(file, {
      bizType: props.bizType || 'image',
      bizId: props.bizId,
      onProgress: (progress) => {
        const item = uploadingFiles.value.find((f) => f.uid === uid);
        if (item) {
          item.progress = progress;
        }
      },
    });

    // 上传成功
    uploadingFile.status = 'success';

    // 缓存 previewUrl
    if (result.previewUrl) {
      previewUrlCache.value.set(result.fileUrl, result.previewUrl);
    }

    // 更新 modelValue
    if (props.multiple) {
      const currentList = Array.isArray(modelValue.value)
        ? [...modelValue.value]
        : [];
      currentList.push(result.fileUrl);
      modelValue.value = currentList;
    } else {
      modelValue.value = result.fileUrl;
    }

    // 触发事件
    emit('change', modelValue.value);
    emit('success', result);

    // 移除上传中记录
    const index = uploadingFiles.value.findIndex((f) => f.uid === uid);
    if (index !== -1) {
      uploadingFiles.value.splice(index, 1);
    }

    ElMessage.success('上传成功');
  } catch (error: any) {
    uploadingFile.status = 'error';
    ElMessage.error(error?.message || '上传失败');

    // 移除失败的记录
    setTimeout(() => {
      const index = uploadingFiles.value.findIndex((f) => f.uid === uid);
      if (index !== -1) {
        uploadingFiles.value.splice(index, 1);
      }
    }, 2000);
  }
}

/**
 * 删除图片
 */
function handleRemove(url: string) {
  if (props.multiple) {
    const currentList = Array.isArray(modelValue.value)
      ? [...modelValue.value]
      : [];
    const index = currentList.indexOf(url);
    if (index !== -1) {
      currentList.splice(index, 1);
      modelValue.value = currentList.length > 0 ? currentList : null;
    }
  } else {
    modelValue.value = null;
  }
  // 清除缓存
  previewUrlCache.value.delete(url);
  emit('change', modelValue.value);
}

/**
 * 预览图片
 */
function handlePreview(url: string) {
  previewUrl.value = getDisplayUrl(url);
  previewVisible.value = true;
}

/**
 * 关闭预览
 */
function closePreview() {
  previewVisible.value = false;
}
</script>

<template>
  <div class="image-upload">
    <div class="image-upload-list flex flex-wrap gap-2">
      <!-- 已上传的图片 -->
      <div
        v-for="url in imageList"
        :key="url"
        class="image-upload-item group relative overflow-hidden rounded-lg border border-gray-200"
        :style="{ width, height }"
      >
        <ElImage
          :src="getDisplayUrl(url)"
          fit="cover"
          class="h-full w-full"
          lazy
        >
          <template #error>
            <div
              class="flex h-full w-full items-center justify-center bg-gray-100"
            >
              <span class="i-lucide-image-off text-2xl text-gray-400"></span>
            </div>
          </template>
        </ElImage>
        <!-- 操作遮罩 -->
        <div
          class="image-upload-actions absolute inset-0 flex items-center justify-center gap-4 bg-black/50 opacity-0 transition-opacity group-hover:opacity-100"
        >
          <span
            class="i-lucide-zoom-in cursor-pointer text-2xl text-white transition-colors hover:text-blue-400"
            title="预览"
            @click.stop="handlePreview(url)"
          ></span>
          <span
            v-if="!disabled"
            class="i-lucide-trash-2 cursor-pointer text-2xl text-white transition-colors hover:text-red-400"
            title="删除"
            @click.stop="handleRemove(url)"
          ></span>
        </div>
      </div>

      <!-- 上传中的图片 -->
      <div
        v-for="file in uploadingFiles"
        :key="file.uid"
        class="image-upload-item relative flex items-center justify-center overflow-hidden rounded-lg border border-dashed border-gray-300 bg-gray-50"
        :style="{ width, height }"
      >
        <div class="flex flex-col items-center">
          <ElProgress
            type="circle"
            :percentage="file.progress"
            :width="60"
            :status="file.status === 'error' ? 'exception' : undefined"
          />
          <span class="mt-1 max-w-full truncate px-2 text-xs text-gray-500">
            {{ file.name }}
          </span>
        </div>
      </div>

      <!-- 上传按钮 -->
      <ElUpload
        v-if="showUploadButton"
        :show-file-list="false"
        :accept="accept"
        :multiple="multiple"
        :http-request="handleUpload"
        :disabled="disabled"
        class="image-upload-trigger"
      >
        <div
          class="flex cursor-pointer flex-col items-center justify-center rounded-lg border border-dashed border-gray-300 bg-gray-50 transition-colors hover:border-blue-400 hover:bg-blue-50"
          :style="{ width, height }"
        >
          <span class="i-lucide-plus text-3xl text-gray-400"></span>
          <span class="mt-1 text-xs text-gray-500">上传图片</span>
        </div>
      </ElUpload>
    </div>

    <!-- 提示文字 -->
    <div v-if="tip" class="mt-2 text-xs text-gray-400">
      {{ tip }}
    </div>

    <!-- 图片预览弹窗 -->
    <Teleport to="body">
      <Transition name="fade">
        <div
          v-if="previewVisible"
          class="fixed inset-0 z-[2000] flex items-center justify-center bg-black/80"
          @click="closePreview"
        >
          <!-- 关闭按钮 -->
          <span
            class="i-lucide-x absolute right-4 top-4 cursor-pointer text-3xl text-white transition-colors hover:text-gray-300"
            @click="closePreview"
          ></span>
          <!-- 图片 -->
          <img
            :src="previewUrl"
            class="max-h-[90vh] max-w-[90vw] object-contain"
            @click.stop
          />
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<style scoped>
.image-upload-trigger :deep(.el-upload) {
  display: block;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
