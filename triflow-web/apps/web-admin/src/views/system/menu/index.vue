<script lang="ts" setup>
import type { SysMenuApi } from '#/api/system';

import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';
import { IconifyIcon } from '@vben/icons';

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
  ElRadio,
  ElRadioGroup,
  ElRow,
  ElSelect,
  ElTable,
  ElTableColumn,
  ElTag,
  ElTreeSelect,
} from 'element-plus';

import {
  createMenu,
  deleteMenu,
  downloadMenuTemplate,
  exportMenu,
  getMenuById,
  getMenuList,
  importMenu,
  updateMenu,
} from '#/api/system';
import { IconSelect } from '#/components/icon-select';

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

const tableData = ref<SysMenuApi.MenuVO[]>([]);
const loading = ref(false);
const tableRef = ref();
const isExpanded = ref(true);
const searchForm = reactive<SysMenuApi.QueryParams>({
  keyword: '',
  menuType: undefined,
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<SysMenuApi.CreateParams & { id?: number }>({
  parentId: 0,
  menuName: '',
  menuType: 'M',
  path: '',
  name: '',
  component: '',
  redirect: '',
  permission: '',
  icon: '',
  sort: 0,
  visible: 1,
  status: 1,
  isFrame: 0,
  isCache: 1,
  isAffix: 0,
  remark: '',
});

const menuTreeOptions = ref<any[]>([]);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const menuTypeOptions = [
  { label: '目录', value: 'M', type: 'primary' as const },
  { label: '菜单', value: 'C', type: 'success' as const },
  { label: '按钮', value: 'F', type: 'warning' as const },
];

const visibleOptions = [
  { label: '显示', value: 1 },
  { label: '隐藏', value: 0 },
];

const yesNoOptions = [
  { label: '是', value: 1 },
  { label: '否', value: 0 },
];

const formRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
};

// ==================== 方法 ====================

function listToTree(list: SysMenuApi.MenuVO[]): any[] {
  const map = new Map<number, any>();
  const roots: any[] = [];

  list.forEach((item) => {
    map.set(item.id, { ...item, children: [] });
  });

  list.forEach((item) => {
    const node = map.get(item.id);
    if (item.parentId === 0 || !map.has(item.parentId)) {
      roots.push(node);
    } else {
      const parent = map.get(item.parentId);
      if (parent) {
        parent.children.push(node);
      }
    }
  });

  function cleanChildren(nodes: any[]) {
    nodes.forEach((node) => {
      if (node.children.length === 0) {
        delete node.children;
      } else {
        cleanChildren(node.children);
      }
    });
  }
  cleanChildren(roots);

  return roots;
}

function buildMenuTreeOptions(list: SysMenuApi.MenuVO[]): any[] {
  const tree = listToTree(list);

  function transformNode(nodes: any[]): any[] {
    return nodes.map((node) => ({
      id: node.id,
      label: node.menuName,
      children: node.children ? transformNode(node.children) : undefined,
    }));
  }

  return [{ id: 0, label: '根目录', children: transformNode(tree) }];
}

async function fetchData() {
  loading.value = true;
  try {
    const data = await getMenuList(searchForm);
    tableData.value = listToTree(data);
    const allMenus = await getMenuList({});
    menuTreeOptions.value = buildMenuTreeOptions(allMenus);
  } catch {
    ElMessage.error('获取菜单列表失败');
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.menuType = undefined;
  searchForm.status = undefined;
  fetchData();
}

function resetForm() {
  formData.id = undefined;
  formData.parentId = 0;
  formData.menuName = '';
  formData.menuType = 'M';
  formData.path = '';
  formData.name = '';
  formData.component = '';
  formData.redirect = '';
  formData.permission = '';
  formData.icon = '';
  formData.sort = 0;
  formData.visible = 1;
  formData.status = 1;
  formData.isFrame = 0;
  formData.isCache = 1;
  formData.isAffix = 0;
  formData.remark = '';
}

function handleCreate(parentId: number = 0) {
  resetForm();
  formData.parentId = parentId;
  dialogType.value = 'create';
  dialogTitle.value = '新增菜单';
  dialogVisible.value = true;
}

async function handleEdit(row: SysMenuApi.MenuVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑菜单';
  formLoading.value = true;

  try {
    const detail = await getMenuById(row.id);
    Object.assign(formData, {
      id: detail.id,
      parentId: detail.parentId,
      menuName: detail.menuName,
      menuType: detail.menuType,
      path: detail.path || '',
      name: detail.name || '',
      component: detail.component || '',
      redirect: detail.redirect || '',
      permission: detail.permission || '',
      icon: detail.icon || '',
      sort: detail.sort || 0,
      visible: detail.visible ?? 1,
      status: detail.status,
      isFrame: detail.isFrame ?? 0,
      isCache: detail.isCache ?? 1,
      isAffix: detail.isAffix ?? 0,
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取菜单详情失败');
  } finally {
    formLoading.value = false;
  }
}

function handleCreateChild(row: SysMenuApi.MenuVO) {
  handleCreate(row.id);
}

async function handleSubmit() {
  if (!formData.menuName) {
    ElMessage.warning('请输入菜单名称');
    return;
  }

  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createMenu(formData as SysMenuApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateMenu(formData as SysMenuApi.UpdateParams);
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

async function handleDelete(row: SysMenuApi.MenuVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除菜单「${row.menuName}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteMenu(row.id);
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

function getMenuTypeOption(menuType: string) {
  return (
    menuTypeOptions.find((item) => item.value === menuType) ||
    menuTypeOptions[0]
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

/** 导出菜单 */
async function handleExport() {
  try {
    ElMessage.info('正在导出，请稍候...');
    const blob = await exportMenu(searchForm);
    downloadFile(blob, '菜单数据.xlsx');
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

/** 下载导入模板 */
async function handleDownloadTemplate() {
  try {
    const blob = await downloadMenuTemplate();
    downloadFile(blob, '菜单导入模板.xlsx');
  } catch {
    ElMessage.error('下载模板失败');
  }
}

const importInputRef = ref<HTMLInputElement>();

/** 导入菜单 */
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
    const result = await importMenu(file);
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
          <ElFormItem label="菜单名称">
            <ElInput
              v-model="searchForm.keyword"
              placeholder="请输入菜单名称"
              clearable
              style="width: 180px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="菜单类型">
            <ElSelect
              v-model="searchForm.menuType"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption
                v-for="item in menuTypeOptions"
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
              style="width: 100px"
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
              v-access:code="['system:menu:query']"
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
            v-access:code="['system:menu:create']"
            @click="handleCreate()"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增菜单
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
        <ElTableColumn label="菜单名称" min-width="200" prop="menuName" />
        <ElTableColumn label="图标" width="100" align="center">
          <template #default="{ row }">
            <div v-if="row.icon" class="flex items-center justify-center">
              <IconifyIcon :icon="row.icon" class="size-5 text-primary" />
            </div>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="类型" width="90" align="center">
          <template #default="{ row }">
            <ElTag :type="getMenuTypeOption(row.menuType).type" size="small">
              {{ getMenuTypeOption(row.menuType).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="排序" prop="sort" width="70" align="center" />
        <ElTableColumn
          label="路由路径"
          min-width="150"
          prop="path"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="row.path" class="font-mono text-xs text-blue-600">{{
              row.path
            }}</span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="组件路径"
          min-width="180"
          prop="component"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="row.component" class="font-mono text-xs text-green-600">
              {{ row.component }}
            </span>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="权限标识" min-width="160" prop="permission">
          <template #default="{ row }">
            <ElTag
              v-if="row.permission"
              type="info"
              size="small"
              effect="plain"
              class="font-mono"
            >
              {{ row.permission }}
            </ElTag>
            <span v-else class="text-gray-300">-</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="可见" width="70" align="center">
          <template #default="{ row }">
            <ElTag :type="row.visible === 1 ? 'success' : 'info'" size="small">
              {{ row.visible === 1 ? '显示' : '隐藏' }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="状态" width="70" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="180" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:menu:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              v-if="row.menuType !== 'F'"
              type="success"
              link
              size="small"
              v-access:code="['system:menu:create']"
              @click="handleCreateChild(row)"
            >
              新增
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['system:menu:delete']"
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
      width="750px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <ElForm
        ref="formRef"
        v-loading="formLoading"
        :model="formData"
        :rules="formRules"
        label-width="90px"
      >
        <ElRow :gutter="20">
          <ElCol :span="12">
            <ElFormItem label="上级菜单">
              <ElTreeSelect
                v-model="formData.parentId"
                :data="menuTreeOptions"
                :props="{ label: 'label', value: 'id', children: 'children' }"
                check-strictly
                default-expand-all
                placeholder="请选择上级菜单"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="菜单类型" prop="menuType">
              <ElRadioGroup v-model="formData.menuType">
                <ElRadio
                  v-for="item in menuTypeOptions"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </ElRadio>
              </ElRadioGroup>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="菜单名称" prop="menuName">
              <ElInput
                v-model="formData.menuName"
                placeholder="请输入菜单名称"
              />
            </ElFormItem>
          </ElCol>
          <ElCol v-if="formData.menuType !== 'F'" :span="12">
            <ElFormItem label="菜单图标">
              <IconSelect v-model="formData.icon" placeholder="请选择图标" />
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
          <template v-if="formData.menuType !== 'F'">
            <ElCol :span="12">
              <ElFormItem label="路由路径">
                <ElInput
                  v-model="formData.path"
                  placeholder="如：/system/user"
                />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="路由名称">
                <ElInput v-model="formData.name" placeholder="如：SystemUser" />
              </ElFormItem>
            </ElCol>
          </template>
          <ElCol v-if="formData.menuType === 'C'" :span="12">
            <ElFormItem label="组件路径">
              <ElInput
                v-model="formData.component"
                placeholder="如：/system/user/index"
              />
            </ElFormItem>
          </ElCol>
          <ElCol v-if="formData.menuType === 'M'" :span="12">
            <ElFormItem label="重定向">
              <ElInput
                v-model="formData.redirect"
                placeholder="请输入重定向地址"
              />
            </ElFormItem>
          </ElCol>
          <ElCol v-if="formData.menuType === 'F'" :span="12">
            <ElFormItem label="权限标识">
              <ElInput
                v-model="formData.permission"
                placeholder="如：system:user:add"
              />
            </ElFormItem>
          </ElCol>
          <ElCol v-if="formData.menuType !== 'F'" :span="12">
            <ElFormItem label="是否可见">
              <ElRadioGroup v-model="formData.visible">
                <ElRadio
                  v-for="item in visibleOptions"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </ElRadio>
              </ElRadioGroup>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="状态">
              <ElRadioGroup v-model="formData.status">
                <ElRadio
                  v-for="item in statusOptions"
                  :key="item.value"
                  :value="item.value"
                >
                  {{ item.label }}
                </ElRadio>
              </ElRadioGroup>
            </ElFormItem>
          </ElCol>
          <template v-if="formData.menuType !== 'F'">
            <ElCol :span="8">
              <ElFormItem label="是否外链">
                <ElRadioGroup v-model="formData.isFrame">
                  <ElRadio
                    v-for="item in yesNoOptions"
                    :key="item.value"
                    :value="item.value"
                  >
                    {{ item.label }}
                  </ElRadio>
                </ElRadioGroup>
              </ElFormItem>
            </ElCol>
            <ElCol :span="8">
              <ElFormItem label="是否缓存">
                <ElRadioGroup v-model="formData.isCache">
                  <ElRadio
                    v-for="item in yesNoOptions"
                    :key="item.value"
                    :value="item.value"
                  >
                    {{ item.label }}
                  </ElRadio>
                </ElRadioGroup>
              </ElFormItem>
            </ElCol>
            <ElCol :span="8">
              <ElFormItem label="是否固定">
                <ElRadioGroup v-model="formData.isAffix">
                  <ElRadio
                    v-for="item in yesNoOptions"
                    :key="item.value"
                    :value="item.value"
                  >
                    {{ item.label }}
                  </ElRadio>
                </ElRadioGroup>
              </ElFormItem>
            </ElCol>
          </template>
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
