<script lang="ts" setup>
import type { CmsTextCategoryApi } from '#/api/cms';

import { onMounted, reactive, ref } from 'vue';

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
  ElRow,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
  ElTreeSelect,
} from 'element-plus';

import {
  createCategory,
  deleteCategory,
  getCategoryById,
  getCategoryTree,
  updateCategory,
} from '#/api/cms';

// ==================== 状态定义 ====================

const tableData = ref<CmsTextCategoryApi.CategoryVO[]>([]);
const loading = ref(false);
const tableRef = ref();
const isExpanded = ref(false);
const searchForm = reactive<CmsTextCategoryApi.QueryParams>({
  keyword: '',
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<CmsTextCategoryApi.CreateParams & { id?: number }>({
  pid: 0,
  categoryName: '',
  categoryKey: '',
  icon: '',
  sort: 0,
  status: 1,
  remark: '',
});

// 分类树（用于选择父分类）
const categoryTree = ref<CmsTextCategoryApi.CategoryVO[]>([]);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const formRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
  ],
  categoryKey: [{ required: true, message: '请输入分类标识', trigger: 'blur' }],
};

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const res = await getCategoryTree(searchForm);
    tableData.value = res;
    categoryTree.value = [
      {
        id: 0,
        pid: 0,
        categoryName: '顶级分类',
        categoryKey: 'root',
        status: 1,
        createTime: '',
        children: res,
      },
    ];
  } catch {
    ElMessage.error('获取分类列表失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.status = undefined;
  fetchData();
}

function resetForm() {
  formData.id = undefined;
  formData.pid = 0;
  formData.categoryName = '';
  formData.categoryKey = '';
  formData.icon = '';
  formData.sort = 0;
  formData.status = 1;
  formData.remark = '';
}

function handleCreate(parentId: number = 0) {
  resetForm();
  formData.pid = parentId;
  dialogType.value = 'create';
  dialogTitle.value = '新增分类';
  dialogVisible.value = true;
}

async function handleEdit(row: CmsTextCategoryApi.CategoryVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑分类';
  formLoading.value = true;

  try {
    const detail = await getCategoryById(row.id);
    Object.assign(formData, {
      id: detail.id,
      pid: detail.pid,
      categoryName: detail.categoryName,
      categoryKey: detail.categoryKey,
      icon: detail.icon || '',
      sort: detail.sort || 0,
      status: detail.status,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取分类详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createCategory(formData as CmsTextCategoryApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateCategory(formData as CmsTextCategoryApi.UpdateParams);
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

async function handleDelete(row: CmsTextCategoryApi.CategoryVO) {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('请先删除子分类');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除分类「${row.categoryName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteCategory(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

function handleExpandAll() {
  const allRows = getAllRows(tableData.value);
  allRows.forEach((row) => {
    tableRef.value?.toggleRowExpansion(row, true);
  });
  isExpanded.value = true;
}

function handleCollapseAll() {
  const allRows = getAllRows(tableData.value);
  allRows.forEach((row) => {
    tableRef.value?.toggleRowExpansion(row, false);
  });
  isExpanded.value = false;
}

function getAllRows(
  data: CmsTextCategoryApi.CategoryVO[],
): CmsTextCategoryApi.CategoryVO[] {
  let result: CmsTextCategoryApi.CategoryVO[] = [];
  data.forEach((item) => {
    result.push(item);
    if (item.children && item.children.length > 0) {
      result = [...result, ...getAllRows(item.children)];
    }
  });
  return result;
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
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
              placeholder="分类名称/标识"
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
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['cms:textCategory:query']"
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
            v-access:code="['cms:textCategory:create']"
            @click="handleCreate(0)"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增分类
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton plain @click="handleExpandAll">
            <span class="i-lucide-chevrons-down mr-1"></span>
            展开全部
          </ElButton>
          <ElButton plain @click="handleCollapseAll">
            <span class="i-lucide-chevrons-up mr-1"></span>
            折叠全部
          </ElButton>
        </div>
      </div>

      <!-- 树形表格 -->
      <ElTable
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        row-key="id"
        border
        default-expand-all
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        class="flex-1"
      >
        <ElTableColumn label="分类名称" prop="categoryName" min-width="200" />
        <ElTableColumn label="分类标识" prop="categoryKey" min-width="150" />
        <ElTableColumn label="图标" prop="icon" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.icon" :class="row.icon" class="text-lg"></span>
            <span v-else class="text-gray-400">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="排序" prop="sort" width="80" align="center" />
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="创建时间" prop="createTime" width="170" />
        <ElTableColumn label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="success"
              link
              size="small"
              v-access:code="['cms:textCategory:create']"
              @click="handleCreate(row.id)"
            >
              新增子级
            </ElButton>
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['cms:textCategory:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['cms:textCategory:delete']"
              @click="handleDelete(row)"
            >
              删除
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>

    <!-- 新增/编辑弹窗 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="550px"
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
          <ElCol :span="24">
            <ElFormItem label="父级分类">
              <ElTreeSelect
                v-model="formData.pid"
                :data="categoryTree"
                :props="{
                  label: 'categoryName',
                  value: 'id',
                  children: 'children',
                }"
                check-strictly
                :disabled="dialogType === 'edit'"
                placeholder="请选择父级分类"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="分类名称" prop="categoryName">
              <ElInput
                v-model="formData.categoryName"
                placeholder="请输入分类名称"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="分类标识" prop="categoryKey">
              <ElInput
                v-model="formData.categoryKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入分类标识"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="图标">
              <ElInput
                v-model="formData.icon"
                placeholder="如：i-lucide-folder"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="排序">
              <ElInputNumber
                v-model="formData.sort"
                :min="0"
                :max="9999"
                style="width: 100%"
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
