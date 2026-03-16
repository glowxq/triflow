<script lang="ts" setup>
import type { WalletApi } from '#/api/wallet';

import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElDatePicker,
  ElForm,
  ElFormItem,
  ElInput,
  ElPagination,
  ElTable,
  ElTableColumn,
} from 'element-plus';

import { getSignInPage } from '#/api/wallet';

const tableData = ref<WalletApi.SignInVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<WalletApi.SignInQuery>({
  username: '',
  startDate: undefined,
  endDate: undefined,
});

const dateRange = ref<[Date, Date] | null>(null);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params: WalletApi.SignInQuery = {
      username: searchForm.username,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };

    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0].toISOString().slice(0, 10);
      params.endDate = dateRange.value[1].toISOString().slice(0, 10);
    }

    const res = await getSignInPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow ?? 0;
  } catch (error) {
    console.error('获取签到记录失败:', error);
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
          <ElFormItem label="签到日期">
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
              v-access:code="['wallet:signin:query']"
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
        <ElTableColumn label="用户" min-width="180">
          <template #default="{ row }">
            <div>{{ row.nickname || row.username }}</div>
            <div class="text-xs text-gray-400">{{ row.username }}</div>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="signDate" label="签到日期" width="140" />
        <ElTableColumn prop="points" label="积分" width="90" align="right" />
        <ElTableColumn
          prop="consecutiveDays"
          label="连续天数"
          width="110"
          align="center"
        />
        <ElTableColumn prop="createTime" label="创建时间" width="170" />
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
</template>
