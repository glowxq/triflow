<script lang="ts" setup>
import type { EnumItem } from '#/api/base/enum';
import type { NotifySubscribeApi } from '#/api/notify';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElMessageBox,
  ElPagination,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import { getEnumOptions } from '#/api/base/enum';
import {
  deleteNotifySubscribe,
  deleteNotifySubscribeBatch,
  getNotifySubscribePage,
  updateNotifySubscribeStatus,
} from '#/api/notify';
import { EnumSelect } from '#/components/enum-select';

// ==================== 状态定义 ====================

const tableData = ref<NotifySubscribeApi.SubscribeVO[]>([]);
const loading = ref(false);
const selectedRows = ref<NotifySubscribeApi.SubscribeVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<NotifySubscribeApi.QueryParams>({
  keyword: '',
  userId: undefined,
  channel: undefined,
  subscribeStatus: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();
const formLoading = ref(false);
const formData = reactive<NotifySubscribeApi.UpdateStatusParams>({
  id: 0,
  subscribeStatus: '',
});

const channelOptions = ref<EnumItem[]>([]);
const statusOptions = ref<EnumItem[]>([]);

// ==================== 常量 ====================

const statusTagMap: Record<string, 'danger' | 'info' | 'success' | 'warning'> =
  {
    accept: 'success',
    reject: 'info',
    ban: 'danger',
  };

const formRules = {
  subscribeStatus: [
    { required: true, message: '请选择订阅状态', trigger: 'change' },
  ],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

// ==================== 方法 ====================

async function fetchEnums() {
  try {
    const [channels, statuses] = await Promise.all([
      getEnumOptions('NotifyChannelEnum'),
      getEnumOptions('NotifySubscribeStatusEnum'),
    ]);
    channelOptions.value = channels;
    statusOptions.value = statuses;
  } catch (error) {
    console.error('加载枚举失败:', error);
  }
}

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getNotifySubscribePage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取订阅列表失败');
  } finally {
    loading.value = false;
  }
}

function getEnumLabel(options: EnumItem[], value?: string) {
  if (!value) return '';
  return options.find((item) => item.code === value)?.name || value;
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.userId = undefined;
  searchForm.channel = undefined;
  searchForm.subscribeStatus = undefined;
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

function handleSelectionChange(rows: NotifySubscribeApi.SubscribeVO[]) {
  selectedRows.value = rows;
}

function openUpdateDialog(row: NotifySubscribeApi.SubscribeVO) {
  formData.id = row.id;
  formData.subscribeStatus = row.subscribeStatus;
  dialogTitle.value = `更新订阅状态 - ${row.templateKey}`;
  dialogVisible.value = true;
}

function handleDialogClose() {
  formData.id = 0;
  formData.subscribeStatus = '';
}

async function handleSubmit() {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
  } catch {
    return;
  }

  formLoading.value = true;
  try {
    await updateNotifySubscribeStatus({
      id: formData.id,
      subscribeStatus: formData.subscribeStatus,
    });
    ElMessage.success('订阅状态更新成功');
    dialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('订阅状态更新失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: NotifySubscribeApi.SubscribeVO) {
  try {
    await ElMessageBox.confirm(
      `确认删除订阅记录「${row.templateKey || row.templateId}」吗？`,
      '提示',
      { type: 'warning' },
    );
    await deleteNotifySubscribe(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败');
    }
  }
}

async function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedRows.value.length} 条订阅记录吗？`,
      '提示',
      { type: 'warning' },
    );
    const ids = selectedRows.value.map((item) => item.id);
    await deleteNotifySubscribeBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
}

onMounted(async () => {
  await fetchEnums();
  fetchData();
});
</script>

<template>
  <Page auto-content-height>
    <ElForm :model="searchForm" inline label-width="80px" class="mb-4">
      <ElFormItem label="关键词">
        <ElInput
          v-model="searchForm.keyword"
          placeholder="模板标识/模板ID"
          clearable
        />
      </ElFormItem>
      <ElFormItem label="用户ID">
        <ElInput v-model.number="searchForm.userId" placeholder="输入用户ID" />
      </ElFormItem>
      <ElFormItem label="渠道">
        <EnumSelect
          v-model="searchForm.channel"
          enum-class="NotifyChannelEnum"
          placeholder="请选择渠道"
          clearable
          class="query-select"
        />
      </ElFormItem>
      <ElFormItem label="状态">
        <EnumSelect
          v-model="searchForm.subscribeStatus"
          enum-class="NotifySubscribeStatusEnum"
          placeholder="请选择状态"
          clearable
          class="query-select"
        />
      </ElFormItem>
      <ElFormItem>
        <ElButton
          type="primary"
          v-access:code="['wechat:subscribe:query']"
          @click="handleSearch"
        >
          查询
        </ElButton>
        <ElButton @click="handleReset">重置</ElButton>
      </ElFormItem>
    </ElForm>

    <div class="mb-4 flex items-center gap-2">
      <ElButton
        type="danger"
        :disabled="!hasSelected"
        v-access:code="['wechat:subscribe:deleteBatch']"
        @click="handleBatchDelete"
      >
        批量删除
      </ElButton>
    </div>

    <ElTable
      v-loading="loading"
      :data="tableData"
      row-key="id"
      @selection-change="handleSelectionChange"
    >
      <ElTableColumn type="selection" width="55" />
      <ElTableColumn prop="userId" label="用户ID" width="100" />
      <ElTableColumn prop="templateKey" label="模板标识" min-width="160" />
      <ElTableColumn prop="templateId" label="模板ID" min-width="160" />
      <ElTableColumn prop="channel" label="渠道" min-width="120">
        <template #default="{ row }">
          <ElTag type="info">
            {{ getEnumLabel(channelOptions, row.channel) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="subscribeStatus" label="订阅状态" min-width="120">
        <template #default="{ row }">
          <ElTag :type="statusTagMap[row.subscribeStatus] || 'info'">
            {{ getEnumLabel(statusOptions, row.subscribeStatus) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="subscribeTime" label="订阅时间" min-width="180" />
      <ElTableColumn label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <ElButton
            type="primary"
            link
            v-access:code="['wechat:subscribe:update']"
            @click="openUpdateDialog(row)"
          >
            更新状态
          </ElButton>
          <ElButton
            type="danger"
            link
            v-access:code="['wechat:subscribe:delete']"
            @click="handleDelete(row)"
          >
            删除
          </ElButton>
        </template>
      </ElTableColumn>
    </ElTable>

    <div class="mt-4 flex justify-end">
      <ElPagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        :page-size="pagination.pageSize"
        :current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="480px"
      @close="handleDialogClose"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElFormItem label="订阅状态" prop="subscribeStatus">
          <EnumSelect
            v-model="formData.subscribeStatus"
            enum-class="NotifySubscribeStatusEnum"
            placeholder="请选择订阅状态"
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
.query-select {
  min-width: 200px;
}
</style>
