<script lang="ts" setup>
import type { SysDeptApi, SysRoleApi, SysUserApi } from '#/api/system';

import { computed, onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElAvatar,
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
  ElTooltip,
  ElTreeSelect,
} from 'element-plus';

import {
  assignRoles,
  batchUpdateDataScope,
  createUser,
  deleteUser,
  deleteUserBatch,
  downloadUserTemplate,
  exportUser,
  getAllRoles,
  getDeptTree,
  getUserById,
  getUserPage,
  getUserSocials,
  importUser,
  kickoutUser,
  resetPassword,
  unbindSocial,
  updateUser,
} from '#/api/system';
import { adjustWallet } from '#/api/wallet';
import ImageUpload from '#/components/image-upload/image-upload.vue';

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

const tableData = ref<SysUserApi.UserVO[]>([]);
const loading = ref(false);
const selectedRows = ref<SysUserApi.UserVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<SysUserApi.QueryParams>({
  keyword: '',
  status: undefined,
  deptId: undefined,
  dataScope: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

const formData = reactive<
  SysUserApi.CreateParams & {
    id?: number;
  }
>({
  username: '',
  password: '',
  nickname: '',
  realName: '',
  avatar: '',
  phone: '',
  email: '',
  gender: 0,
  status: 1,
  deptId: undefined,
  dataScope: 'UserCreate',
  roleIds: [],
  remark: '',
});

// 第三方绑定相关状态
const socialDialogVisible = ref(false);
const socialDialogUserId = ref<number>(0);
const socialDialogUsername = ref('');
const socialList = ref<SysUserApi.UserSocialVO[]>([]);
const socialLoading = ref(false);

const roleList = ref<SysRoleApi.RoleVO[]>([]);
const deptTree = ref<SysDeptApi.DeptTreeVO[]>([]);

const resetPwdDialogVisible = ref(false);
const resetPwdUserId = ref<number>(0);
const newPassword = ref('');

// 调整余额/积分相关状态
const adjustDialogVisible = ref(false);
const adjustLoading = ref(false);
const adjustDialogUsername = ref('');
const adjustDialogPoints = ref(0);
const adjustDialogRewardPoints = ref(0);
const adjustFormRef = ref();
const adjustForm = reactive({
  userId: 0,
  type: 'points' as const,
  action: 'income' as 'expense' | 'income',
  amount: 1,
  title: '',
  remark: '',
});
const adjustFormRules = {
  action: [{ required: true, message: '请选择操作类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入积分数量', trigger: 'blur' }],
  title: [{ required: true, message: '请输入调整原因', trigger: 'blur' }],
};

const assignRoleDialogVisible = ref(false);
const assignRoleUserId = ref<number>(0);
const assignRoleIds = ref<number[]>([]);

// 批量设置数据权限相关状态
const batchDataScopeDialogVisible = ref(false);
const batchDataScopeValue = ref<string>('UserCreate');
const batchDataScopeLoading = ref(false);

// ==================== 常量 ====================

const statusOptions = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
];

const genderOptions = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
];

const dataScopeOptions = [
  { label: '所有数据', value: 'All' },
  { label: '本部门及下级', value: 'DeptAndChildren' },
  { label: '本部门', value: 'Dept' },
  { label: '本人创建', value: 'UserCreate' },
  { label: '用户组', value: 'JoinGroup' },
];

const formRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
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
    const res = await getUserPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取用户列表失败');
  } finally {
    loading.value = false;
  }
}

async function fetchRoleList() {
  try {
    roleList.value = await getAllRoles();
  } catch {
    console.error('获取角色列表失败');
  }
}

