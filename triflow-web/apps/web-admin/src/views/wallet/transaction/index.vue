<script lang="ts" setup>
import type { WalletApi } from '#/api/wallet';

import { computed, onMounted, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDatePicker,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElOption,
  ElPagination,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { getUserList } from '#/api/system';
import { adjustWallet, getTransactionPage } from '#/api/wallet';

// ==================== 状态定义 ====================

const tableData = ref<WalletApi.TransactionVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<WalletApi.QueryParams>({
  username: '',
  type: undefined,
  action: undefined,
  bizType: '',
  startTime: undefined,
  endTime: undefined,
});

const dateRange = ref<[Date, Date] | null>(null);

const adjustDialogVisible = ref(false);
const adjustFormRef = ref();
const adjustFormLoading = ref(false);
const adjustUserIds = ref<number[]>([]);
const adjustForm = reactive<
  Omit<WalletApi.AdjustParams, 'userId'> & { userId?: number }
>({
  type: 'points',
  action: 'income',
  amount: 0,
  title: '',
  bizType: '',
  bizId: '',
  remark: '',
});

const userList = ref<{ id: number; nickname: string; username: string }[]>([]);
const userLoading = ref(false);

// ==================== 常量 ====================

const typeOptions = [
  { label: '积分', value: 'points' as const },
];

const actionOptions = [
  { label: '收入', value: 'income' as const },
  { label: '支出', value: 'expense' as const },
  { label: '冻结', value: 'freeze' as const },
  { label: '解冻', value: 'unfreeze' as const },
];

const adjustActionOptions = [
  { label: '增加', value: 'income' as const },
  { label: '扣减', value: 'expense' as const },
];

const adjustFormRules = {
  action: [{ message: '请选择操作类型', required: true, trigger: 'change' }],
  amount: [{ message: '请输入金额', required: true, trigger: 'blur' }],
  title: [{ message: '请输入标题', required: true, trigger: 'blur' }],
  type: [{ message: '请选择类型', required: true, trigger: 'change' }],
};

// ==================== 计算属性 ====================

const amountLabel = computed(() => {
  return adjustForm.type === 'points' ? '积分数量' : '金额';
});

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };

    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0].toISOString();
      params.endTime = dateRange.value[1].toISOString();
    } else {
      params.startTime = undefined;
      params.endTime = undefined;
    }

    const res = await getTransactionPage(params);
    let records: any[] = [];
    if (Array.isArray(res)) {
      records = res;
    } else if (Array.isArray((res as any)?.records)) {
      records = (res as any).records;
    } else if (Array.isArray((res as any)?.rows)) {
      records = (res as any).rows;
    }
    const total = Number(
      (res as any)?.totalRow ?? (res as any)?.total ?? records.length,
    );
    tableData.value = records;
    pagination.total = total;
  } catch (error) {
    console.error('获取变动记录失败:', error);
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
  searchForm.type = undefined;
  searchForm.action = undefined;
  searchForm.bizType = '';
  dateRange.value = null;
  handleSearch();
}

// 监听分页变化（使用 watch 代替废弃的 @current-change 和 @size-change 事件）
// 注意：需要 skip 首次执行，因为 onMounted 已经调用了 fetchData
let paginationInitialized = false;
watch(
  () => [pagination.pageNum, pagination.pageSize],
  ([, newSize], [, oldSize]) => {
    if (!paginationInitialized) {
      paginationInitialized = true;
      return;
    }
    // 如果是 pageSize 变化，重置到第一页
    if (newSize !== oldSize) {
      pagination.pageNum = 1;
      return; // 让 pageNum 的变化再触发一次 watch
    }
    fetchData();
  },
);

async function searchUsers(keyword: string) {
  if (!keyword) {
    userList.value = [];
    return;
  }
  userLoading.value = true;
  try {
    const res = await getUserList({ keyword });
    userList.value = res.map((u) => ({
      id: u.id,
      nickname: u.nickname || '',
      username: u.username,
    }));
  } catch (error) {
    console.error('搜索用户失败:', error);
  } finally {
    userLoading.value = false;
  }
}

function showAdjustDialog() {
  adjustUserIds.value = [];
  adjustForm.type = 'points';
  adjustForm.action = 'income';
  adjustForm.amount = 0;
  adjustForm.title = '';
  adjustForm.bizType = '';
  adjustForm.bizId = '';
  adjustForm.remark = '';
  userList.value = [];
  adjustDialogVisible.value = true;
}

async function handleAdjust() {
  if (!adjustFormRef.value) return;
  if (adjustUserIds.value.length === 0) {
    ElMessage.warning('请选择至少一个用户');
    return;
  }

  try {
    await adjustFormRef.value.validate();
    adjustFormLoading.value = true;

    let successCount = 0;
    let failCount = 0;
    for (const userId of adjustUserIds.value) {
      try {
        await adjustWallet({
          userId,
          type: adjustForm.type,
          action: adjustForm.action,
          amount: adjustForm.amount,
          title: adjustForm.title,
          bizType: adjustForm.bizType,
          bizId: adjustForm.bizId,
          remark: adjustForm.remark,
        });
        successCount++;
      } catch {
        failCount++;
      }
    }

    if (failCount === 0) {
      ElMessage.success(`已成功调整 ${successCount} 个用户`);
    } else {
      ElMessage.warning(`成功 ${successCount} 个，失败 ${failCount} 个`);
    }
    adjustDialogVisible.value = false;
    fetchData();
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message);
    }
  } finally {
    adjustFormLoading.value = false;
  }
}

