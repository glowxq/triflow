<script lang="ts" setup>
import type { AiCallLogVO } from '#/api/ai';

import { onMounted, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElCol,
  ElDatePicker,
  ElDescriptions,
  ElDescriptionsItem,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElPagination,
  ElRow,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { deleteAiCallLog, getAiCallLogPage } from '#/api/ai';

// ==================== 状态定义 ====================

const tableData = ref<AiCallLogVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive({
  username: '',
  provider: '',
  model: '',
  status: undefined as number | undefined,
  startTime: '',
  endTime: '',
});

const dateRange = ref<[string, string] | null>(null);

// AI 提供商选项 (与 Spring AI 自动检测的提供商名称对应)
const providerOptions = [
  { label: 'DeepSeek', value: 'deepseek' },
  { label: 'OpenAI / 通义千问', value: 'openai' },
  { label: '智谱 GLM', value: 'zhipuai' },
  { label: 'Anthropic Claude', value: 'anthropic' },
  { label: 'Ollama 本地模型', value: 'ollama' },
];

// 详情对话框
const detailDialogVisible = ref(false);
const detailData = ref<AiCallLogVO | null>(null);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      searchForm.startTime = dateRange.value[0];
      searchForm.endTime = dateRange.value[1];
    } else {
      searchForm.startTime = '';
      searchForm.endTime = '';
    }

    const res = await getAiCallLogPage({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    });
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch (error) {
    console.error('获取调用记录失败:', error);
    tableData.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.username = '';
  searchForm.provider = '';
  searchForm.model = '';
  searchForm.status = undefined;
  searchForm.startTime = '';
  searchForm.endTime = '';
  dateRange.value = null;
  handleSearch();
}

// 监听分页变化
let paginationInitialized = false;
watch(
  () => [pagination.pageNum, pagination.pageSize],
  ([_newPage, newSize], [_oldPage, oldSize]) => {
    if (!paginationInitialized) {
      paginationInitialized = true;
      return;
    }
    if (newSize !== oldSize) {
      pagination.pageNum = 1;
      return;
    }
    fetchData();
  },
);

function showDetailDialog(row: AiCallLogVO) {
  detailData.value = row;
  detailDialogVisible.value = true;
}

async function handleDelete(row: AiCallLogVO) {
  try {
    await ElMessageBox.confirm('确定要删除该调用记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    await deleteAiCallLog(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message);
    }
  }
}

// 格式化耗时
function formatDuration(ms?: number): string {
  if (!ms) return '-';
  if (ms < 1000) return `${ms}ms`;
  return `${(ms / 1000).toFixed(2)}s`;
}

// 格式化 token
function formatTokens(tokens?: number): string {
  if (tokens === undefined || tokens === null) return '-';
  return tokens.toLocaleString();
}

// 截断文本
function truncateText(text?: string, maxLength = 50): string {
  if (!text) return '-';
  if (text.length <= maxLength) return text;
  return `${text.slice(0, maxLength)}...`;
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchData();
});
</script>