async function fetchDeptTree() {
  try {
    deptTree.value = await getDeptTree();
  } catch {
    console.error('获取部门树失败');
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.status = undefined;
  searchForm.deptId = undefined;
  searchForm.dataScope = undefined;
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

function handleSelectionChange(rows: SysUserApi.UserVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.username = '';
  formData.password = '';
  formData.nickname = '';
  formData.realName = '';
  formData.avatar = '';
  formData.phone = '';
  formData.email = '';
  formData.gender = 0;
  formData.status = 1;
  formData.deptId = undefined;
  formData.dataScope = 'UserCreate';
  formData.roleIds = [];
  formData.remark = '';
  formData.points = 0;
  formData.balance = 0;
  formData.frozenPoints = 0;
  formData.frozenBalance = 0;
}

function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增用户';
  dialogVisible.value = true;
}

async function handleEdit(row: SysUserApi.UserVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑用户';
  formLoading.value = true;

  try {
    const detail = await getUserById(row.id);
    Object.assign(formData, {
      id: detail.id,
      username: detail.username,
      nickname: detail.nickname || '',
      realName: detail.realName || '',
      avatar: detail.avatar || '',
      phone: detail.phone || '',
      email: detail.email || '',
      gender: detail.gender || 0,
      status: detail.status,
      deptId: detail.deptId,
      dataScope: detail.dataScope || 'UserCreate',
      roleIds: detail.roleIds || [],
      remark: detail.remark || '',
      points: detail.points ?? 0,
      balance: detail.balance ?? 0,
      frozenPoints: detail.frozenPoints ?? 0,
      frozenBalance: detail.frozenBalance ?? 0,
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取用户详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createUser(formData as SysUserApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateUser(formData as SysUserApi.UpdateParams);
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

async function handleDelete(row: SysUserApi.UserVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除用户「${row.username}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteUser(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的用户');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteUserBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

function handleResetPassword(row: SysUserApi.UserVO) {
  resetPwdUserId.value = row.id;
  newPassword.value = '';
  resetPwdDialogVisible.value = true;
}

async function confirmResetPassword() {
  if (!newPassword.value) {
    ElMessage.warning('请输入新密码');
    return;
  }
  try {
    await resetPassword(resetPwdUserId.value, newPassword.value);
    ElMessage.success('密码重置成功');
    resetPwdDialogVisible.value = false;
  } catch {
    ElMessage.error('密码重置失败');
  }
}

function handleAdjustBalance(row: SysUserApi.UserVO) {
  adjustForm.userId = row.id;
  adjustForm.type = 'points';
  adjustForm.action = 'income';
  adjustForm.amount = 1;
  adjustForm.title = '';
  adjustForm.remark = '';
  adjustDialogUsername.value = row.nickname || row.username;
  adjustDialogPoints.value = row.points ?? 0;
  adjustDialogRewardPoints.value = row.rewardPoints ?? 0;
  adjustDialogVisible.value = true;
}

async function confirmAdjustBalance() {
  try {
    await adjustFormRef.value?.validate();
  } catch {
    return;
  }
  adjustLoading.value = true;
  try {
    await adjustWallet({
      userId: adjustForm.userId,
      type: adjustForm.type,
      action: adjustForm.action,
      amount: adjustForm.amount,
      title: adjustForm.title,
      remark: adjustForm.remark,
    });
    ElMessage.success('调整成功');
    adjustDialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('调整失败');
  } finally {
    adjustLoading.value = false;
  }
}

async function handleAssignRole(row: SysUserApi.UserVO) {
  assignRoleUserId.value = row.id;
  try {
    const detail = await getUserById(row.id);
    assignRoleIds.value = detail.roleIds || [];
    assignRoleDialogVisible.value = true;
  } catch {
    ElMessage.error('获取用户角色失败');
  }
}

async function confirmAssignRole() {
  try {
    await assignRoles(assignRoleUserId.value, assignRoleIds.value);
    ElMessage.success('角色分配成功');
    assignRoleDialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('角色分配失败');
  }
}

/** 批量设置数据权限 */
function handleBatchDataScope() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要设置的用户');
    return;
  }
  batchDataScopeValue.value = 'UserCreate';
  batchDataScopeDialogVisible.value = true;
}

/** 确认批量设置数据权限 */
async function confirmBatchDataScope() {
  batchDataScopeLoading.value = true;
  try {
    const ids = selectedRows.value.map((row) => row.id);
    const affected = await batchUpdateDataScope(ids, batchDataScopeValue.value);
    ElMessage.success(`成功更新 ${affected} 个用户的数据权限`);
    batchDataScopeDialogVisible.value = false;
    fetchData();
  } catch {
    ElMessage.error('批量设置数据权限失败');
  } finally {
    batchDataScopeLoading.value = false;
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

/** 踢用户下线 */
async function handleKickout(row: SysUserApi.UserVO) {
  try {
    await ElMessageBox.confirm(
      `确定要将用户「${row.username}」踢下线吗？`,
      '踢下线确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await kickoutUser(row.id);
    ElMessage.success('已将用户踢下线');
  } catch {
    // 用户取消
  }
}

function getGenderText(gender?: number) {
  return genderOptions.find((item) => item.value === gender)?.label || '未知';
}

function getDataScopeText(dataScope?: string) {
  return (
    dataScopeOptions.find((item) => item.value === dataScope)?.label ||
    '本人创建'
  );
}

// ==================== 第三方绑定 ====================

/** 社交平台配置 */
const socialPlatforms: Record<
  string,
  { color: string; icon: string; name: string }
> = {
  feishu: { name: '飞书', icon: 'i-simple-icons-feishu', color: '#3370ff' },
  wechat_mp: {
    name: '微信公众号',
    icon: 'i-simple-icons-wechat',
    color: '#07c160',
  },
  wechat_miniapp: {
    name: '微信小程序',
    icon: 'i-simple-icons-wechat',
    color: '#07c160',
  },
  wechat_open: {
    name: '微信开放平台',
    icon: 'i-simple-icons-wechat',
    color: '#07c160',
  },
  qq: { name: 'QQ', icon: 'i-simple-icons-tencentqq', color: '#12b7f5' },
  github: { name: 'GitHub', icon: 'i-simple-icons-github', color: '#24292f' },
  google: { name: 'Google', icon: 'i-simple-icons-google', color: '#4285f4' },
  apple: { name: 'Apple', icon: 'i-simple-icons-apple', color: '#000000' },
  dingtalk: { name: '钉钉', icon: 'i-simple-icons-dingtalk', color: '#0089ff' },
  weibo: { name: '微博', icon: 'i-simple-icons-sinaweibo', color: '#e6162d' },
};

/** 获取社交平台信息 */
function getSocialPlatform(socialType: string) {
  return (
    socialPlatforms[socialType] || {
      name: socialType,
      icon: 'i-lucide-link',
      color: '#666666',
    }
  );
}

/** 查看第三方绑定 */
async function handleViewSocials(row: SysUserApi.UserVO) {
  socialDialogUserId.value = row.id;
  socialDialogUsername.value = row.username;
  socialDialogVisible.value = true;
  socialLoading.value = true;
  try {
    socialList.value = await getUserSocials(row.id);
  } catch {
    ElMessage.error('获取绑定信息失败');
    socialList.value = [];
  } finally {
    socialLoading.value = false;
  }
}

/** 解绑第三方账号 */
async function handleUnbindSocial(social: SysUserApi.UserSocialVO) {
  const platform = getSocialPlatform(social.socialType);
  try {
    await ElMessageBox.confirm(
      `确定要解绑「${platform.name}」账号吗？解绑后用户将无法使用该方式登录。`,
      '解绑确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await unbindSocial(socialDialogUserId.value, social.socialType);
    ElMessage.success('解绑成功');
    // 刷新绑定列表
    socialList.value = await getUserSocials(socialDialogUserId.value);
  } catch {
    // 用户取消
  }
}

/** 导出用户 */
async function handleExport() {
  try {
    ElMessage.info('正在导出，请稍候...');
    const blob = await exportUser(searchForm);
    downloadFile(blob, '用户数据.xlsx');
    ElMessage.success('导出成功');
  } catch {
    ElMessage.error('导出失败');
  }
}

/** 下载导入模板 */
async function handleDownloadTemplate() {
  try {
    const blob = await downloadUserTemplate();
    downloadFile(blob, '用户导入模板.xlsx');
  } catch {
    ElMessage.error('下载模板失败');
  }
}

const importInputRef = ref<HTMLInputElement>();

/** 导入用户 */
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
    const result = await importUser(file);
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
  fetchRoleList();
  fetchDeptTree();
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
              placeholder="用户名/昵称/手机号"
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
          <ElFormItem label="部门">
            <ElTreeSelect
              v-model="searchForm.deptId"
              :data="deptTree"
              :props="{ label: 'name', value: 'id', children: 'children' }"
              check-strictly
              clearable
              placeholder="全部"
              style="width: 180px"
            />
          </ElFormItem>
          <ElFormItem label="数据权限">
            <ElSelect
              v-model="searchForm.dataScope"
              placeholder="全部"
              clearable
              style="width: 130px"
            >
              <ElOption
                v-for="item in dataScopeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['system:user:query']"
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
            v-access:code="['system:user:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增用户
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:user:delete']"
            @click="handleBatchDelete"
          >
            批量删除
          </ElButton>
          <ElButton
            type="info"
            plain
            :disabled="!hasSelected"
            v-access:code="['system:user:edit']"
            @click="handleBatchDataScope"
          >
            批量设置权限
          </ElButton>
        </div>
        <div class="flex items-center gap-3">
          <ElButton
            type="success"
            plain
            v-access:code="['system:user:export']"
            @click="handleExport"
          >
            导出
          </ElButton>
          <ElButton
            type="warning"
            plain
            v-access:code="['system:user:import']"
            @click="handleImport"
          >
            导入
          </ElButton>
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
        <ElTableColumn label="头像" width="70" align="center">
          <template #default="{ row }">
            <ElAvatar :size="36" :src="row.avatar">
              <span class="i-lucide-user text-lg"></span>
            </ElAvatar>
          </template>
        </ElTableColumn>
        <ElTableColumn label="用户名" prop="username" min-width="120" />
        <ElTableColumn
          label="昵称"
          prop="nickname"
          min-width="120"
          show-overflow-tooltip
        />
        <ElTableColumn label="真实姓名" prop="realName" width="100" />
        <ElTableColumn label="性别" width="70" align="center">
          <template #default="{ row }">
            {{ getGenderText(row.gender) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="手机号" prop="phone" width="130" />
        <ElTableColumn label="积分" width="90" align="right">
          <template #default="{ row }">
            <span class="font-medium text-amber-600">{{
              row.points ?? 0
            }}</span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="奖励积分" width="100" align="right">
          <template #default="{ row }">
            <span class="font-medium text-orange-500">
              {{ row.rewardPoints ?? 0 }}
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn label="数据权限" width="110" align="center">
          <template #default="{ row }">
            <ElTag type="info" size="small">
              {{ getDataScopeText(row.dataScope) }}
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
        <ElTableColumn label="操作" width="320" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['system:user:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="warning"
              link
              size="small"
              @click="handleAssignRole(row)"
            >
              角色
            </ElButton>
            <ElButton
              type="success"
              link
              size="small"
              @click="handleViewSocials(row)"
            >
              绑定
            </ElButton>
            <ElButton
              type="warning"
              link
              size="small"
              v-access:code="['system:user:adjustBalance']"
              @click="handleAdjustBalance(row)"
            >
              调整余额
            </ElButton>
            <ElButton
              type="info"
              link
              size="small"
              v-access:code="['system:user:resetPwd']"
              @click="handleResetPassword(row)"
            >
              重置密码
            </ElButton>
            <ElButton
              link
              size="small"
              v-access:code="['system:user:kickout']"
              @click="handleKickout(row)"
            >
              踢下线
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['system:user:delete']"
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
          <!-- 头像上传 -->
          <ElCol :span="24">
            <ElFormItem label="头像">
              <ImageUpload
                v-model="formData.avatar"
                biz-type="avatar"
                width="100px"
                height="100px"
                tip="支持 jpg、png、gif 格式，大小不超过 2MB"
                :max-size="2"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="用户名" prop="username">
              <ElInput
                v-model="formData.username"
                :disabled="dialogType === 'edit'"
                placeholder="请输入用户名"
              />
            </ElFormItem>
          </ElCol>
          <ElCol v-if="dialogType === 'create'" :span="12">
            <ElFormItem label="密码" prop="password">
              <ElInput
                v-model="formData.password"
                type="password"
                show-password
                placeholder="请输入密码"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="昵称">
              <ElInput v-model="formData.nickname" placeholder="请输入昵称" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="真实姓名">
              <ElInput
                v-model="formData.realName"
                placeholder="请输入真实姓名"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="性别">
              <ElSelect
                v-model="formData.gender"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in genderOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="手机号">
              <ElInput v-model="formData.phone" placeholder="请输入手机号" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="邮箱">
              <ElInput v-model="formData.email" placeholder="请输入邮箱" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="所属部门">
              <ElTreeSelect
                v-model="formData.deptId"
                :data="deptTree"
                :props="{ label: 'name', value: 'id', children: 'children' }"
                check-strictly
                clearable
                placeholder="请选择部门"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="角色">
              <ElSelect
                v-model="formData.roleIds"
                multiple
                collapse-tags
                collapse-tags-tooltip
                placeholder="请选择角色"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in roleList"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="数据权限">
              <ElSelect
                v-model="formData.dataScope"
                placeholder="请选择数据权限"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in dataScopeOptions"
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

    <!-- 重置密码弹窗 -->
    <ElDialog
      v-model="resetPwdDialogVisible"
      title="重置密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="80px">
        <ElFormItem label="新密码" required>
          <ElInput
            v-model="newPassword"
            type="password"
            show-password
            placeholder="请输入新密码"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="resetPwdDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="confirmResetPassword">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 调整余额/积分弹窗 -->
    <ElDialog
      v-model="adjustDialogVisible"
      title="调整积分"
      width="480px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="mb-4 rounded bg-gray-50 p-3 text-sm text-gray-600">
        用户：<span class="font-medium text-gray-900">{{
          adjustDialogUsername
        }}</span>
        <span class="mx-2">|</span>
        积分：<span class="font-medium text-amber-600">{{
          adjustDialogPoints
        }}</span>
        <span class="mx-2">|</span>
        奖励积分：<span class="font-medium text-orange-500">{{
          adjustDialogRewardPoints
        }}</span>
      </div>
      <ElForm
        ref="adjustFormRef"
        :model="adjustForm"
        :rules="adjustFormRules"
        label-width="80px"
      >
        <ElFormItem label="操作类型" prop="action">
          <ElSelect
            v-model="adjustForm.action"
            placeholder="请选择"
            class="w-full"
          >
            <ElOption label="增加" value="income" />
            <ElOption label="扣减" value="expense" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="积分数量" prop="amount">
          <ElInputNumber
            v-model="adjustForm.amount"
            :min="1"
            :step="1"
            :precision="0"
            controls-position="right"
            placeholder="请输入积分数量"
            class="w-full"
          />
        </ElFormItem>
        <ElFormItem label="标题" prop="title">
          <ElInput v-model="adjustForm.title" placeholder="请输入调整原因" />
        </ElFormItem>
        <ElFormItem label="备注">
          <ElInput
            v-model="adjustForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注（选填）"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="adjustDialogVisible = false">取消</ElButton>
        <ElButton
          type="primary"
          :loading="adjustLoading"
          @click="confirmAdjustBalance"
        >
          确定
        </ElButton>
      </template>
    </ElDialog>

    <!-- 分配角色弹窗 -->
    <ElDialog
      v-model="assignRoleDialogVisible"
      title="分配角色"
      width="450px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="80px">
        <ElFormItem label="角色">
          <ElSelect
            v-model="assignRoleIds"
            multiple
            placeholder="请选择角色"
            style="width: 100%"
          >
            <ElOption
              v-for="item in roleList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </ElSelect>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="assignRoleDialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="confirmAssignRole">确定</ElButton>
      </template>
    </ElDialog>

    <!-- 批量设置数据权限弹窗 -->
    <ElDialog
      v-model="batchDataScopeDialogVisible"
      title="批量设置数据权限"
      width="450px"
      :close-on-click-modal="false"
    >
      <div class="mb-4 rounded bg-blue-50 p-3 text-sm text-blue-600">
        已选择 <span class="font-medium">{{ selectedRows.length }}</span> 个用户
      </div>
      <ElForm label-width="100px">
        <ElFormItem label="数据权限">
          <ElSelect
            v-model="batchDataScopeValue"
            placeholder="请选择数据权限"
            style="width: 100%"
          >
            <ElOption
              v-for="item in dataScopeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </ElSelect>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="batchDataScopeDialogVisible = false">取消</ElButton>
        <ElButton
          type="primary"
          :loading="batchDataScopeLoading"
          @click="confirmBatchDataScope"
        >
          确定
        </ElButton>
      </template>
    </ElDialog>

    <!-- 第三方绑定弹窗 -->
    <ElDialog
      v-model="socialDialogVisible"
      :title="`第三方账号绑定 - ${socialDialogUsername}`"
      width="550px"
      :close-on-click-modal="false"
    >
      <div v-loading="socialLoading" class="min-h-[150px]">
        <div
          v-if="socialList.length === 0 && !socialLoading"
          class="flex flex-col items-center justify-center py-8 text-gray-400"
        >
          <span class="i-lucide-link-2-off mb-2 text-4xl"></span>
          <span>暂无第三方账号绑定</span>
        </div>
        <div v-else class="space-y-3">
          <div
            v-for="social in socialList"
            :key="social.id"
            class="flex items-center justify-between rounded-lg border border-gray-200 p-3"
          >
            <div class="flex items-center gap-3">
              <!-- 平台图标 -->
              <div
                class="flex h-10 w-10 items-center justify-center rounded-full"
                :style="{
                  backgroundColor: `${getSocialPlatform(social.socialType).color}15`,
                }"
              >
                <span
                  :class="getSocialPlatform(social.socialType).icon"
                  class="text-xl"
                  :style="{ color: getSocialPlatform(social.socialType).color }"
                ></span>
              </div>
              <!-- 绑定信息 -->
              <div>
                <div class="font-medium">
                  {{ getSocialPlatform(social.socialType).name }}
                </div>
                <div class="text-xs text-gray-400">
                  <ElTooltip :content="social.socialId" placement="top">
                    <span>{{
                      social.nickname || `${social.socialId?.slice(0, 16)}...`
                    }}</span>
                  </ElTooltip>
                  <span class="mx-1">·</span>
                  <span>绑定于 {{ social.bindTime?.slice(0, 10) }}</span>
                </div>
              </div>
            </div>
            <!-- 操作按钮 -->
            <ElButton
              type="danger"
              size="small"
              plain
              @click="handleUnbindSocial(social)"
            >
              解绑
            </ElButton>
          </div>
        </div>
      </div>
      <template #footer>
        <ElButton @click="socialDialogVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
