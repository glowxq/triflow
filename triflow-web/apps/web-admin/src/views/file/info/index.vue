<script lang="ts" setup>
import type { FileInfoApi } from '#/api/file';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElImage,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
  ElUpload,
} from 'element-plus';

import {
  deleteFile,
  deleteFileBatch,
  directUpload,
  getFilePage,
  getPreviewUrl,
  physicalDeleteFile,
  physicalDeleteFileBatch,
} from '#/api/file';

// ==================== 工具函数 ====================

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return `${Number.parseFloat((bytes / k ** i).toFixed(2))} ${sizes[i]}`;
}

function isImage(fileType?: string): boolean {
  // 支持 MIME 类型格式（image/jpeg）和简单类型格式（image）
  return fileType?.startsWith('image/') || fileType === 'image' || false;
}

// ==================== 状态定义 ====================

const tableData = ref<FileInfoApi.FileVO[]>([]);
const loading = ref(false);
const selectedRows = ref<FileInfoApi.FileVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<FileInfoApi.QueryParams>({
  keyword: '',
  category: undefined,
  status: undefined,
});

const uploadDialogVisible = ref(false);
const uploadLoading = ref(false);
const uploadProgress = ref(0);
const fileList = ref<any[]>([]);

const previewDialogVisible = ref(false);
const previewUrl = ref('');
const previewFileName = ref('');

// ==================== 常量 ====================

const statusOptions = [
  { label: '永久', value: 1, type: 'success' as const },
  { label: '临时', value: 0, type: 'warning' as const },
];

const fileTypeOptions = [
  { label: '图片', value: 'image', color: '#67c23a' },
  { label: '文档', value: 'document', color: '#409eff' },
  { label: '视频', value: 'video', color: '#e6a23c' },
  { label: '音频', value: 'audio', color: '#f56c6c' },
  { label: '压缩包', value: 'archive', color: '#909399' },
  { label: '其他', value: 'other', color: '#606266' },
];

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getFilePage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取文件列表失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.category = undefined;
  searchForm.status = undefined;
  pagination.pageNum = 1;
  fetchData();
}

function handleSizeChange(size: number) {
  pagination.pageSize = size;
  pagination.pageNum = 1;
  fetchData();
}

function handleCurrentChange(page: number) {
  pagination.pageNum = page;
  fetchData();
}

function handleSelectionChange(rows: FileInfoApi.FileVO[]) {
  selectedRows.value = rows;
}

function handleUpload() {
  fileList.value = [];
  uploadDialogVisible.value = true;
}

function handleUploadChange() {
  // 文件选择变化时的回调，不做自动上传
}

async function submitUpload() {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的文件');
    return;
  }

  uploadLoading.value = true;
  uploadProgress.value = 0;

  try {
    const totalFiles = fileList.value.length;
    let completed = 0;

    for (const file of fileList.value) {
      await directUpload(file.raw, {
        onProgress: (progress) => {
          // 计算总体进度：已完成文件 + 当前文件进度
          uploadProgress.value = Math.round(
            ((completed + progress / 100) / totalFiles) * 100,
          );
        },
      });
      completed++;
      uploadProgress.value = Math.round((completed / totalFiles) * 100);
    }

    ElMessage.success('上传成功');
    uploadDialogVisible.value = false;
    fileList.value = [];
    fetchData();
  } catch (error: any) {
    ElMessage.error(error?.message || '上传失败');
  } finally {
    uploadLoading.value = false;
    uploadProgress.value = 0;
  }
}

async function handlePreview(row: FileInfoApi.FileVO) {
  if (isImage(row.fileType)) {
    try {
      const url = await getPreviewUrl(row.id);
      previewUrl.value = url;
      previewFileName.value = row.originalName;
      previewDialogVisible.value = true;
    } catch {
      ElMessage.error('获取预览链接失败');
    }
  } else {
    // 非图片文件，直接打开链接
    window.open(row.fileUrl, '_blank');
  }
}

