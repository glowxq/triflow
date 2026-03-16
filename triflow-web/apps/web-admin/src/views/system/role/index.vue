<script lang="ts" setup>
import type { SysMenuApi, SysRoleApi } from '#/api/system';

import { computed, nextTick, onMounted, reactive, ref } from 'vue';

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
  ElTable,
  ElTableColumn,
  ElTag,
  ElTree,
  ElSwitch,
} from 'element-plus';

import {
  assignMenus,
  createRole,
  deleteRole,
  deleteRoleBatch,
  downloadRoleTemplate,
  exportRole,
  getMenuTree,
  getRoleById,
  getRoleMenus,
  getRolePage,
  importRole,
  updateRole,
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

const tableData = ref<SysRoleApi.RoleVO[]>([]);
const loading = ref(false);
const selectedRows = ref<SysRoleApi.RoleVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<SysRoleApi.QueryParams>({
  keyword: '',
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<SysRoleApi.CreateParams & { id?: number }>({
  roleKey: '',
  roleName: '',
  sort: 0,
  status: 1,
  menuIds: [],
  remark: '',
});

const menuTree = ref<SysMenuApi.MenuTreeVO[]>([]);
const menuTreeRef = ref<InstanceType<typeof ElTree>>();
const menuLinkage = ref(true);
const menuExpandedKeys = ref<number[]>([]);

const assignMenuDialogVisible = ref(false);
const assignMenuRoleId = ref<number>(0);
const assignMenuIds = ref<number[]>([]);
const assignMenuTreeRef = ref<InstanceType<typeof ElTree>>();
const assignMenuLinkage = ref(true);
const assignMenuExpandedKeys = ref<number[]>([]);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const formRules = {
  roleKey: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);

// ==================== 方法 ====================

function transformMenuTree(menus: SysMenuApi.MenuTreeVO[]): any[] {
  return menus.map((menu) => ({
    id: menu.id,
    label: menu.meta?.title || menu.name || '',
    children: menu.children ? transformMenuTree(menu.children) : undefined,
  }));
}

function collectMenuIds(menus: SysMenuApi.MenuTreeVO[], result: number[] = []) {
  menus.forEach((menu) => {
    if (menu.id) {
      result.push(menu.id);
    }
    if (menu.children && menu.children.length) {
      collectMenuIds(menu.children, result);
    }
  });
  return result;
}

function handleExpandAll() {
  const keys = collectMenuIds(menuTree.value);
  menuExpandedKeys.value = keys;
  // 使用nextTick确保响应式数据更新后再调用方法
  nextTick(() => {
    // 遍历所有节点并展开
    const treeStore = menuTreeRef.value?.store;
    if (treeStore) {
      const allNodes = treeStore.nodesMap;
      for (const nodeKey in allNodes) {
        const node = allNodes[nodeKey];
        if (node && !node.isLeaf) {
          node.expanded = true;
        }
      }
    }
  });
}

function handleCollapseAll() {
  menuExpandedKeys.value = [];
  // 使用nextTick确保响应式数据更新后再调用方法
  nextTick(() => {
    // 遍历所有节点并折叠
    const treeStore = menuTreeRef.value?.store;
    if (treeStore) {
      const allNodes = treeStore.nodesMap;
      for (const nodeKey in allNodes) {
        const node = allNodes[nodeKey];
        if (node && !node.isLeaf) {
          node.expanded = false;
        }
      }
    }
  });
}

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getRolePage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取角色列表失败');
  } finally {
    loading.value = false;
  }
}

async function fetchMenuTree() {
  try {
    const data = await getMenuTree();
    menuTree.value = data;
    // 默认展开所有节点
    menuExpandedKeys.value = collectMenuIds(data);
  } catch {
    console.error('获取菜单树失败');
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
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

function handleSelectionChange(rows: SysRoleApi.RoleVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.roleKey = '';
  formData.roleName = '';
  formData.sort = 0;
  formData.status = 1;
  formData.menuIds = [];
  formData.remark = '';
  menuLinkage.value = true;
}

function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增角色';
  dialogVisible.value = true;
}

async function handleEdit(row: SysRoleApi.RoleVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑角色';
  formLoading.value = true;

  try {
    const detail = await getRoleById(row.id);
    const menuIds = await getRoleMenus(row.id);
    Object.assign(formData, {
      id: detail.id,
      roleKey: detail.roleKey,
      roleName: detail.roleName,
      sort: detail.sort || 0,
      status: detail.status,
      menuIds,
      remark: detail.remark || '',
    });
    dialogVisible.value = true;
    setTimeout(() => {
      menuTreeRef.value?.setCheckedKeys(menuIds);
    }, 100);
  } catch {
    ElMessage.error('获取角色详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || [];
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || [];
  formData.menuIds = [...checkedKeys, ...halfCheckedKeys] as number[];

  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createRole(formData as SysRoleApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateRole(formData as SysRoleApi.UpdateParams);
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

async function handleDelete(row: SysRoleApi.RoleVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除角色「${row.roleName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteRole(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的角色');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个角色吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteRoleBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleAssignMenu(row: SysRoleApi.RoleVO) {
  assignMenuRoleId.value = row.id;
  assignMenuLinkage.value = true;
  // 默认展开所有节点
  assignMenuExpandedKeys.value = collectMenuIds(menuTree.value);
  try {
    const menuIds = await getRoleMenus(row.id);
    assignMenuIds.value = menuIds;
    assignMenuDialogVisible.value = true;
    setTimeout(() => {
      assignMenuTreeRef.value?.setCheckedKeys(menuIds);
    }, 100);
  } catch {
    ElMessage.error('获取角色菜单失败');
  }
}

async function confirmAssignMenu() {
  const checkedKeys = assignMenuTreeRef.value?.getCheckedKeys() || [];
  const halfCheckedKeys = assignMenuTreeRef.value?.getHalfCheckedKeys() || [];
  const menuIds = [...checkedKeys, ...halfCheckedKeys] as number[];

  try {
    await assignMenus(assignMenuRoleId.value, menuIds);
    ElMessage.success('菜单分配成功');
    assignMenuDialogVisible.value = false;
  } catch {
    ElMessage.error('菜单分配失败');
  }
}

function handleAssignExpandAll() {
  const keys = collectMenuIds(menuTree.value);
  assignMenuExpandedKeys.value = keys;
  nextTick(() => {
    const treeStore = assignMenuTreeRef.value?.store;
    if (treeStore) {
      const allNodes = treeStore.nodesMap;
      for (const nodeKey in allNodes) {
        const node = allNodes[nodeKey];
        if (node && !node.isLeaf) {
          node.expanded = true;
        }
      }
    }
  });
}

function handleAssignCollapseAll() {
  assignMenuExpandedKeys.value = [];
  nextTick(() => {
    const treeStore = assignMenuTreeRef.value?.store;
    if (treeStore) {
      const allNodes = treeStore.nodesMap;
      for (const nodeKey in allNodes) {
        const node = allNodes[nodeKey];
        if (node && !node.isLeaf) {
          node.expanded = false;
        }
      }
    }
  });
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

/** 导出角色 */
async function handleExport() {
  try {
    ElMessage.info('正在导出，请稍候...');
    const blob = await exportRole(searchForm);
    downloadFile(blob, '角色数据.xlsx');
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

/** 下载导入模板 */
async function handleDownloadTemplate() {
  try {
    const blob = await downloadRoleTemplate();
    downloadFile(blob, '角色导入模板.xlsx');
  } catch {
    ElMessage.error('下载模板失败');
  }
}

const importInputRef = ref<HTMLInputElement>();

/** 导入角色 */
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
    const result = await importRole(file);
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
  fetchMenuTree();
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
              placeholder="角色编码/名称"
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
              v-access:code="['system:role:query']"
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
            v-access:code="['system:role:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增角色
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:role:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton
            type="success"
            plain
            v-access:code="['system:role:export']"
            @click="handleExport"
          >
            导出
          </ElButton>
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
        v-loading="loading"
        :data="tableData"
        border
        stripe
        class="flex-1"
        @selection-change="handleSelectionChange"
      >
        <ElTableColumn type="selection" width="50" align="center" />
        <ElTableColumn label="角色编码" prop="roleKey" min-width="120" />
        <ElTableColumn label="角色名称" prop="roleName" min-width="120" />
        <ElTableColumn label="排序" prop="sort" width="80" align="center" />
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="备注"
          prop="remark"
          min-width="150"
          show-overflow-tooltip
        />
        <ElTableColumn label="创建时间" prop="createTime" width="170" />
        <ElTableColumn label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:role:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="success"
              link
              size="small"
              @click="handleAssignMenu(row)"
            >
              菜单
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['system:role:delete']"
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
        label-width="85px"
      >
        <ElRow :gutter="20">
          <ElCol :span="12">
            <ElFormItem label="角色编码" prop="roleKey">
              <ElInput
                v-model="formData.roleKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入角色编码"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="角色名称" prop="roleName">
              <ElInput v-model="formData.roleName" placeholder="请输入角色名称" />
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
            <ElFormItem label="菜单权限">
              <div class="mb-2 flex flex-wrap items-center justify-between gap-2">
                <div class="flex flex-wrap gap-2">
                  <ElButton size="small" @click="handleExpandAll">展开全部</ElButton>
                  <ElButton size="small" @click="handleCollapseAll">折叠全部</ElButton>
                </div>
                <div class="flex items-center gap-2 text-xs text-gray-500">
                  <span>父子联动</span>
                  <ElSwitch v-model="menuLinkage" />
                </div>
              </div>
              <div
                class="max-h-60 w-full overflow-auto rounded-md border border-gray-200 bg-gray-50 p-3"
              >
                <ElTree
                  ref="menuTreeRef"
                  :data="transformMenuTree(menuTree)"
                  :props="{ label: 'label', children: 'children' }"
                  :default-expanded-keys="menuExpandedKeys"
                  node-key="id"
                  show-checkbox
                  :check-strictly="!menuLinkage"
                />
              </div>
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

    <!-- 分配菜单弹窗 -->
    <ElDialog
      v-model="assignMenuDialogVisible"
      title="分配菜单权限"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
        <div class="flex flex-wrap gap-2">
          <ElButton size="small" @click="handleAssignExpandAll">展开全部</ElButton>
          <ElButton size="small" @click="handleAssignCollapseAll">折叠全部</ElButton>
        </div>
        <div class="flex items-center gap-2 text-xs text-gray-500">
          <span>父子联动</span>
          <ElSwitch v-model="assignMenuLinkage" />
        </div>
      </div>
      <div
        class="max-h-96 overflow-auto rounded-md border border-gray-200 bg-gray-50 p-3"
      >
        <ElTree
          ref="assignMenuTreeRef"
          :data="transformMenuTree(menuTree)"
          :props="{ label: 'label', children: 'children' }"
          :default-expanded-keys="assignMenuExpandedKeys"
          node-key="id"
          show-checkbox
          :check-strictly="!assignMenuLinkage"
        />
      </div>
      <template #footer>
        <ElButton @click="assignMenuDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="confirmAssignMenu">确定</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