function getTypeTagType(type: string) {
  return type === 'points' ? 'warning' : 'primary';
}

function getActionTagType(action: string) {
  switch (action) {
    case 'expense': {
      return 'danger';
    }
    case 'freeze': {
      return 'info';
    }
    case 'income': {
      return 'success';
    }
    case 'unfreeze': {
      return 'primary';
    }
    default: {
      return 'info';
    }
  }
}

function formatAmount(row: WalletApi.TransactionVO) {
  const prefix =
    row.action === 'income' || row.action === 'unfreeze' ? '+' : '-';
  const value =
    row.type === 'points'
      ? Math.abs(row.amount)
      : Math.abs(row.amount).toFixed(2);
  return `${prefix}${value}`;
}

function getAmountClass(action: string) {
  return action === 'income' || action === 'unfreeze'
    ? 'text-green-500'
    : 'text-red-500';
}

function formatBalance(row: WalletApi.TransactionVO) {
  return row.type === 'points'
    ? row.afterAmount.toString()
    : row.afterAmount.toFixed(2);
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchData();
});
</script>

<template>
  <Page auto-content-height class="wallet-page">
    <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 搜索区域 -->
      <div class="mb-4 border-b border-border pb-4">
        <ElForm :model="searchForm" inline>
          <ElFormItem label="用户名">
            <ElInput
              v-model="searchForm.username"
              placeholder="用户名/昵称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="类型">
            <ElSelect
              v-model="searchForm.type"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="操作">
            <ElSelect
              v-model="searchForm.action"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in actionOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="时间范围">
            <ElDatePicker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 260px"
            />
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['wallet:transaction:query']"
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
            v-access:code="['wallet:transaction:adjust']"
            @click="showAdjustDialog"
          >
            <span class="i-lucide-sliders-horizontal mr-1"></span>
            账户调整
          </ElButton>
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <ElTable v-loading="loading" :data="tableData" border stripe>
          <ElTableColumn prop="id" label="ID" width="70" />
          <ElTableColumn label="用户" width="160">
            <template #default="{ row }">
              <div>{{ row.nickname || row.username }}</div>
              <div class="text-xs text-gray-400">{{ row.username }}</div>
            </template>
          </ElTableColumn>
          <ElTableColumn label="类型" width="100" align="center">
            <template #default="{ row }">
              <ElTag :type="getTypeTagType(row.type)" size="small">
                {{ row.typeDesc || row.type }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作" width="100" align="center">
            <template #default="{ row }">
              <ElTag :type="getActionTagType(row.action)" size="small">
                {{ row.actionDesc || row.action }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="变动金额" width="120" align="right">
            <template #default="{ row }">
              <span :class="getAmountClass(row.action)" class="font-semibold">
                {{ formatAmount(row) }}
              </span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="变动后余额" width="120" align="right">
            <template #default="{ row }">
              {{ formatBalance(row) }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="title" label="标题" min-width="150" />
          <ElTableColumn prop="remark" label="备注" min-width="120" />
          <ElTableColumn label="操作人" width="100">
            <template #default="{ row }">
              {{ row.operatorName || '-' }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="createTime" label="创建时间" width="170" />
        </ElTable>
      </div>

      <!-- 分页 - 使用新版写法，移除废弃的事件监听 -->
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

    <!-- 调整对话框 -->
    <ElDialog
      v-model="adjustDialogVisible"
      title="调整用户账户"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustFormRules"
        label-width="100px"
      >
        <ElFormItem label="选择用户" required>
          <ElSelect
            v-model="adjustUserIds"
            multiple
            filterable
            remote
            reserve-keyword
            collapse-tags
            collapse-tags-tooltip
            :max-collapse-tags="3"
            placeholder="输入用户名搜索（支持多选）"
            :remote-method="searchUsers"
            :loading="userLoading"
            style="width: 100%"
          >
            <ElOption
              v-for="user in userList"
              :key="user.id"
              :label="`${user.nickname || user.username} (${user.username})`"
              :value="user.id"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="账户类型" prop="type">
          <ElSelect v-model="adjustForm.type" style="width: 100%">
            <ElOption
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="操作类型" prop="action">
          <ElSelect v-model="adjustForm.action" style="width: 100%">
            <ElOption
              v-for="item in adjustActionOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem :label="amountLabel" prop="amount">
          <ElInputNumber
            v-model="adjustForm.amount"
            :min="0.01"
            :precision="adjustForm.type === 'points' ? 0 : 2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="标题" prop="title">
          <ElInput
            v-model="adjustForm.title"
            placeholder="例如：后台手动调整"
          />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="adjustForm.remark"
            type="textarea"
            :rows="3"
            placeholder="可选的备注说明"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="adjustDialogVisible = false">取消</ElButton>
        <ElButton
          type="primary"
          :loading="adjustFormLoading"
          @click="handleAdjust"
        >
          确定
        </ElButton>
      </template>
    </ElDialog>
  </Page>
</template>

<style scoped>
.wallet-page .table-wrapper {
  overflow-x: auto;
}

.wallet-page .pagination-wrapper {
  flex-wrap: wrap;
  gap: 8px;
}

@media (max-width: 768px) {
  .wallet-page :deep(.el-form--inline .el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .wallet-page :deep(.el-form--inline .el-form-item__content) {
    flex: 1;
  }

  .wallet-page :deep(.el-select),
  .wallet-page :deep(.el-date-editor) {
    width: 100% !important;
  }
}
</style>
