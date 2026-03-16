<script lang="ts" setup>
import type { SysDeptApi } from '#/api/system';

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
  createDept,
  deleteDept,
  downloadDeptTemplate,
  exportDept,
  getDeptById,
  getDeptList,
  getDeptTree,
  importDept,
  updateDept,
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

const tableData = ref<SysDeptApi.DeptTreeVO[]>([]);
const loading = ref(false);
const tableRef = ref();
const isExpanded = ref(true);
const searchForm = reactive<SysDeptApi.QueryParams>({
  keyword: '',
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<SysDeptApi.CreateParams & { id?: number }>({
  parentId: 0,
  deptName: '',
  sort: 0,
  leader: '',
  phone: '',
  email: '',
  status: 1,
  remark: '',
});

const deptTreeOptions = ref<any[]>([]);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const formRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
};

// ==================== 方法 ====================

function buildDeptTreeOptions(list: SysDeptApi.DeptTreeVO[]): any[] {
  function transformNode(nodes: SysDeptApi.DeptTreeVO[]): any[] {
    return nodes.map((node) => ({
      id: node.id,
      label: node.deptName,
      children: node.children ? transformNode(node.children) : undefined,
    }));
  }

  return [{ id: 0, label: '根部门', children: transformNode(list) }];
}

function listToTree(list: SysDeptApi.DeptVO[]): SysDeptApi.DeptTreeVO[] {
  const map = new Map<number, SysDeptApi.DeptTreeVO>();
  const roots: SysDeptApi.DeptTreeVO[] = [];

  list.forEach((item) => {
    map.set(item.id, { ...item, children: [] });
  });

  list.forEach((item) => {
    const node = map.get(item.id)!;
    if (item.parentId === 0 || !map.has(item.parentId)) {
      roots.push(node);
    } else {
      const parent = map.get(item.parentId);
      if (parent) {
        if (!parent.children) {
          parent.children = [];
        }
        parent.children.push(node);
      }
    }
  });

  function cleanChildren(nodes: SysDeptApi.DeptTreeVO[]) {
    nodes.forEach((node) => {
      if (node.children && node.children.length === 0) {
        delete node.children;
      } else if (node.children) {
        cleanChildren(node.children);
      }
    });
  }
  cleanChildren(roots);

  return roots;
}

async function fetchData() {
  loading.value = true;
  try {
    if (searchForm.keyword || searchForm.status !== undefined) {
      const data = await getDeptList(searchForm);
      tableData.value = listToTree(data);
    } else {
      tableData.value = await getDeptTree();
    }
    const tree = await getDeptTree();
    deptTreeOptions.value = buildDeptTreeOptions(tree);
  } catch {
    ElMessage.error('获取部门列表失败');
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
  formData.parentId = 0;
  formData.deptName = '';
  formData.sort = 0;
  formData.leader = '';
  formData.phone = '';
  formData.email = '';
  formData.status = 1;
  formData.remark = '';
}

function handleCreate(parentId: number = 0) {
  resetForm();
  formData.parentId = parentId;
  dialogType.value = 'create';
  dialogTitle.value = '新增部门';
  dialogVisible.value = true;
}

async function handleEdit(row: SysDeptApi.DeptTreeVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑部门';
  formLoading.value = true;

  try {
    const detail = await getDeptById(row.id);
    Object.assign(formData, {
      id: detail.id,
      parentId: detail.parentId,
      deptName: detail.deptName,
      sort: detail.sort || 0,
      leader: detail.leader || '',
      phone: detail.phone || '',
      email: detail.email || '',
      status: detail.status,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取部门详情失败');
  } finally {
    formLoading.value = false;
  }
}

function handleCreateChild(row: SysDeptApi.DeptTreeVO) {
  handleCreate(row.id);
}

async function handleSubmit() {
  if (!formData.deptName) {
    ElMessage.warning('请输入部门名称');
    return;
  }

  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createDept(formData as SysDeptApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateDept(formData as SysDeptApi.UpdateParams);
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

async function handleDelete(row: SysDeptApi.DeptTreeVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除部门「${row.deptName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteDept(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

/** 递归获取所有行 */
function getAllRows(data: any[]): any[] {
  const rows: any[] = [];
  data.forEach((row) => {
    rows.push(row);
    if (row.children && row.children.length > 0) {
      rows.push(...getAllRows(row.children));
    }
  });
  return rows;
}

/** 展开全部 */
function handleExpandAll() {
  const allRows = getAllRows(tableData.value);
  allRows.forEach((row) => {
    tableRef.value?.toggleRowExpansion(row, true);
  });
  isExpanded.value = true;
}

/** 折叠全部 */
function handleCollapseAll() {
  const allRows = getAllRows(tableData.value);
  allRows.forEach((row) => {
    tableRef.value?.toggleRowExpansion(row, false);
  });
  isExpanded.value = false;
}

/** 导出部门 */
async function handleExport() {
  try {
    ElMessage.info('正在导出，请稍候...');
    const blob = await exportDept(searchForm);
    downloadFile(blob, '部门数据.xlsx');
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

/** 下载导入模板 */
async function handleDownloadTemplate() {
  try {
    const blob = await downloadDeptTemplate();
    downloadFile(blob, '部门导入模板.xlsx');
  } catch {
    ElMessage.error('下载模板失败');
  }
}

const importInputRef = ref<HTMLInputElement>();

/** 导入部门 */
function handleImport() {
  importInputRef.value?.click();
}

/** 处理文件选择 */
async function handleFileChange(event: Event) {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (!file) return;

  try {
    ElMessage.info('正在导入，请稍候...');
    const result = await importDept(file);
    if (result.fail > 0) {
      ElMessage.warning(
        `导入完成：成功 ${result.success} 条，失败 ${result.fail} 条`,
      );
    } else {
      ElMessage.success(`成功导入 ${result.success} 条数据`);
    }
    fetchData();
  } catch {
    ElMessage.error('导入失败');
  } finally {
    target.value = '';
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
          <ElFormItem label="部门名称">
            <ElInput
              v-model="searchForm.keyword"
              placeholder="请输入部门名称"
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
              v-access:code="['system:dept:query']"
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
            v-access:code="['system:dept:create']"
            @click="handleCreate()"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增部门
          </ElButton>
          <ElButton
            @click="isExpanded ? handleCollapseAll() : handleExpandAll()"
          >
            {{ isExpanded ? '折叠全部' : '展开全部' }}
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton type="success" plain @click="handleExport"> 导出 </ElButton>
          <ElButton type="warning" plain @click="handleImport"> 导入 </ElButton>
          <ElButton type="info" plain @click="handleDownloadTemplate">
            下载模板
          </ElButton>
          <input
            ref="importInputRef"
            type="file"
            accept=".xlsx,.xls"
            style="display: none"
            @change="handleFileChange"
          />
        </div>
      </div>

      <!-- 表格 -->
      <ElTable
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        border
        stripe
        class="flex-1"
        :default-expand-all="isExpanded"
        row-key="id"
      >
        <ElTableColumn label="部门名称" min-width="200" prop="deptName" />
        <ElTableColumn label="排序" prop="sort" width="80" align="center" />
        <ElTableColumn label="负责人" min-width="100" prop="leader">
          <template #default="{ row }">
            <span v-if="row.leader" class="text-gray-700">{{
              row.leader
            }}</span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="联系电话" min-width="130" prop="phone">
          <template #default="{ row }">
            <span v-if="row.phone" class="text-blue-600">{{ row.phone }}</span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="邮箱"
          min-width="180"
          prop="email"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="row.email" class="text-blue-600">{{ row.email }}</span>
            <span v-else class="text-gray-300">-</span>
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
        <ElTableColumn label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:dept:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="success"
              link
              size="small"
              v-access:code="['system:dept:create']"
              @click="handleCreateChild(row)"
            >
              新增
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['system:dept:delete']"
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
            <ElFormItem label="上级部门">
              <ElTreeSelect
                v-model="formData.parentId"
                :data="deptTreeOptions"
                :props="{ label: 'label', value: 'id', children: 'children' }"
                check-strictly
                default-expand-all
                placeholder="请选择上级部门"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="部门名称" prop="deptName">
              <ElInput v-model="formData.deptName" placeholder="请输入部门名称" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="排序">
              <ElInputNumber
                v-model="formData.sort"
                :min="0"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="负责人">
              <ElInput v-model="formData.leader" placeholder="请输入负责人" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="联系电话">
              <ElInput v-model="formData.phone" placeholder="请输入联系电话" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="邮箱">
              <ElInput v-model="formData.email" placeholder="请输入邮箱" />
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
