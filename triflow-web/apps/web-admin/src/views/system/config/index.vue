<script lang="ts" setup>
import type { ConfigEnumItem, SysConfigApi } from '#/api/system';

import { computed, onMounted, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCol,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElPagination,
  ElRow,
  ElSelect,
  ElSwitch,
  ElTable,
  ElTableColumn,
  ElTag,
  ElUpload,
} from 'element-plus';

import {
  createConfig,
  deleteConfig,
  deleteConfigBatch,
  downloadConfigTemplate,
  exportConfigList,
  getConfigById,
  getConfigCategoryEnums,
  getConfigPage,
  getConfigValueTypeEnums,
  importConfigData,
  refreshConfigCache,
  updateConfig,
} from '#/api/system';
import MonacoEditor from '#/components/monaco-editor/index.vue';

// ==================== 状态定义 ====================

const tableData = ref<SysConfigApi.ConfigVO[]>([]);
const loading = ref(false);
const selectedRows = ref<SysConfigApi.ConfigVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<SysConfigApi.QueryParams>({
  keyword: '',
  status: undefined,
  category: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<SysConfigApi.CreateParams & { id?: number }>({
  configKey: '',
  configName: '',
  configValue: '',
  category: '',
  valueType: 'string',
  configType: 1,
  sort: 0,
  status: 1,
  remark: '',
});

// 枚举数据
const categoryEnums = ref<ConfigEnumItem[]>([]);
const valueTypeEnums = ref<ConfigEnumItem[]>([]);

// JSON 校验状态
const jsonError = ref('');

// Monaco Editor 引用
const jsonEditorRef = ref<InstanceType<typeof MonacoEditor>>();
const arrayEditorRef = ref<InstanceType<typeof MonacoEditor>>();

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const isSystemOptions = [
  { label: '是', value: 0, type: 'warning' as const },
  { label: '否', value: 1, type: 'info' as const },
];

const formRules = {
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

// 用于布尔值类型的双向绑定
const booleanValue = computed({
  get: () => formData.configValue === 'true',
  set: (val: boolean) => {
    formData.configValue = val ? 'true' : 'false';
  },
});

// 用于数值类型的双向绑定
const numberValue = computed({
  get: () => {
    const num = Number(formData.configValue);
    return Number.isNaN(num) ? 0 : num;
  },
  set: (val: number | undefined) => {
    formData.configValue = String(val ?? 0);
  },
});

// ==================== 方法 ====================

/**
 * 加载枚举数据
 */
async function fetchEnums() {
  try {
    const [categories, valueTypes] = await Promise.all([
      getConfigCategoryEnums(),
      getConfigValueTypeEnums(),
    ]);
    categoryEnums.value = categories;
    valueTypeEnums.value = valueTypes;
  } catch (error) {
    console.error('加载枚举数据失败:', error);
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
    const res = await getConfigPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取配置列表失败');
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
  searchForm.status = undefined;
  searchForm.category = undefined;
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

function handleSelectionChange(rows: SysConfigApi.ConfigVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.configKey = '';
  formData.configName = '';
  formData.configValue = '';
  formData.category = '';
  formData.valueType = 'string';
  formData.configType = 1;
  formData.sort = 0;
  formData.status = 1;
  formData.remark = '';
  jsonError.value = '';
}

async function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增配置';

  // 确保枚举数据已加载
  if (categoryEnums.value.length === 0) {
    await fetchEnums();
  }

  dialogVisible.value = true;
}

async function handleEdit(row: SysConfigApi.ConfigVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑配置';
  formLoading.value = true;

  try {
    // 确保枚举数据已加载
    if (categoryEnums.value.length === 0) {
      await fetchEnums();
    }

    const detail = await getConfigById(row.id);
    Object.assign(formData, {
      id: detail.id,
      configKey: detail.configKey,
      configName: detail.configName || '',
      configValue: detail.configValue || '',
      category: detail.category || '',
      valueType: detail.valueType || 'string',
      configType: detail.configType ?? 1,
      sort: detail.sort ?? 0,
      status: detail.status,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取配置详情失败');
  } finally {
    formLoading.value = false;
  }
}

/**
 * 校验 JSON 格式
 */
function validateJson(value: string): boolean {
  if (!value || value.trim() === '') {
    jsonError.value = '';
    return true;
  }
  try {
    JSON.parse(value);
    jsonError.value = '';
    return true;
  } catch {
    jsonError.value = 'JSON 格式不正确';
    return false;
  }
}

/**
 * 格式化 JSON（使用 Monaco Editor 的格式化功能）
 */
function formatJsonCode() {
  if (!formData.configValue) return;
  try {
    const parsed = JSON.parse(formData.configValue);
    formData.configValue = JSON.stringify(parsed, null, 2);
    jsonError.value = '';
    // 触发 Monaco Editor 格式化
    jsonEditorRef.value?.formatCode();
  } catch {
    ElMessage.warning('JSON 格式不正确，无法格式化');
  }
}

/**
 * 格式化数组
 */
function formatArrayCode() {
  if (!formData.configValue) return;
  try {
    const parsed = JSON.parse(formData.configValue);
    if (!Array.isArray(parsed)) {
      ElMessage.warning('配置值不是有效的数组格式');
      return;
    }
    formData.configValue = JSON.stringify(parsed, null, 2);
    jsonError.value = '';
    // 触发 Monaco Editor 格式化
    arrayEditorRef.value?.formatCode();
  } catch {
    ElMessage.warning('JSON 数组格式不正确，无法格式化');
  }
}

async function handleSubmit() {
  // 如果是 JSON 或数组类型，先校验格式
  if (
    (formData.valueType === 'json' || formData.valueType === 'array') &&
    !validateJson(formData.configValue)
  ) {
    ElMessage.warning('请输入正确的 JSON 格式');
    return;
  }

  // 如果是数组类型，额外校验是否为数组
  if (formData.valueType === 'array' && formData.configValue) {
    try {
      const parsed = JSON.parse(formData.configValue);
      if (!Array.isArray(parsed)) {
        ElMessage.warning('请输入有效的 JSON 数组格式');
        return;
      }
    } catch {
      ElMessage.warning('请输入正确的 JSON 数组格式');
      return;
    }
  }

  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createConfig(formData as SysConfigApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateConfig(formData as SysConfigApi.UpdateParams);
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

async function handleDelete(row: SysConfigApi.ConfigVO) {
  if (row.configType === 0) {
    ElMessage.warning('系统内置配置不允许删除');
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
    await deleteConfig(row.id);
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
  const systemConfigs = selectedRows.value.filter(
    (row) => row.configType === 0,
  );
  if (systemConfigs.length > 0) {
    ElMessage.warning('选中的配置中包含系统内置配置，不允许删除');
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
    await deleteConfigBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleRefreshCache() {
  try {
    await refreshConfigCache();
    ElMessage.success('缓存刷新成功');
  } catch {
    ElMessage.error('缓存刷新失败');
  }
}

// ==================== 导入导出 ====================

async function handleExport() {
  try {
    const blob = await exportConfigList(searchForm);
    const url = window.URL.createObjectURL(blob as Blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '系统配置.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

async function handleImport(file: File) {
  try {
    await importConfigData(file);
    ElMessage.success('导入成功');
    fetchData();
  } catch {
    ElMessage.error('导入失败');
  }
  return false; // 阻止默认上传行为
}

async function handleDownloadTemplate() {
  try {
    const blob = await downloadConfigTemplate();
    const url = window.URL.createObjectURL(blob as Blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = '配置导入模板.xlsx';
    document.body.append(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
    ElMessage.success('模板下载成功');
  } catch {
    ElMessage.error('模板下载失败');
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

function getIsSystemOption(isSystem?: number) {
  return (
    isSystemOptions.find((item) => item.value === isSystem) ||
    isSystemOptions[1]
  );
}

function getCategoryLabel(code?: string) {
  if (!code) return '';
  return categoryEnums.value.find((e) => e.code === code)?.name || code;
}

function getValueTypeLabel(code?: string) {
  if (!code) return '';
  return valueTypeEnums.value.find((e) => e.code === code)?.name || code;
}

// 监听值类型变化，重置配置值
watch(
  () => formData.valueType,
  (newType, oldType) => {
    if (newType !== oldType && dialogVisible.value) {
      // 切换类型时重置配置值
      switch (newType) {
        case 'array': {
          formData.configValue = '[]';

          break;
        }
        case 'boolean': {
          formData.configValue = 'false';

          break;
        }
        case 'json': {
          formData.configValue = '{}';

          break;
        }
        case 'number': {
          formData.configValue = '0';

          break;
        }
        default: {
          formData.configValue = '';
        }
      }
      jsonError.value = '';
    }
  },
);

// ==================== 生命周期 ====================

onMounted(() => {
  fetchData();
  fetchEnums();
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
              placeholder="配置键/配置名称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
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
          <ElFormItem label="分类">
            <ElSelect
              v-model="searchForm.category"
              placeholder="全部"
              clearable
              style="width: 150px"
            >
              <ElOption
                v-for="item in categoryEnums"
                :key="item.code"
                :label="item.name"
                :value="item.code"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['system:config:query']"
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
            v-access:code="['system:config:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增配置
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:config:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton type="success" plain @click="handleExport">
            <span class="i-lucide-download mr-1"></span>
            导出
          </ElButton>
          <ElUpload
            :show-file-list="false"
            accept=".xlsx,.xls"
            :before-upload="handleImport"
          >
            <ElButton type="info" plain>
              <span class="i-lucide-upload mr-1"></span>
              导入
            </ElButton>
          </ElUpload>
          <ElButton plain @click="handleDownloadTemplate">
            <span class="i-lucide-file-text mr-1"></span>
            下载模板
          </ElButton>
          <ElButton
            type="warning"
            plain
            v-access:code="['system:config:refresh']"
            @click="handleRefreshCache"
          >
            <span class="i-lucide-refresh-cw mr-1"></span>
            刷新缓存
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
          label="配置键"
          prop="configKey"
          min-width="180"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="配置名称"
          prop="configName"
          min-width="150"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="配置值"
          prop="configValue"
          min-width="200"
          show-overflow-tooltip
        />
        <ElTableColumn label="分类" width="100" align="center">
          <template #default="{ row }">
            {{ getCategoryLabel(row.category) || row.category }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="值类型" width="90" align="center">
          <template #default="{ row }">
            {{ getValueTypeLabel(row.valueType) || row.valueType }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="系统内置" width="90" align="center">
          <template #default="{ row }">
            <ElTag :type="getIsSystemOption(row.configType).type" size="small">
              {{ getIsSystemOption(row.configType).label }}
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
        <ElTableColumn label="操作" width="150" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:config:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              :disabled="row.configType === 0"
              v-access:code="['system:config:delete']"
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
      width="650px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <ElForm
        ref="formRef"
        v-loading="formLoading"
        :model="formData"
        :rules="formRules"
        label-width="85px"
      >
        <ElRow :gutter="20">
          <ElCol :span="12">
            <ElFormItem label="配置键" prop="configKey">
              <ElInput
                v-model="formData.configKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入配置键"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="配置名称" prop="configName">
              <ElInput
                v-model="formData.configName"
                placeholder="请输入配置名称"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="分类">
              <ElSelect
                v-model="formData.category"
                placeholder="请选择分类"
                style="width: 100%"
                clearable
              >
                <ElOption
                  v-for="item in categoryEnums"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="值类型">
              <ElSelect
                v-model="formData.valueType"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in valueTypeEnums"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="系统内置">
              <ElSelect
                v-model="formData.configType"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in isSystemOptions"
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
            <ElFormItem label="配置值" prop="configValue">
              <!-- 字符串类型：文本域 -->
              <ElInput
                v-if="formData.valueType === 'string'"
                v-model="formData.configValue"
                type="textarea"
                :rows="3"
                placeholder="请输入配置值"
              />

              <!-- 数值类型：数字输入框 -->
              <ElInputNumber
                v-else-if="formData.valueType === 'number'"
                v-model="numberValue"
                style="width: 100%"
                :controls="true"
                placeholder="请输入数值"
              />

              <!-- 布尔类型：开关 -->
              <div v-else-if="formData.valueType === 'boolean'" class="w-full">
                <ElSwitch
                  v-model="booleanValue"
                  active-text="true"
                  inactive-text="false"
                />
                <span class="ml-3 text-sm text-gray-500">
                  当前值: {{ formData.configValue }}
                </span>
              </div>

              <!-- JSON 类型：Monaco Editor -->
              <div
                v-else-if="formData.valueType === 'json'"
                class="w-full space-y-2"
              >
                <MonacoEditor
                  ref="jsonEditorRef"
                  v-model="formData.configValue"
                  language="json"
                  :height="200"
                  @blur="validateJson(formData.configValue)"
                />
                <div class="flex items-center justify-between">
                  <span v-if="jsonError" class="text-xs text-red-500">
                    {{ jsonError }}
                  </span>
                  <span v-else class="text-xs text-green-500">
                    JSON 格式正确
                  </span>
                  <ElButton
                    type="primary"
                    link
                    size="small"
                    @click="formatJsonCode"
                  >
                    格式化 JSON
                  </ElButton>
                </div>
              </div>

              <!-- 数组类型：Monaco Editor -->
              <div
                v-else-if="formData.valueType === 'array'"
                class="w-full space-y-2"
              >
                <MonacoEditor
                  ref="arrayEditorRef"
                  v-model="formData.configValue"
                  language="json"
                  :height="160"
                  @blur="validateJson(formData.configValue)"
                />
                <div class="flex items-center justify-between">
                  <span v-if="jsonError" class="text-xs text-red-500">
                    {{ jsonError }}
                  </span>
                  <span v-else class="text-xs text-green-500">
                    JSON 数组格式正确
                  </span>
                  <ElButton
                    type="primary"
                    link
                    size="small"
                    @click="formatArrayCode"
                  >
                    格式化数组
                  </ElButton>
                </div>
              </div>

              <!-- 默认：文本域 -->
              <ElInput
                v-else
                v-model="formData.configValue"
                type="textarea"
                :rows="3"
                placeholder="请输入配置值"
              />
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