async function handleDownload(row: FileInfoApi.FileVO) {
  try {
    ElMessage.info('正在获取下载链接...');
    const url = await getPreviewUrl(row.id);
    // 创建隐藏的下载链接并触发下载
    const link = document.createElement('a');
    link.href = url;
    link.download = row.originalName;
    link.target = '_blank';
    document.body.append(link);
    link.click();
    link.remove();
  } catch {
    ElMessage.error('获取下载链接失败');
  }
}

async function handleDelete(row: FileInfoApi.FileVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除文件「${row.originalName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteFile(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handlePhysicalDelete(row: FileInfoApi.FileVO) {
  try {
    await ElMessageBox.confirm(
      `⚠️ 警告：物理删除将同时删除OSS存储的实际文件，此操作不可恢复！\n\n确定要永久删除文件「${row.originalName}」吗？`,
      '物理删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
        dangerouslyUseHTMLString: false,
      },
    );
    await physicalDeleteFile(row.id);
    ElMessage.success('物理删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchPhysicalDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的文件');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `⚠️ 警告：物理删除将同时删除OSS存储的实际文件，此操作不可恢复！\n\n确定要永久删除选中的 ${selectedRows.value.length} 个文件吗？`,
      '批量物理删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'error',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await physicalDeleteFileBatch(ids);
    ElMessage.success('批量物理删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的文件');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个文件吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteFileBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

function copyToClipboard(text: string) {
  navigator.clipboard
    .writeText(text)
    .then(() => {
      ElMessage.success('链接已复制');
    })
    .catch(() => {
      ElMessage.error('复制失败');
    });
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[0]
  );
}

function getCategoryLabel(category?: string) {
  return (
    fileTypeOptions.find((item) => item.value === category)?.label ||
    category ||
    '其他'
  );
}

function getCategoryColor(category?: string) {
  return (
    fileTypeOptions.find((item) => item.value === category)?.color || '#606266'
  );
}

function getFileIcon(fileType?: string): string {
  if (!fileType) return 'i-lucide-file';
  if (fileType.startsWith('image/')) return 'i-lucide-image';
  if (fileType.startsWith('video/')) return 'i-lucide-video';
  if (fileType.startsWith('audio/')) return 'i-lucide-music';
  if (fileType.includes('pdf')) return 'i-lucide-file-text';
  if (fileType.includes('word') || fileType.includes('document'))
    return 'i-lucide-file-type';
  if (fileType.includes('excel') || fileType.includes('sheet'))
    return 'i-lucide-file-spreadsheet';
  if (
    fileType.includes('zip') ||
    fileType.includes('rar') ||
    fileType.includes('7z')
  )
    return 'i-lucide-archive';
  return 'i-lucide-file';
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchData();
});
</script>

<template>
  <Page auto-content-height>
    <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 搜索区域 -->
      <div class="mb-4 border-b border-border pb-4">
        <ElForm :model="searchForm" inline>
          <ElFormItem label="关键词">
            <ElInput
              v-model="searchForm.keyword"
              placeholder="文件名"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="文件分类">
            <ElSelect
              v-model="searchForm.category"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in fileTypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="状态">
            <ElSelect
              v-model="searchForm.status"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['file:info:query']"
              @click="handleSearch"
            >
              查询
            </ElButton>
            <ElButton @click="handleReset">重置</ElButton>
          </ElFormItem>
        </ElForm>
      </div>

      <!-- 操作按钮 -->
      <div class="mb-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <ElButton
            type="primary"
            v-access:code="['file:info:upload']"
            @click="handleUpload"
          >
            <span class="i-lucide-upload mr-1"></span>
            上传文件
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['file:info:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
          <ElButton
            type="danger"
            :disabled="!hasSelected"
            v-access:code="['file:info:delete']"
            @click="handleBatchPhysicalDelete"
          >
            批量物理删除
          </ElButton>
        </div>
      </div>

      <!-- 表格 -->
      <ElTable
        v-loading="loading"
        :data="tableData"
        border
        stripe
        class="flex-1"
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" width="50" align="center" />
        <ElTableColumn label="预览" width="80" align="center">
          <template #default="{ row }">
            <div class="flex items-center justify-center">
              <ElImage
                v-if="isImage(row.fileType) || row.category === 'image'"
                :src="row.previewUrl || row.fileUrl"
                fit="cover"
                class="h-12 w-12 cursor-pointer rounded"
                :preview-src-list="[row.previewUrl || row.fileUrl]"
                :z-index="2000"
                lazy
              >
                <template #error>
                  <div
                    class="flex h-12 w-12 items-center justify-center bg-gray-100"
                  >
                    <span
                      class="i-lucide-image-off text-lg text-gray-400"
                    ></span>
                  </div>
                </template>
              </ElImage>
              <span
                v-else
                :class="getFileIcon(row.fileType)"
                class="text-2xl text-gray-400"
              ></span>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn label="文件名" min-width="200">
          <template #default="{ row }">
            <div class="flex items-center gap-2">
              <span class="truncate" :title="row.originalName">{{
                row.originalName
              }}</span>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn label="分类" width="90" align="center">
          <template #default="{ row }">
            <ElTag
              size="small"
              :style="{
                backgroundColor: getCategoryColor(row.category),
                borderColor: getCategoryColor(row.category),
                color: '#fff',
              }"
              round
            >
              {{ getCategoryLabel(row.category) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="文件大小" width="100" align="right">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="扩展名"
          prop="fileExt"
          width="80"
          align="center"
        />
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="上传时间" prop="uploadTime" width="170" />
        <ElTableColumn label="上传用户" prop="uploaderName" width="100" />
        <ElTableColumn label="操作" width="280" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              @click="handlePreview(row)"
            >
              预览
            </ElButton>
            <ElButton
              type="success"
              link
              size="small"
              v-access:code="['file:info:download']"
              @click="handleDownload(row)"
            >
              下载
            </ElButton>
            <ElButton
              type="info"
              link
              size="small"
              @click="copyToClipboard(row.fileUrl)"
            >
              复制链接
            </ElButton>
            <ElButton
              type="warning"
              link
              size="small"
              v-access:code="['file:info:delete']"
              @click="handleDelete(row)"
            >
              删除
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['file:info:delete']"
              @click="handlePhysicalDelete(row)"
            >
              物理删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>

      <!-- 分页 -->
      <div class="mt-4 flex justify-end">
        <ElPagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 上传弹窗 -->
    <ElDialog
      v-model="uploadDialogVisible"
      title="上传文件"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElUpload
        v-model:file-list="fileList"
        drag
        multiple
        :auto-upload="false"
        :on-change="handleUploadChange"
      >
        <div class="flex flex-col items-center justify-center py-8">
          <span
            class="i-lucide-upload-cloud mb-2 text-4xl text-gray-400"
          ></span>
          <p class="text-gray-600">将文件拖到此处，或点击上传</p>
          <p class="mt-1 text-sm text-gray-400">支持单个或批量上传</p>
        </div>
      </ElUpload>
      <template #footer>
        <ElButton @click="uploadDialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="uploadLoading" @click="submitUpload">
          上传
        </ElButton>
      </template>
    </ElDialog>

    <!-- 预览弹窗 -->
    <ElDialog
      v-model="previewDialogVisible"
      :title="previewFileName"
      width="800px"
      :close-on-click-modal="true"
    >
      <div class="flex justify-center">
        <ElImage
          :src="previewUrl"
          fit="contain"
          style="max-height: 500px"
          :preview-src-list="[previewUrl]"
        />
      </div>
    </ElDialog>
  </Page>
</template>
