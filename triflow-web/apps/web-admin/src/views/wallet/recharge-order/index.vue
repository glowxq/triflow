<script lang="ts" setup>
import type { WalletApi } from '#/api/wallet';

import { onMounted, reactive, ref } from 'vue';

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

import { getRechargeOrderPage, refundRechargeOrder, updateRechargeOrder } from '#/api/wallet';

// ==================== 状态定义 ====================

const tableData = ref<WalletApi.RechargeOrderVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<WalletApi.RechargeOrderQuery>({
  orderNo: '',
  username: '',
  type: undefined,
  status: undefined,
  startTime: undefined,
  endTime: undefined,
});

const dateRange = ref<[Date, Date] | null>(null);

const refundDialogVisible = ref(false);
const refundFormLoading = ref(false);
const refundForm = reactive<{ orderId?: number; orderNo?: string; reason: string }>({
  orderId: undefined,
  orderNo: '',
  reason: '',
});

const editDialogVisible = ref(false);
const editFormLoading = ref(false);
const editForm = reactive<{
  id?: number;
  orderNo?: string;
  userLabel?: string;
  status?: WalletApi.RechargeOrderVO['status'];
  payAmount?: number;
  rewardAmount?: number;
  remark?: string;
}>({
  id: undefined,
  orderNo: '',
  userLabel: '',
  status: undefined,
  payAmount: undefined,
  rewardAmount: undefined,
  remark: '',
});

// ==================== 常量 ====================

const typeOptions = [
  { label: '积分', value: 'points' as const },
  { label: '余额', value: 'balance' as const },
];

const statusOptions = [
  { label: '待支付', value: 'pending' as const, type: 'info' as const },
  { label: '已支付', value: 'paid' as const, type: 'success' as const },
  { label: '已退款', value: 'refunded' as const, type: 'warning' as const },
  { label: '已关闭', value: 'closed' as const, type: 'info' as const },
  { label: '支付失败', value: 'failed' as const, type: 'danger' as const },
];

