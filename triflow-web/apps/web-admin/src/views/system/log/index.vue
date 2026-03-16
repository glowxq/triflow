<script lang="ts" setup>
import type { LogOperationApi } from '#/api/system';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDatePicker,
  ElDialog,
  ElDescriptions,
  ElDescriptionsItem,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import {
  clearLog,
  deleteLogBatch,
  exportLog,
  getLogById,
  getLogPage,
} from '#/api/system';

// ==================== 工具函数 ====================

function downloadFile(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = filename;
  document.body.append(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

// ==================== 状态定义 ====================

const tableData = ref<LogOperationApi.LogVO[]>([]);
const loading = ref(false);
const selectedRows = ref<LogOperationApi.LogVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<LogOperationApi.QueryParams>({
  keyword: '',
  module: undefined,
  operation: undefined,
  status: undefined,
  startTime: undefined,
  endTime: undefined,
});

const detailVisible = ref(false);
const detailData = ref<LogOperationApi.LogVO | null>(null);
const detailLoading = ref(false);

const dateRange = ref<[string, string] | null>(null);

// ==================== 常量 ====================

const statusOptions = [
  { label: '成功', value: 1, type: 'success' as const },
  { label: '失败', value: 0, type: 'danger' as const },
];

const moduleOptions = [
  { label: '系统管理', value: 'system' },
  { label: '用户管理', value: 'user' },
  { label: '角色管理', value: 'role' },
  { label: '菜单管理', value: 'menu' },
  { label: '部门管理', value: 'dept' },
  { label: '配置管理', value: 'config' },
  { label: '开关管理', value: 'switch' },
  { label: '文件管理', value: 'file' },
  { label: '内容管理', value: 'cms' },
];

const operationOptions = [
  { label: '新增', value: 'create' },
  { label: '修改', value: 'update' },
  { label: '删除', value: 'delete' },
  { label: '查询', value: 'query' },
  { label: '登录', value: 'login' },
  { label: '登出', value: 'logout' },
  { label: '导入', value: 'import' },
  { label: '导出', value: 'export' },
  { label: '其他', value: 'other' },
];

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    // 处理时间范围
    if (dateRange.value && dateRange.value.length === 2) {
      searchForm.startTime = dateRange.value[0];
      searchForm.endTime = dateRange.value[1];
    } else {
      searchForm.startTime = undefined;
      searchForm.endTime = undefined;
    }

    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getLogPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取操作日志列表失败');
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
  searchForm.module = undefined;
  searchForm.operation = undefined;
  searchForm.status = undefined;
  searchForm.startTime = undefined;
  searchForm.endTime = undefined;
  dateRange.value = null;
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

function handleSelectionChange(rows: LogOperationApi.LogVO[]) {
  selectedRows.value = rows;
}

async function handleDetail(row: LogOperationApi.LogVO) {
  detailLoading.value = true;
  detailVisible.value = true;
  try {
    const detail = await getLogById(row.id);
    detailData.value = detail;
  } catch {
    ElMessage.error('获取日志详情失败');
  } finally {
    detailLoading.value = false;
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的日志');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 条日志吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteLogBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleClear() {
  try {
    await ElMessageBox.confirm(
      '确定要清空所有操作日志吗？此操作不可恢复！',
      '清空确认',
      {
        confirmButtonText: '确定清空',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await clearLog();
    ElMessage.success('清空成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleExport() {
  try {
    ElMessage.info('正在导出，请稍候...');
    const blob = await exportLog(searchForm);
    downloadFile(blob, '操作日志.xlsx');
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

function getModuleLabel(module?: string) {
  if (!module) return '-';
  const option = moduleOptions.find((item) => item.value === module);
  return option?.label || module;
}

function getOperationLabel(operation?: string) {
  if (!operation) return '-';
  const option = operationOptions.find((item) => item.value === operation);
  return option?.label || operation;
}

function getMethodTagType(method?: string) {
  if (!method) return 'info';
  const map: Record<string, string> = {
    GET: 'success',
    POST: 'primary',
    PUT: 'warning',
    DELETE: 'danger',
  };
  return map[method.toUpperCase()] || 'info';
}

function formatJson(jsonStr?: string) {
  if (!jsonStr) return '-';
  try {
    return JSON.stringify(JSON.parse(jsonStr), null, 2);
  } catch {
    return jsonStr;
  }
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
              placeholder="操作描述/操作人"
              clearable
              style="width: 180px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="模块">
            <ElSelect
              v-model="searchForm.module"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <ElOption
                v-for="item in moduleOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作类型">
            <ElSelect
              v-model="searchForm.operation"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in operationOptions"
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
              style="width: 100px"
            >
              <ElOption
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作时间">
            <ElDatePicker
              v-model="dateRange"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DDTHH:mm:ss"
              style="width: 340px"
            />
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['system:log:query']"
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
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:log:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
          <ElButton
            type="danger"
            v-access:code="['system:log:clear']"
            @click="handleClear"
          >
            清空日志
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton
            type="success"
            plain
            v-access:code="['system:log:export']"
            @click="handleExport"
          >
            导出
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
        <ElTableColumn label="模块" width="110" align="center">
          <template #default="{ row }">
            {{ getModuleLabel(row.module) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作类型" width="90" align="center">
          <template #default="{ row }">
            {{ getOperationLabel(row.operation) }}
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="操作描述"
          prop="description"
          min-width="180"
          show-overflow-tooltip
        />
        <ElTableColumn label="请求方法" width="90" align="center">
          <template #default="{ row }">
            <ElTag
              v-if="row.method"
              :type="getMethodTagType(row.method) as any"
              size="small"
            >
              {{ row.method }}
            </ElTag>
            <span v-else>-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="操作人"
          prop="operatorName"
          width="100"
          align="center"
        />
        <ElTableColumn label="操作IP" prop="ip" width="140" />
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="耗时" width="90" align="center">
          <template #default="{ row }">
            <span v-if="row.duration != null">{{ row.duration }}ms</span>
            <span v-else>-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作时间" prop="operateTime" width="170" />
        <ElTableColumn label="操作" width="80" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:log:query']"
              @click="handleDetail(row)"
            >
              详情
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

    <!-- 详情弹窗 -->
    <ElDialog
      v-model="detailVisible"
      title="日志详情"
      width="700px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-loading="detailLoading">
        <ElDescriptions v-if="detailData" :column="2" border>
          <ElDescriptionsItem label="日志ID">
            {{ detailData.id }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作模块">
            {{ getModuleLabel(detailData.module) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作类型">
            {{ getOperationLabel(detailData.operation) }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作状态">
            <ElTag
              :type="getStatusOption(detailData.status).type"
              size="small"
            >
              {{ getStatusOption(detailData.status).label }}
            </ElTag>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作描述" :span="2">
            {{ detailData.description || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求方法">
            <ElTag
              v-if="detailData.method"
              :type="getMethodTagType(detailData.method) as any"
              size="small"
            >
              {{ detailData.method }}
            </ElTag>
            <span v-else>-</span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="执行耗时">
            {{
              detailData.duration != null ? `${detailData.duration}ms` : '-'
            }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求URL" :span="2">
            {{ detailData.requestUrl || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作人">
            {{ detailData.operatorName || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作人ID">
            {{ detailData.operatorId || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作IP">
            {{ detailData.ip || '-' }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="操作时间">
            {{ detailData.operateTime }}
          </ElDescriptionsItem>
          <ElDescriptionsItem label="用户代理" :span="2">
            <span class="break-all text-xs text-gray-500">
              {{ detailData.userAgent || '-' }}
            </span>
          </ElDescriptionsItem>
          <ElDescriptionsItem label="请求参数" :span="2">
            <pre
              class="max-h-48 overflow-auto rounded bg-gray-50 p-2 text-xs"
              >{{ formatJson(detailData.requestParams) }}</pre
            >
          </ElDescriptionsItem>
          <ElDescriptionsItem label="响应数据" :span="2">
            <pre
              class="max-h-48 overflow-auto rounded bg-gray-50 p-2 text-xs"
              >{{ formatJson(detailData.responseData) }}</pre
            >
          </ElDescriptionsItem>
          <ElDescriptionsItem
            v-if="detailData.status === 0"
            label="错误信息"
            :span="2"
          >
            <pre
              class="max-h-48 overflow-auto rounded bg-red-50 p-2 text-xs text-red-600"
              >{{ detailData.errorMsg || '-' }}</pre
            >
          </ElDescriptionsItem>
        </ElDescriptions>
      </div>
      <template #footer>
        <ElButton @click="detailVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
