<script lang="ts" setup>
import type { WalletApi } from '#/api/wallet';

import { computed, onMounted, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElPagination,
  ElSelect,
  ElSwitch,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import {
  createRechargeConfig,
  deleteRechargeConfig,
  deleteRechargeConfigBatch,
  getRechargeConfigDetail,
  getRechargeConfigPage,
  updateRechargeConfig,
} from '#/api/wallet';

// ==================== 状态定义 ====================

const tableData = ref<WalletApi.RechargeConfigVO[]>([]);
const loading = ref(false);
const selectedRows = ref<WalletApi.RechargeConfigVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<WalletApi.RechargeConfigQuery>({
  keyword: '',
  type: undefined,
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<WalletApi.RechargeConfigUpdateParams>({
  id: 0,
  configName: '',
  type: 'points',
  payAmount: 0,
  rewardAmount: 0,
  bonusAmount: 0,
  status: 1,
  sort: 0,
  remark: '',
});

// ==================== 常量 ====================

const typeOptions = [
  { label: '积分', value: 'points' as const },
];

const statusOptions = [
  { label: '启用', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const formRules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择充值类型', trigger: 'change' }],
  payAmount: [{ required: true, message: '请输入支付金额', trigger: 'blur' }],
  rewardAmount: [
    { required: true, message: '请输入到账金额', trigger: 'blur' },
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
};

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
    const res = await getRechargeConfigPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow ?? 0;
  } catch (error) {
    console.error('获取充值配置失败:', error);
    ElMessage.error('获取充值配置失败');
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
  searchForm.type = undefined;
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

function handleSelectionChange(rows: WalletApi.RechargeConfigVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = 0;
  formData.configName = '';
  formData.type = 'points';
  formData.payAmount = 0;
  formData.rewardAmount = 0;
  formData.bonusAmount = 0;
  formData.status = 1;
  formData.sort = 0;
  formData.remark = '';
}

function showCreateDialog() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增充值配置';
  dialogVisible.value = true;
}

async function showEditDialog(row: WalletApi.RechargeConfigVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑充值配置';
  dialogVisible.value = true;
  formLoading.value = true;
  try {
    const detail = await getRechargeConfigDetail(row.id);
    Object.assign(formData, detail);
  } catch (error) {
    console.error('获取配置详情失败:', error);
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    formLoading.value = true;

    if (dialogType.value === 'create') {
      await createRechargeConfig({
        configName: formData.configName,
        type: formData.type,
        payAmount: formData.payAmount,
        rewardAmount: formData.rewardAmount,
        bonusAmount: formData.bonusAmount,
        status: formData.status,
        sort: formData.sort,
        remark: formData.remark,
      });
      ElMessage.success('创建成功');
    } else {
      await updateRechargeConfig({ ...formData });
      ElMessage.success('更新成功');
    }

    dialogVisible.value = false;
    fetchData();
  } catch (error) {
    console.error('保存充值配置失败:', error);
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: WalletApi.RechargeConfigVO) {
  try {
    await ElMessageBox.confirm(
      `确定删除配置「${row.configName}」吗？`,
      '提示',
      {
        type: 'warning',
      },
    );
    await deleteRechargeConfig(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // ignore
  }
}

async function handleBatchDelete() {
  if (selectedRows.value.length === 0) return;
  try {
    await ElMessageBox.confirm('确定删除选中的配置吗？', '提示', {
      type: 'warning',
    });
    const ids = selectedRows.value.map((row) => row.id);
    await deleteRechargeConfigBatch(ids);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // ignore
  }
}

function formatAmount(value: number | undefined, type: 'balance' | 'points') {
  const num = Number(value ?? 0);
  return type === 'points' ? Math.round(num).toString() : num.toFixed(2);
}

watch(
  () => [pagination.pageNum, pagination.pageSize],
  ([newPage, newSize], [oldPage, oldSize]) => {
    if (newSize !== oldSize) {
      pagination.pageNum = 1;
      return;
    }
    if (newPage !== oldPage) {
      fetchData();
    }
  },
);

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
          <ElFormItem label="关键词">
            <ElInput
              v-model="searchForm.keyword"
              placeholder="名称/备注"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="类型">
            <ElSelect
              v-model="searchForm.type"
              clearable
              placeholder="全部"
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
          <ElFormItem label="状态">
            <ElSelect
              v-model="searchForm.status"
              clearable
              placeholder="全部"
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
              v-access:code="['wallet:recharge:query']"
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
            v-access:code="['wallet:recharge:create']"
            @click="showCreateDialog"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增配置
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['wallet:recharge:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
        </div>
      </div>

      <!-- 表格 -->
      <ElTable
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" width="50" />
        <ElTableColumn prop="id" label="ID" width="70" />
        <ElTableColumn prop="configName" label="配置名称" min-width="200" />
        <ElTableColumn label="类型" width="100" align="center">
          <template #default="{ row }">
            <ElTag
              :type="row.type === 'points' ? 'warning' : 'primary'"
              size="small"
            >
              {{ row.type === 'points' ? '积分' : '余额' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="支付金额" width="120" align="right">
          <template #default="{ row }">
            {{ Number(row.payAmount).toFixed(2) }} 元
          </template>
        </ElTableColumn>
        <ElTableColumn label="到账/赠送" min-width="180">
          <template #default="{ row }">
            <div>
              到账: {{ formatAmount(row.rewardAmount, row.type) }}
              {{ row.type === 'points' ? '积分' : '元' }}
            </div>
            <div class="text-xs text-gray-400">
              赠送: {{ formatAmount(row.bonusAmount || 0, row.type) }}
              {{ row.type === 'points' ? '积分' : '元' }}
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="sort" label="排序" width="80" align="center" />
        <ElTableColumn label="状态" width="90" align="center">
          <template #default="{ row }">
            <ElTag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="remark" label="备注" min-width="160" />
        <ElTableColumn prop="createTime" label="创建时间" width="170" />
        <ElTableColumn label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              v-access:code="['wallet:recharge:update']"
              @click="showEditDialog(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="danger"
              link
              v-access:code="['wallet:recharge:delete']"
              @click="handleDelete(row)"
            >
              删除
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

    <!-- 弹窗 -->
    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="110px"
      >
        <ElFormItem label="配置名称" prop="configName">
          <ElInput
            v-model="formData.configName"
            placeholder="例如：10元=100积分"
          />
        </ElFormItem>
        <ElFormItem label="充值类型" prop="type">
          <ElSelect v-model="formData.type" style="width: 100%">
            <ElOption
              v-for="item in typeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="支付金额" prop="payAmount">
          <ElInputNumber
            v-model="formData.payAmount"
            :min="0.01"
            :precision="2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="到账金额" prop="rewardAmount">
          <ElInputNumber
            v-model="formData.rewardAmount"
            :min="0.01"
            :precision="formData.type === 'points' ? 0 : 2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="赠送金额">
          <ElInputNumber
            v-model="formData.bonusAmount"
            :min="0"
            :precision="formData.type === 'points' ? 0 : 2"
            style="width: 100%"
          />
        </ElFormItem>
        <ElFormItem label="排序">
          <ElInputNumber v-model="formData.sort" :min="0" style="width: 100%" />
        </ElFormItem>
        <ElFormItem label="状态" prop="status">
          <ElSwitch
            v-model="formData.status"
            :active-value="1"
            :inactive-value="0"
          />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="formData.remark"
            type="textarea"
            :rows="3"
            placeholder="可选备注"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="formLoading" @click="handleSubmit">
          保存
        </ElButton>
      </template>
    </ElDialog>
  </Page>
</template>

<style scoped>
.wallet-page :deep(.el-form--inline .el-form-item) {
  margin-bottom: 12px;
}
</style>
