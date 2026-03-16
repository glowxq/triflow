<script lang="ts" setup>
import type { WechatTabbarApi } from '#/api/wechat';

import { computed, onMounted, reactive, ref } from 'vue';

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
} from 'element-plus';

import {
  createWechatTabbar,
  deleteWechatTabbar,
  deleteWechatTabbarBatch,
  getWechatTabbarPage,
  updateWechatTabbar,
} from '#/api/wechat';
import IconPicker from '#/components/icon-picker/icon-picker.vue';
import { StatusSelect, StatusTag } from '#/components/status';

// ==================== 状态定义 ====================

const tableData = ref<WechatTabbarApi.TabbarVO[]>([]);
const loading = ref(false);
const selectedRows = ref<WechatTabbarApi.TabbarVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<WechatTabbarApi.QueryParams>({
  keyword: '',
  status: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<WechatTabbarApi.CreateParams & { id?: number }>({
  text: '',
  pagePath: '',
  iconType: 'unocss',
  icon: '',
  iconActive: '',
  badge: '',
  isBulge: 0,
  sort: 0,
  status: 1,
  remark: '',
});

// ==================== 常量 ====================

const iconTypeOptions = [
  { label: 'Unocss', value: 'unocss' },
  { label: 'Iconfont', value: 'iconfont' },
  { label: 'UI 库', value: 'uiLib' },
  { label: '图片', value: 'image' },
];

const formRules = {
  text: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  pagePath: [{ required: true, message: '请输入页面路径', trigger: 'blur' }],
  iconType: [{ required: true, message: '请选择图标类型', trigger: 'change' }],
  icon: [{ required: true, message: '请输入图标资源', trigger: 'blur' }],
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
    const res = await getWechatTabbarPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取底部菜单失败');
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

function handleSelectionChange(rows: WechatTabbarApi.TabbarVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.text = '';
  formData.pagePath = '';
  formData.iconType = 'unocss';
  formData.icon = '';
  formData.iconActive = '';
  formData.badge = '';
  formData.isBulge = 0;
  formData.sort = 0;
  formData.status = 1;
  formData.remark = '';
}

function openCreateDialog() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增底部菜单';
  dialogVisible.value = true;
}

function openEditDialog(row: WechatTabbarApi.TabbarVO) {
  dialogType.value = 'edit';
  dialogTitle.value = `编辑菜单 - ${row.text}`;
  formData.id = row.id;
  formData.text = row.text;
  formData.pagePath = row.pagePath;
  formData.iconType = row.iconType;
  formData.icon = row.icon;
  formData.iconActive = row.iconActive || '';
  formData.badge = row.badge || '';
  formData.isBulge = row.isBulge || 0;
  formData.sort = row.sort || 0;
  formData.status = row.status;
  formData.remark = row.remark || '';
  dialogVisible.value = true;
}

function handleDialogClose() {
  resetForm();
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
    if (dialogType.value === 'create') {
      await createWechatTabbar(formData as WechatTabbarApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateWechatTabbar(formData as WechatTabbarApi.UpdateParams);
      ElMessage.success('更新成功');
    }
    dialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('操作失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: WechatTabbarApi.TabbarVO) {
  try {
    await ElMessageBox.confirm(`确认删除菜单「${row.text}」吗？`, '提示', {
      type: 'warning',
    });
    await deleteWechatTabbar(row.id);
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
      `确认删除选中的 ${selectedRows.value.length} 条菜单吗？`,
      '提示',
      { type: 'warning' },
    );
    const ids = selectedRows.value.map((item) => item.id);
    await deleteWechatTabbarBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败');
    }
  }
}

onMounted(fetchData);
</script>

<template>
  <Page auto-content-height>
    <ElForm :model="searchForm" inline label-width="80px" class="mb-4">
      <ElFormItem label="关键词">
        <ElInput
          v-model="searchForm.keyword"
          placeholder="菜单名称/页面路径"
          clearable
        />
      </ElFormItem>
      <ElFormItem label="状态">
        <StatusSelect
          v-model="searchForm.status"
          placeholder="请选择状态"
          width="200px"
        />
      </ElFormItem>
      <ElFormItem>
        <ElButton
          type="primary"
          v-access:code="['wechat:tabbar:query']"
          @click="handleSearch"
        >
          查询
        </ElButton>
        <ElButton @click="handleReset">重置</ElButton>
      </ElFormItem>
    </ElForm>

    <div class="mb-4 flex items-center gap-2">
      <ElButton
        type="primary"
        v-access:code="['wechat:tabbar:create']"
        @click="openCreateDialog"
      >
        新增
      </ElButton>
      <ElButton
        type="danger"
        :disabled="!hasSelected"
        v-access:code="['wechat:tabbar:deleteBatch']"
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
      <ElTableColumn prop="text" label="菜单名称" min-width="140" />
      <ElTableColumn prop="pagePath" label="页面路径" min-width="200" />
      <ElTableColumn prop="iconType" label="图标类型" width="120" />
      <ElTableColumn prop="icon" label="图标资源" min-width="160" />
      <ElTableColumn prop="badge" label="徽标" width="100" />
      <ElTableColumn prop="isBulge" label="鼓包" width="80">
        <template #default="{ row }">
          <ElSwitch :model-value="row.isBulge === 1" disabled />
        </template>
      </ElTableColumn>
      <ElTableColumn prop="sort" label="排序" width="80" />
      <ElTableColumn prop="status" label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :status="row.status" />
        </template>
      </ElTableColumn>
      <ElTableColumn label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <ElButton
            type="primary"
            link
            v-access:code="['wechat:tabbar:update']"
            @click="openEditDialog(row)"
          >
            编辑
          </ElButton>
          <ElButton
            type="danger"
            link
            v-access:code="['wechat:tabbar:delete']"
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
      width="640px"
      @close="handleDialogClose"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="110px"
      >
        <ElRow :gutter="16">
          <ElCol :span="12">
            <ElFormItem label="菜单名称" prop="text">
              <ElInput v-model="formData.text" placeholder="请输入菜单名称" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="页面路径" prop="pagePath">
              <ElInput
                v-model="formData.pagePath"
                placeholder="例如 pages/index/index"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="图标类型" prop="iconType">
              <ElSelect
                v-model="formData.iconType"
                placeholder="请选择图标类型"
              >
                <ElOption
                  v-for="item in iconTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="图标资源" prop="icon">
              <IconPicker
                v-model="formData.icon"
                :icon-type="formData.iconType"
                placeholder="请选择图标或输入资源"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="选中图标">
              <IconPicker
                v-model="formData.iconActive"
                :icon-type="formData.iconType"
                placeholder="image 模式必填"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="徽标">
              <ElInput v-model="formData.badge" placeholder="数字或 dot" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="鼓包">
              <ElSwitch
                v-model="formData.isBulge"
                :active-value="1"
                :inactive-value="0"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="排序">
              <ElInputNumber v-model="formData.sort" :min="0" class="w-full" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="状态" prop="status">
              <StatusSelect v-model="formData.status" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="备注">
              <ElInput v-model="formData.remark" placeholder="备注" />
            </ElFormItem>
          </ElCol>
        </ElRow>
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
