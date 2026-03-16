<script lang="ts" setup>
import type {
  EnumItem,
  SwitchEnumCompareResult,
  SysSwitchApi,
} from '#/api/system';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCol,
  ElDatePicker,
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
} from 'element-plus';

import {
  compareSwitchWithEnum,
  createSwitch,
  deleteNotInEnum,
  deleteSwitch,
  deleteSwitchBatch,
  getSwitchById,
  getSwitchCategoryEnums,
  getSwitchLogPage,
  getSwitchPage,
  getSwitchScopeEnums,
  getSwitchStrategyEnums,
  getSwitchTypeEnums,
  initFromEnum,
  refreshSwitchCache,
  toggleSwitch,
  updateSwitch,
} from '#/api/system';

// ==================== 状态定义 ====================

const tableData = ref<SysSwitchApi.SwitchVO[]>([]);
const loading = ref(false);
const selectedRows = ref<SysSwitchApi.SwitchVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<SysSwitchApi.QueryParams>({
  keyword: '',
  switchKey: '',
  category: undefined,
  switchType: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<SysSwitchApi.CreateParams & { id?: number }>({
  switchKey: '',
  switchName: '',
  switchType: 'basic',
  category: '',
  switchValue: 0,
  grayStrategy: undefined,
  grayWhitelist: '',
  grayPercent: undefined,
  effectiveTime: undefined,
  expireTime: undefined,
  status: 1,
  remark: '',
});

// 日志弹窗
const logDialogVisible = ref(false);
const logTableData = ref<SysSwitchApi.SwitchLogVO[]>([]);
const logLoading = ref(false);
const logPagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const currentSwitchId = ref<number>(0);

// 切换开关弹窗
const toggleDialogVisible = ref(false);
const toggleSwitchId = ref<number>(0);
const toggleSwitchName = ref('');
const toggleNewValue = ref<number>(0);
const changeReason = ref('');

// 枚举数据（从后端加载）
const categoryEnums = ref<EnumItem[]>([]);
const typeEnums = ref<EnumItem[]>([]);
const scopeEnums = ref<EnumItem[]>([]);
const strategyEnums = ref<EnumItem[]>([]);
const enumsLoading = ref(false);

// 枚举对比弹窗
const compareDialogVisible = ref(false);
const compareLoading = ref(false);
const compareResult = ref<null | SwitchEnumCompareResult>(null);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const switchValueOptions = [
  { label: '开启', value: 1, type: 'success' as const },
  { label: '关闭', value: 0, type: 'info' as const },
];

const formRules = {
  switchKey: [{ required: true, message: '请输入开关标识', trigger: 'blur' }],
  switchName: [{ required: true, message: '请输入开关名称', trigger: 'blur' }],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

const isGraySwitch = computed(() => formData.switchType === 'gray');

const isTimedSwitch = computed(() => formData.switchType === 'timed');

// 从枚举获取标签
const getCategoryLabel = (code?: string) => {
  if (!code) return '';
  return categoryEnums.value.find((e) => e.code === code)?.name || code;
};

const getTypeLabel = (code?: string) => {
  if (!code) return '';
  return typeEnums.value.find((e) => e.code === code)?.name || code;
};

// ==================== 方法 ====================

/**
 * 加载枚举数据
 */
async function fetchEnums() {
  enumsLoading.value = true;
  try {
    const [categories, types, scopes, strategies] = await Promise.all([
      getSwitchCategoryEnums(),
      getSwitchTypeEnums(),
      getSwitchScopeEnums(),
      getSwitchStrategyEnums(),
    ]);
    categoryEnums.value = categories;
    typeEnums.value = types;
    scopeEnums.value = scopes;
    strategyEnums.value = strategies;
  } catch (error) {
    console.error('加载枚举数据失败:', error);
  } finally {
    enumsLoading.value = false;
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
    const res = await getSwitchPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取开关列表失败');
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
  searchForm.switchKey = '';
  searchForm.category = undefined;
  searchForm.switchType = undefined;
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

function handleSelectionChange(rows: SysSwitchApi.SwitchVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.switchKey = '';
  formData.switchName = '';
  formData.switchType = 'basic';
  formData.category = '';
  formData.switchValue = 0;
  formData.grayStrategy = undefined;
  formData.grayWhitelist = '';
  formData.grayPercent = undefined;
  formData.effectiveTime = undefined;
  formData.expireTime = undefined;
  formData.status = 1;
  formData.remark = '';
}

function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增开关';
  dialogVisible.value = true;
}

async function handleEdit(row: SysSwitchApi.SwitchVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑开关';
  formLoading.value = true;

  try {
    const detail = await getSwitchById(row.id);
    Object.assign(formData, {
      id: detail.id,
      switchKey: detail.switchKey,
      switchName: detail.switchName,
      switchType: detail.switchType || 'basic',
      category: detail.category || '',
      switchValue: detail.switchValue,
      grayStrategy: detail.grayStrategy,
      grayWhitelist: detail.grayWhitelist || '',
      grayPercent: detail.grayPercent,
      effectiveTime: detail.effectiveTime,
      expireTime: detail.expireTime,
      status: detail.status,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取开关详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createSwitch(formData as SysSwitchApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateSwitch(formData as SysSwitchApi.UpdateParams);
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

async function handleDelete(row: SysSwitchApi.SwitchVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除开关「${row.switchName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteSwitch(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的开关');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个开关吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteSwitchBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

function handleToggle(row: SysSwitchApi.SwitchVO) {
  toggleSwitchId.value = row.id;
  toggleSwitchName.value = row.switchName;
  toggleNewValue.value = row.switchValue === 1 ? 0 : 1;
  changeReason.value = '';
  toggleDialogVisible.value = true;
}

async function confirmToggle() {
  try {
    await toggleSwitch(
      toggleSwitchId.value,
      toggleNewValue.value,
      changeReason.value,
    );
    ElMessage.success('开关状态切换成功');
    toggleDialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('开关状态切换失败');
  }
}

async function handleRefreshCache() {
  try {
    await refreshSwitchCache();
    ElMessage.success('缓存刷新成功');
  } catch {
    ElMessage.error('缓存刷新失败');
  }
}

async function handleViewLogs(row: SysSwitchApi.SwitchVO) {
  currentSwitchId.value = row.id;
  logPagination.pageNum = 1;
  await fetchLogData();
  logDialogVisible.value = true;
}

async function fetchLogData() {
  logLoading.value = true;
  try {
    const res = await getSwitchLogPage(
      currentSwitchId.value,
      logPagination.pageNum,
      logPagination.pageSize,
    );
    logTableData.value = res.records;
    logPagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取操作日志失败');
  } finally {
    logLoading.value = false;
  }
}

function handleLogSizeChange(size: number) {
  logPagination.pageSize = size;
  logPagination.pageNum = 1;
  fetchLogData();
}

function handleLogCurrentChange(page: number) {
  logPagination.pageNum = page;
  fetchLogData();
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

function getSwitchValueOption(value: number) {
  return (
    switchValueOptions.find((item) => item.value === value) ||
    switchValueOptions[1]
  );
}

function getSwitchTypeLabel(type?: string) {
  return getTypeLabel(type) || '未知类型';
}

// ==================== 枚举对比功能 ====================

/**
 * 打开枚举对比弹窗
 */
async function handleCompareEnum() {
  compareDialogVisible.value = true;
  compareLoading.value = true;
  try {
    compareResult.value = await compareSwitchWithEnum();
  } catch {
    ElMessage.error('获取对比结果失败');
  } finally {
    compareLoading.value = false;
  }
}

/**
 * 一键删除枚举中不存在的开关
 */
async function handleDeleteNotInEnum() {
  if (!compareResult.value?.missingInEnum.length) {
    ElMessage.warning('没有需要删除的开关');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除数据库中存在但枚举中未定义的 ${compareResult.value.missingInEnum.length} 个开关吗？此操作不可恢复！`,
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const count = await deleteNotInEnum();
    ElMessage.success(`成功删除 ${count} 个开关`);
    // 刷新对比结果
    await handleCompareEnum();
    // 刷新列表
    fetchData();
  } catch {
    // 用户取消
  }
}

/**
 * 一键初始化枚举中的开关
 */
async function handleInitFromEnum() {
  if (!compareResult.value?.missingInDatabase.length) {
    ElMessage.warning('没有需要初始化的开关');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要将枚举中定义但数据库不存在的 ${compareResult.value.missingInDatabase.length} 个开关初始化到数据库吗？`,
      '初始化确认',
      {
        confirmButtonText: '确定初始化',
        cancelButtonText: '取消',
        type: 'info',
      },
    );
    const count = await initFromEnum();
    ElMessage.success(`成功初始化 ${count} 个开关`);
    // 刷新对比结果
    await handleCompareEnum();
    // 刷新列表
    fetchData();
  } catch {
    // 用户取消
  }
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchEnums();
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
              placeholder="开关标识/开关名称"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="开关标识">
            <ElInput
              v-model="searchForm.switchKey"
              placeholder="精确匹配标识"
              clearable
              style="width: 220px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="开关类型">
            <ElSelect
              v-model="searchForm.switchType"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in typeEnums"
                :key="item.code"
                :label="item.name"
                :value="item.code"
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
              v-access:code="['system:switch:query']"
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
            v-access:code="['system:switch:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增开关
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:switch:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton type="info" plain @click="handleCompareEnum">
            <span class="i-lucide-git-compare mr-1"></span>
            枚举对比
          </ElButton>
          <ElButton
            type="warning"
            plain
            v-access:code="['system:switch:refresh']"
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
          label="开关标识"
          prop="switchKey"
          min-width="180"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="开关名称"
          prop="switchName"
          min-width="150"
          show-overflow-tooltip
        />
        <ElTableColumn label="开关类型" width="100" align="center">
          <template #default="{ row }">
            {{ getSwitchTypeLabel(row.switchType) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="分类" width="100" align="center">
          <template #default="{ row }">
            {{ getCategoryLabel(row.category) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="开关状态" width="100" align="center">
          <template #default="{ row }">
            <ElTag
              :type="getSwitchValueOption(row.switchValue).type"
              size="small"
            >
              {{ getSwitchValueOption(row.switchValue).label }}
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
        <ElTableColumn label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="success"
              link
              size="small"
              v-access:code="['system:switch:toggle']"
              @click="handleToggle(row)"
            >
              切换
            </ElButton>
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:switch:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="info"
              link
              size="small"
              @click="handleViewLogs(row)"
            >
              日志
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['system:switch:delete']"
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
            <ElFormItem label="开关标识" prop="switchKey">
              <ElInput
                v-model="formData.switchKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入开关标识"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="开关名称" prop="switchName">
              <ElInput
                v-model="formData.switchName"
                placeholder="请输入开关名称"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="开关类型">
              <ElSelect
                v-model="formData.switchType"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in typeEnums"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="分类">
              <ElSelect
                v-model="formData.category"
                placeholder="请选择"
                style="width: 100%"
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
            <ElFormItem label="开关状态">
              <ElSwitch
                v-model="formData.switchValue"
                :active-value="1"
                :inactive-value="0"
                active-text="开"
                inactive-text="关"
              />
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

          <!-- 灰度开关配置 -->
          <template v-if="isGraySwitch">
            <ElCol :span="12">
              <ElFormItem label="灰度策略">
                <ElSelect
                  v-model="formData.grayStrategy"
                  placeholder="请选择"
                  style="width: 100%"
                >
                  <ElOption
                    v-for="item in strategyEnums"
                    :key="item.code"
                    :label="item.name"
                    :value="item.code"
                  />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="灰度比例">
                <ElInputNumber
                  v-model="formData.grayPercent"
                  :min="0"
                  :max="100"
                  placeholder="0-100"
                  style="width: 100%"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="24">
              <ElFormItem label="灰度白名单">
                <ElInput
                  v-model="formData.grayWhitelist"
                  type="textarea"
                  :rows="2"
                  placeholder="用户ID列表，多个用逗号分隔"
                />
              </ElFormItem>
            </ElCol>
          </template>

          <!-- 定时开关配置 -->
          <template v-if="isTimedSwitch">
            <ElCol :span="12">
              <ElFormItem label="生效时间">
                <ElDatePicker
                  v-model="formData.effectiveTime"
                  type="datetime"
                  placeholder="选择生效时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="过期时间">
                <ElDatePicker
                  v-model="formData.expireTime"
                  type="datetime"
                  placeholder="选择过期时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  style="width: 100%"
                />
              </ElFormItem>
            </ElCol>
          </template>

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

    <!-- 切换开关弹窗 -->
    <ElDialog
      v-model="toggleDialogVisible"
      title="切换开关状态"
      width="450px"
      :close-on-click-modal="false"
    >
      <div class="mb-4">
        <p class="text-gray-600">
          确定要将开关「{{ toggleSwitchName }}」切换为
          <span
            :class="toggleNewValue === 1 ? 'text-green-600' : 'text-gray-500'"
          >
            {{ toggleNewValue === 1 ? '开启' : '关闭' }}
          </span>
          状态吗？
        </p>
      </div>
      <ElForm label-width="80px">
        <ElFormItem label="变更原因">
          <ElInput
            v-model="changeReason"
            type="textarea"
            :rows="2"
            placeholder="请输入变更原因（可选）"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="toggleDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="confirmToggle">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 操作日志弹窗 -->
    <ElDialog
      v-model="logDialogVisible"
      title="操作日志"
      width="800px"
      :close-on-click-modal="false"
    >
      <ElTable
        v-loading="logLoading"
        :data="logTableData"
        border
        stripe
        max-height="400"
      >
        <ElTableColumn label="操作时间" prop="operateTime" width="170" />
        <ElTableColumn label="操作人" prop="operatorName" width="100" />
        <ElTableColumn label="原状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getSwitchValueOption(row.oldValue).type" size="small">
              {{ getSwitchValueOption(row.oldValue).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="新状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getSwitchValueOption(row.newValue).type" size="small">
              {{ getSwitchValueOption(row.newValue).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="变更原因"
          prop="changeReason"
          show-overflow-tooltip
        />
      </ElTable>
      <div class="mt-4 flex justify-end">
        <ElPagination
          v-model:current-page="logPagination.pageNum"
          v-model:page-size="logPagination.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="logPagination.total"
          background
          layout="total, sizes, prev, pager, next"
          @size-change="handleLogSizeChange"
          @current-change="handleLogCurrentChange"
        />
      </div>
    </ElDialog>

    <!-- 枚举对比弹窗 -->
    <ElDialog
      v-model="compareDialogVisible"
      title="枚举对比"
      width="900px"
      :close-on-click-modal="false"
    >
      <div v-loading="compareLoading">
        <template v-if="compareResult">
          <!-- 统计信息 -->
          <div class="mb-4 flex gap-4">
            <div class="rounded-lg bg-blue-50 px-4 py-2">
              <span class="text-gray-600">枚举定义数量：</span>
              <span class="text-lg font-bold text-blue-600">{{
                compareResult.enumCount
              }}</span>
            </div>
            <div class="rounded-lg bg-green-50 px-4 py-2">
              <span class="text-gray-600">数据库开关数量：</span>
              <span class="text-lg font-bold text-green-600">{{
                compareResult.databaseCount
              }}</span>
            </div>
            <div class="rounded-lg bg-purple-50 px-4 py-2">
              <span class="text-gray-600">已同步：</span>
              <span class="text-lg font-bold text-purple-600">{{
                compareResult.synced.length
              }}</span>
            </div>
          </div>

          <!-- 枚举中有但数据库没有的 -->
          <div class="mb-4">
            <div class="mb-2 flex items-center justify-between">
              <h4 class="flex items-center gap-2 font-medium">
                <span class="i-lucide-plus-circle text-orange-500"></span>
                枚举中有但数据库没有（{{
                  compareResult.missingInDatabase.length
                }}）
              </h4>
              <ElButton
                v-if="compareResult.missingInDatabase.length > 0"
                type="primary"
                size="small"
                @click="handleInitFromEnum"
              >
                一键初始化
              </ElButton>
            </div>
            <div
              v-if="compareResult.missingInDatabase.length > 0"
              class="max-h-40 overflow-y-auto rounded border border-orange-200 bg-orange-50 p-2"
            >
              <div
                v-for="item in compareResult.missingInDatabase"
                :key="item.switchKey"
                class="mb-1 flex items-center gap-2 text-sm"
              >
                <ElTag type="warning" size="small">
                  {{ item.enumName }}
                </ElTag>
                <span class="text-gray-600">{{ item.switchKey }}</span>
                <span class="text-gray-400">- {{ item.description }}</span>
              </div>
            </div>
            <div v-else class="text-sm text-gray-400">暂无数据</div>
          </div>

          <!-- 数据库有但枚举没有的 -->
          <div class="mb-4">
            <div class="mb-2 flex items-center justify-between">
              <h4 class="flex items-center gap-2 font-medium">
                <span class="i-lucide-trash-2 text-red-500"></span>
                数据库有但枚举没有（{{ compareResult.missingInEnum.length }}）
              </h4>
              <ElButton
                v-if="compareResult.missingInEnum.length > 0"
                type="danger"
                size="small"
                @click="handleDeleteNotInEnum"
              >
                一键删除
              </ElButton>
            </div>
            <div
              v-if="compareResult.missingInEnum.length > 0"
              class="max-h-40 overflow-y-auto rounded border border-red-200 bg-red-50 p-2"
            >
              <div
                v-for="key in compareResult.missingInEnum"
                :key="key"
                class="mb-1 text-sm"
              >
                <ElTag type="danger" size="small">{{ key }}</ElTag>
              </div>
            </div>
            <div v-else class="text-sm text-gray-400">暂无数据</div>
          </div>

          <!-- 已同步的开关 -->
          <div>
            <h4 class="mb-2 flex items-center gap-2 font-medium">
              <span class="i-lucide-check-circle text-green-500"></span>
              已同步（{{ compareResult.synced.length }}）
            </h4>
            <div
              v-if="compareResult.synced.length > 0"
              class="max-h-40 overflow-y-auto rounded border border-green-200 bg-green-50 p-2"
            >
              <div class="flex flex-wrap gap-1">
                <ElTag
                  v-for="key in compareResult.synced"
                  :key="key"
                  type="success"
                  size="small"
                >
                  {{ key }}
                </ElTag>
              </div>
            </div>
            <div v-else class="text-sm text-gray-400">暂无数据</div>
          </div>
        </template>
      </div>
      <template #footer>
        <ElButton @click="compareDialogVisible = false">关闭</ElButton>
        <ElButton type="primary" @click="handleCompareEnum">刷新</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