const statusTagMap = statusOptions.reduce((acc, item) => {
  acc[item.value] = item;
  return acc;
}, {} as Record<string, { label: string; type: 'info' | 'success' | 'warning' | 'danger' }>);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    } as WalletApi.RechargeOrderQuery;

    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0].toISOString();
      params.endTime = dateRange.value[1].toISOString();
    } else {
      params.startTime = undefined;
      params.endTime = undefined;
    }

    const res = await getRechargeOrderPage(params);
    const records = Array.isArray((res as any)?.records) ? (res as any).records : [];
    tableData.value = records;
    pagination.total = Number((res as any)?.totalRow ?? (res as any)?.total ?? records.length);
  } catch (error) {
    console.error('获取充值订单失败:', error);
    ElMessage.error('获取充值订单失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.orderNo = '';
  searchForm.username = '';
  searchForm.type = undefined;
  searchForm.status = undefined;
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

function openRefundDialog(row: WalletApi.RechargeOrderVO) {
  refundForm.orderId = row.id;
  refundForm.orderNo = row.orderNo;
  refundForm.reason = '';
  refundDialogVisible.value = true;
}

function openEditDialog(row: WalletApi.RechargeOrderVO) {
  editForm.id = row.id;
  editForm.orderNo = row.orderNo;
  editForm.userLabel = row.nickname || row.username || '-';
  editForm.status = row.status;
  editForm.payAmount = row.payAmount;
  editForm.rewardAmount = row.rewardAmount;
  editForm.remark = row.remark || '';
  editDialogVisible.value = true;
}

async function submitRefund() {
  if (!refundForm.orderId) {
    return;
  }
  refundFormLoading.value = true;
  try {
    await refundRechargeOrder({
      orderId: refundForm.orderId,
      reason: refundForm.reason || undefined,
    });
    ElMessage.success('退款申请成功');
    refundDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error('充值订单退款失败:', error);
    ElMessage.error('退款失败');
  } finally {
    refundFormLoading.value = false;
  }
}

async function submitEdit() {
  if (!editForm.id) {
    return;
  }
  editFormLoading.value = true;
  try {
    await updateRechargeOrder({
      id: editForm.id,
      status: editForm.status,
      payAmount: editForm.payAmount,
      rewardAmount: editForm.rewardAmount,
      remark: editForm.remark || undefined,
    });
    ElMessage.success('订单已更新');
    editDialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error('更新订单失败:', error);
    ElMessage.error('更新失败');
  } finally {
    editFormLoading.value = false;
  }
}

function formatAmount(value?: number) {
  if (value === undefined || value === null) {
    return '-';
  }
  return Number(value).toFixed(2);
}

onMounted(() => {
  fetchData();
});
</script>

<template>
  <div class="wallet-recharge-order">
    <Page auto-content-height class="wallet-page">
      <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 搜索区域 -->
      <div class="mb-4 border-b border-border pb-4">
        <ElForm :model="searchForm" inline>
          <ElFormItem label="订单号">
            <ElInput
              v-model="searchForm.orderNo"
              placeholder="输入订单号"
              clearable
              style="width: 220px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
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
              style="width: 160px"
            >
              <ElOption
                v-for="item in typeOptions"
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
              style="width: 160px"
            >
              <ElOption
                v-for="item in statusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="下单时间">
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
              v-access:code="['wallet:recharge-order:query']"
              @click="handleSearch"
            >
              查询
            </ElButton>
            <ElButton @click="handleReset">重置</ElButton>
          </ElFormItem>
        </ElForm>
      </div>

      <!-- 表格 -->
      <ElTable v-loading="loading" :data="tableData" border stripe>
        <ElTableColumn prop="id" label="ID" width="70" />
        <ElTableColumn prop="orderNo" label="订单号" min-width="180" />
        <ElTableColumn label="用户" min-width="180">
          <template #default="{ row }">
            <div>{{ row.nickname || row.username }}</div>
            <div class="text-xs text-gray-400">{{ row.username }}</div>
          </template>
        </ElTableColumn>
        <ElTableColumn label="类型" width="110">
          <template #default="{ row }">
            <ElTag :type="row.type === 'points' ? 'info' : 'success'">
              {{ row.typeDesc || (row.type === 'points' ? '积分' : '余额') }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="支付金额" width="120" align="right">
          <template #default="{ row }">{{ formatAmount(row.payAmount) }}</template>
        </ElTableColumn>
        <ElTableColumn label="到账金额" width="120" align="right">
          <template #default="{ row }">
            {{ row.type === 'points' ? row.rewardAmount : formatAmount(row.rewardAmount) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="状态" width="110">
          <template #default="{ row }">
            <ElTag :type="statusTagMap[row.status]?.type || 'info'">
              {{ row.statusDesc || statusTagMap[row.status]?.label || row.status }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="payTime" label="支付时间" width="170" />
        <ElTableColumn prop="refundTime" label="退款时间" width="170" />
        <ElTableColumn prop="createTime" label="创建时间" width="170" />
        <ElTableColumn label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              @click="openEditDialog(row)"
            >
              编辑
            </ElButton>
            <ElButton
              size="small"
              type="danger"
              :disabled="row.status !== 'paid'"
              v-access:code="['wallet:recharge-order:refund']"
              @click="openRefundDialog(row)"
            >
              退款
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>

      <!-- 分页 -->
      <div class="mt-4 flex justify-end">
        <ElPagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      </div>
    </Page>

    <ElDialog
      v-model="refundDialogVisible"
      title="充值订单退款"
      width="420px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="90px">
        <ElFormItem label="订单号">
          <ElInput v-model="refundForm.orderNo" disabled />
        </ElFormItem>
        <ElFormItem label="退款原因">
          <ElInput
            v-model="refundForm.reason"
            type="textarea"
            :rows="3"
            placeholder="可选：填写退款原因"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="refundDialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="refundFormLoading" @click="submitRefund">
          确认退款
        </ElButton>
      </template>
    </ElDialog>

    <ElDialog
      v-model="editDialogVisible"
      title="编辑充值订单"
      width="520px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="100px">
        <ElFormItem label="订单号">
          <ElInput v-model="editForm.orderNo" disabled />
        </ElFormItem>
        <ElFormItem label="用户">
          <ElInput v-model="editForm.userLabel" disabled />
        </ElFormItem>
        <ElFormItem label="订单状态">
          <ElSelect v-model="editForm.status" style="width: 100%">
            <ElOption
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="支付金额">
          <ElInputNumber
            v-model="editForm.payAmount"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="到账金额">
          <ElInputNumber
            v-model="editForm.rewardAmount"
            :min="0"
            :precision="2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="editForm.remark"
            type="textarea"
            :rows="3"
            placeholder="可选：填写备注"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="editDialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="editFormLoading" @click="submitEdit">
          保存
        </ElButton>
      </template>
    </ElDialog>
  </div>
</template>