<template>
  <Page auto-content-height class="ai-call-log-page">
    <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 搜索区域 -->
      <div class="mb-4 border-b border-border pb-4">
        <ElForm :model="searchForm" inline>
          <ElFormItem label="用户名">
            <ElInput
              v-model="searchForm.username"
              placeholder="用户名"
              clearable
              style="width: 150px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="提供商">
            <ElSelect
              v-model="searchForm.provider"
              placeholder="全部"
              clearable
              style="width: 140px"
            >
              <ElOption
                v-for="opt in providerOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="模型">
            <ElInput
              v-model="searchForm.model"
              placeholder="模型名称"
              clearable
              style="width: 150px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="状态">
            <ElSelect
              v-model="searchForm.status"
              placeholder="全部"
              clearable
              style="width: 100px"
            >
              <ElOption label="成功" :value="1" />
              <ElOption label="失败" :value="0" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="时间范围">
            <ElDatePicker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD HH:mm:ss"
              style="width: 340px"
            />
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['ai:call-log:query']"
              @click="handleSearch"
            >
              查询
            </ElButton>
            <ElButton @click="handleReset">重置</ElButton>
          </ElFormItem>
        </ElForm>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <ElTable v-loading="loading" :data="tableData" border stripe>
          <ElTableColumn prop="id" label="ID" width="70" />
          <ElTableColumn prop="username" label="用户" width="120">
            <template #default="{ row }">
              <span>{{ row.username || '-' }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="providerName" label="提供商" width="110">
            <template #default="{ row }">
              <ElTag size="small" type="info">
                {{ row.providerName || row.provider }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="model" label="模型" width="150">
            <template #default="{ row }">
              <span class="text-xs text-gray-500">{{ row.model || '-' }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="用户消息" min-width="200">
            <template #default="{ row }">
              <span class="line-clamp-2 text-sm">
                {{ truncateText(row.userMessage, 80) }}
              </span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="状态" width="80" align="center">
            <template #default="{ row }">
              <ElTag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.statusDesc }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="耗时" width="90" align="center">
            <template #default="{ row }">
              <span :class="row.duration > 5000 ? 'text-orange-500' : ''">
                {{ formatDuration(row.duration) }}
              </span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="Token" width="100" align="center">
            <template #default="{ row }">
              <span class="text-xs">{{ formatTokens(row.totalTokens) }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="createTime" label="调用时间" width="170" />
          <ElTableColumn label="操作" width="130" fixed="right">
            <template #default="{ row }">
              <ElButton
                type="primary"
                link
                size="small"
                @click="showDetailDialog(row)"
              >
                详情
              </ElButton>
              <ElButton
                type="danger"
                link
                size="small"
                v-access:code="['ai:call-log:delete']"
                @click="handleDelete(row)"
              >
                删除
              </ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper mt-4 flex justify-end">
        <ElPagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <ElDialog
      v-model="detailDialogVisible"
      title="调用详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <template v-if="detailData">
        <ElDescriptions :column="2" border>
          <ElDescriptionsItem label="调用ID">
            {{ detailData.id }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="状态">
            <ElTag
              :type="detailData.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ detailData.statusDesc }}
            </ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="用户">
            {{ detailData.username || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求IP">
            {{ detailData.ip || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="AI提供商">
            {{ detailData.providerName || detailData.provider }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="模型">
            {{ detailData.model || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="耗时">
            {{ formatDuration(detailData.duration) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="调用时间">
            {{ detailData.createTime }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="Prompt Token">
            {{ formatTokens(detailData.promptTokens) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="Completion Token">
            {{ formatTokens(detailData.completionTokens) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="总 Token" :span="2">
            {{ formatTokens(detailData.totalTokens) }}
          </ElDescriptionsItem>
        </ElDescriptions>

        <ElRow :gutter="16" class="mt-4">
          <ElCol :span="24">
            <div class="detail-section">
              <h4 class="detail-title">系统提示 (System Prompt)</h4>
              <ElCard class="detail-card" shadow="never">
                <pre class="detail-content">{{
                  detailData.systemPrompt || '(无)'
                }}</pre>
              </ElCard>
            </div>
          </ElCol>
        </ElRow>

        <ElRow :gutter="16" class="mt-4">
          <ElCol :span="24">
            <div class="detail-section">
              <h4 class="detail-title">用户消息</h4>
              <ElCard class="detail-card" shadow="never">
                <pre class="detail-content">{{ detailData.userMessage }}</pre>
              </ElCard>
            </div>
          </ElCol>
        </ElRow>

        <ElRow :gutter="16" class="mt-4">
          <ElCol :span="24">
            <div class="detail-section">
              <h4 class="detail-title">
                AI 响应
                <ElTag
                  v-if="detailData.status === 0"
                  type="danger"
                  size="small"
                  class="ml-2"
                >
                  失败
                </ElTag>
              </h4>
              <ElCard
                class="detail-card"
                :class="{ 'error-card': detailData.status === 0 }"
                shadow="never"
              >
                <pre
                  v-if="detailData.status === 1"
                  class="detail-content"
                >{{ detailData.aiResponse || '(无响应)' }}</pre>
                <pre
                  v-else
                  class="detail-content error-text"
                >{{ detailData.errorMessage || '未知错误' }}</pre>
              </ElCard>
            </div>
          </ElCol>
        </ElRow>
      </template>
      <template #footer>
        <ElButton @click="detailDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>

<style scoped>
@media (max-width: 768px) {
  .ai-call-log-page :deep(.el-form--inline .el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .ai-call-log-page :deep(.el-form--inline .el-form-item__content) {
    flex: 1;
  }

  .ai-call-log-page :deep(.el-dialog) {
    width: 92vw !important;
    margin: 0 auto;
  }
}

.ai-call-log-page .table-wrapper {
  overflow-x: auto;
}

.ai-call-log-page .pagination-wrapper {
  flex-wrap: wrap;
  gap: 8px;
}

.line-clamp-2 {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

/* 详情区块 */
.detail-section {
  margin-bottom: 8px;
}

.detail-title {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.detail-card {
  background-color: #f9fafb;
  border: 1px solid #e5e7eb;
}

.detail-card :deep(.el-card__body) {
  padding: 12px 16px;
}

.detail-content {
  max-height: 200px;
  margin: 0;
  overflow-y: auto;
  font-family: Monaco, Menlo, 'Ubuntu Mono', Consolas, monospace;
  font-size: 13px;
  line-height: 1.6;
  color: #1f2937;
  word-wrap: break-word;
  white-space: pre-wrap;
}

/* 错误样式 */
.error-card {
  background-color: #fef2f2;
  border-color: #fecaca;
}

.error-text {
  color: #dc2626;
}

/* 文字颜色 */
.text-gray-500 {
  color: #6b7280;
}

.text-orange-500 {
  color: #f97316;
}
</style>
