<script lang="ts" setup>
import type { FileConfigApi } from '#/api/file';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCol,
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

import {
  createFileConfig,
  deleteFileConfig,
  deleteFileConfigBatch,
  getFileConfigById,
  getFileConfigPage,
  setDefaultFileConfig,
  testFileConfig,
  updateFileConfig,
} from '#/api/file';

// ==================== 状态定义 ====================

const tableData = ref<FileConfigApi.ConfigVO[]>([]);
const loading = ref(false);
const selectedRows = ref<FileConfigApi.ConfigVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<FileConfigApi.QueryParams>({
  keyword: '',
  storageType: undefined,
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<FileConfigApi.CreateParams & { id?: number }>({
  configName: '',
  configKey: '',
  storageType: 'local',
  isDefault: 0,
  bucketName: '',
  domain: '',
  basePath: '',
  endpoint: '',
  accessKey: '',
  secretKey: '',
  region: '',
  status: 1,
  remark: '',
});

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const storageTypeOptions = [
  { label: '本地存储', value: 'local' },
  { label: '阿里云 OSS', value: 'aliyun' },
  { label: '腾讯云 COS', value: 'tencent' },
  { label: '七牛云', value: 'qiniu' },
  { label: 'MinIO', value: 'minio' },
  { label: 'AWS S3', value: 's3' },
];

const isDefaultOptions = [
  { label: '是', value: 1, type: 'success' as const },
  { label: '否', value: 0, type: 'info' as const },
];

const formRules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入配置标识', trigger: 'blur' }],
  storageType: [
    { required: true, message: '请选择存储类型', trigger: 'change' },
  ],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

const isCloudStorage = computed(() => formData.storageType !== 'local');

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getFileConfigPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取文件配置列表失败');
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
  searchForm.storageType = undefined;
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

function handleSelectionChange(rows: FileConfigApi.ConfigVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.configName = '';
  formData.configKey = '';
  formData.storageType = 'local';
  formData.isDefault = 0;
  formData.bucketName = '';
  formData.domain = '';
  formData.basePath = '';
  formData.endpoint = '';
  formData.accessKey = '';
  formData.secretKey = '';
  formData.region = '';
  formData.status = 1;
  formData.remark = '';
}

function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增文件配置';
  dialogVisible.value = true;
}

async function handleEdit(row: FileConfigApi.ConfigVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑文件配置';
  formLoading.value = true;

  try {
    const detail = await getFileConfigById(row.id);
    Object.assign(formData, {
      id: detail.id,
      configName: detail.configName,
      configKey: detail.configKey || '',
      storageType: detail.storageType,
      isDefault: detail.isDefault,
      bucketName: detail.bucketName || '',
      domain: detail.domain || '',
      basePath: detail.basePath || '',
      endpoint: detail.endpoint || '',
      accessKey: detail.accessKey || '',
      secretKey: '',
      region: detail.region || '',
      status: detail.status,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取文件配置详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createFileConfig(formData as FileConfigApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateFileConfig(formData as FileConfigApi.UpdateParams);
      ElMessage.success('更新成功');
    }
    dialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error(dialogType.value === 'create' ? '创建失败' : '更新失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: FileConfigApi.ConfigVO) {
  if (row.isDefault === 1) {
    ElMessage.warning('默认配置不允许删除');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除配置「${row.configName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteFileConfig(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的配置');
    return;
  }
  const defaultConfigs = selectedRows.value.filter(
    (row) => row.isDefault === 1,
  );
  if (defaultConfigs.length > 0) {
    ElMessage.warning('选中的配置中包含默认配置，不允许删除');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个配置吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteFileConfigBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleSetDefault(row: FileConfigApi.ConfigVO) {
  if (row.isDefault === 1) {
    ElMessage.info('当前配置已是默认配置');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要将「${row.configName}」设为默认配置吗？`,
      '确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
      },
    );
    await setDefaultFileConfig(row.id);
    ElMessage.success('设置成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleTestConnection(row: FileConfigApi.ConfigVO) {
  try {
    ElMessage.info('正在测试连接...');
    const result = await testFileConfig(row.id);
    if (result) {
      ElMessage.success('连接测试成功');
    } else {
      ElMessage.error('连接测试失败');
    }
  } catch {
    ElMessage.error('连接测试失败');
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

function getIsDefaultOption(isDefault: number) {
  return (
    isDefaultOptions.find((item) => item.value === isDefault) ||
    isDefaultOptions[1]
  );
}

function getStorageTypeLabel(type: string) {
  return storageTypeOptions.find((item) => item.value === type)?.label || type;
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
              placeholder="配置名称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="存储类型">
            <ElSelect
              v-model="searchForm.storageType"
              placeholder="全部"
              clearable
              style="width: 150px"
            >
              <ElOption
                v-for="item in storageTypeOptions"
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
              v-access:code="['file:config:query']"
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
            v-access:code="['file:config:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增配置
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['file:config:delete']"
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
        class="flex-1"
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" width="50" align="center" />
        <ElTableColumn
          label="配置名称"
          prop="configName"
          min-width="120"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="配置标识"
          prop="configKey"
          min-width="120"
          show-overflow-tooltip
        />
        <ElTableColumn label="存储类型" width="120" align="center">
          <template #default="{ row }">
            {{ getStorageTypeLabel(row.storageType) }}
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="访问域名"
          prop="domain"
          min-width="200"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="存储桶"
          prop="bucketName"
          width="120"
          show-overflow-tooltip
        />
        <ElTableColumn label="默认" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getIsDefaultOption(row.isDefault).type" size="small">
              {{ getIsDefaultOption(row.isDefault).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="创建时间" prop="createTime" width="170" />
        <ElTableColumn label="操作" width="250" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="success"
              link
              size="small"
              v-access:code="['file:config:setDefault']"
              @click="handleSetDefault(row)"
            >
              设为默认
            </ElButton>
            <ElButton
              type="info"
              link
              size="small"
              v-access:code="['file:config:test']"
              @click="handleTestConnection(row)"
            >
              测试
            </ElButton>
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['file:config:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              :disabled="row.isDefault === 1"
              v-access:code="['file:config:delete']"
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
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <ElForm
        ref="formRef"
        v-loading="formLoading"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElRow :gutter="20">
          <ElCol :span="12">
            <ElFormItem label="配置名称" prop="configName">
              <ElInput
                v-model="formData.configName"
                placeholder="请输入配置名称"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="配置标识" prop="configKey">
              <ElInput
                v-model="formData.configKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入配置标识（唯一）"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="存储类型" prop="storageType">
              <ElSelect
                v-model="formData.storageType"
                :disabled="dialogType === 'edit'"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in storageTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="访问域名">
              <ElInput
                v-model="formData.domain"
                placeholder="如：https://cdn.example.com"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="基础路径">
              <ElInput v-model="formData.basePath" placeholder="如：/upload" />
            </ElFormItem>
          </ElCol>

          <!-- 云存储配置 -->
          <template v-if="isCloudStorage">
            <ElCol :span="12">
              <ElFormItem label="端点地址">
                <ElInput
                  v-model="formData.endpoint"
                  placeholder="请输入端点地址"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="存储桶名称">
                <ElInput
                  v-model="formData.bucketName"
                  placeholder="请输入存储桶名称"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="Access Key">
                <ElInput
                  v-model="formData.accessKey"
                  placeholder="请输入 Access Key"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="Secret Key">
                <ElInput
                  v-model="formData.secretKey"
                  type="password"
                  show-password
                  :placeholder="
                    dialogType === 'edit' ? '留空则不修改' : '请输入 Secret Key'
                  "
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="区域">
                <ElInput v-model="formData.region" placeholder="请输入区域" />
              </ElFormItem>
            </ElCol>
          </template>

          <ElCol :span="12">
            <ElFormItem label="是否默认">
              <ElSelect
                v-model="formData.isDefault"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in isDefaultOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="状态">
              <ElSelect
                v-model="formData.status"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in statusOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="24">
            <ElFormItem label="备注">
              <ElInput
                v-model="formData.remark"
                type="textarea"
                :rows="2"
                placeholder="请输入备注"
              />
            </ElFormItem>
          </ElCol>
        </ElRow>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="formLoading" @click="handleSubmit">
          确定
        </ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
