<script lang="ts" setup>
import type { PromptTemplateVO } from '#/api/ai';

import { onMounted, reactive, ref, watch } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
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
} from 'element-plus';

import {
  deletePromptTemplate,
  getPromptCategories,
  getPromptTemplatePage,
  savePromptTemplate,
  testPromptTemplate,
} from '#/api/ai';

// ==================== 状态定义 ====================

const tableData = ref<PromptTemplateVO[]>([]);
const loading = ref(false);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive({
  name: '',
  code: '',
  category: '',
  enabled: undefined as boolean | undefined,
});

const categories = ref<string[]>([]);

// 编辑对话框
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();
const formLoading = ref(false);
const formData = reactive({
  id: undefined as number | undefined,
  name: '',
  code: '',
  category: '',
  systemPrompt: '',
  userPromptTemplate: '',
  variables: '',
  description: '',
  enabled: true,
  sort: 0,
});

const formRules = {
  code: [{ message: '请输入模板代码', required: true, trigger: 'blur' }],
  name: [{ message: '请输入模板名称', required: true, trigger: 'blur' }],
  systemPrompt: [
    { message: '请输入系统提示', required: true, trigger: 'blur' },
  ],
};

// 测试对话框
const testDialogVisible = ref(false);
const testLoading = ref(false);
const testTemplateId = ref(0);
const testTemplateName = ref('');
const testVariables = ref<Record<string, string>>({});
const testResponse = ref('');

// 变量 JSON 校验状态
const variablesError = ref('');
const variablesPlaceholder = 'JSON 数组格式，如：["name", "topic", "language"]';

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const res = await getPromptTemplatePage({
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    });
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch (error) {
    console.error('获取模板列表失败:', error);
    tableData.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
}

async function fetchCategories() {
  try {
    categories.value = await getPromptCategories();
  } catch (error) {
    console.error('获取分类列表失败:', error);
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.name = '';
  searchForm.code = '';
  searchForm.category = '';
  searchForm.enabled = undefined;
  handleSearch();
}

// 监听分页变化（使用 watch 代替废弃的 @current-change 和 @size-change 事件）
// 注意：需要 skip 首次执行，因为 onMounted 已经调用了 fetchData
let paginationInitialized = false;
watch(
  () => [pagination.pageNum, pagination.pageSize],
  ([_newPage, newSize], [_oldPage, oldSize]) => {
    if (!paginationInitialized) {
      paginationInitialized = true;
      return;
    }
    // 如果是 pageSize 变化，重置到第一页
    if (newSize !== oldSize) {
      pagination.pageNum = 1;
      return; // 让 pageNum 的变化再触发一次 watch
    }
    fetchData();
  },
);

function showAddDialog() {
  dialogTitle.value = '新增 Prompt 模板';
  formData.id = undefined;
  formData.name = '';
  formData.code = '';
  formData.category = '';
  formData.systemPrompt = '';
  formData.userPromptTemplate = '';
  formData.variables = '[]';
  formData.description = '';
  formData.enabled = true;
  formData.sort = 0;
  variablesError.value = '';
  dialogVisible.value = true;
}

function showEditDialog(row: PromptTemplateVO) {
  dialogTitle.value = '编辑 Prompt 模板';
  formData.id = row.id;
  formData.name = row.name;
  formData.code = row.code;
  formData.category = row.category || '';
  formData.systemPrompt = row.systemPrompt;
  formData.userPromptTemplate = row.userPromptTemplate || '';
  formData.variables = row.variables || '[]';
  formData.description = row.description || '';
  formData.enabled = row.enabled;
  formData.sort = row.sort;
  variablesError.value = '';
  dialogVisible.value = true;
}

// 校验变量 JSON 格式
function validateVariables(): boolean {
  if (!formData.variables || formData.variables.trim() === '') {
    formData.variables = '[]';
    variablesError.value = '';
    return true;
  }
  try {
    const parsed = JSON.parse(formData.variables);
    if (!Array.isArray(parsed)) {
      variablesError.value = '变量列表必须是数组格式';
      return false;
    }
    if (!parsed.every((item) => typeof item === 'string')) {
      variablesError.value = '数组元素必须都是字符串';
      return false;
    }
    // 格式化 JSON
    formData.variables = JSON.stringify(parsed, null, 2);
    variablesError.value = '';
    return true;
  } catch {
    variablesError.value = 'JSON 格式错误';
    return false;
  }
}

// 格式化用户模板（高亮变量）
function formatUserTemplate() {
  // 自动检测并高亮 {{variable}} 格式的变量
  const regex = /\{\{(\w+)\}\}/g;
  const matches = formData.userPromptTemplate.match(regex);
  if (matches) {
    const vars = matches.map((m) => m.replaceAll(/\{\{|\}\}/g, ''));
    const uniqueVars = [...new Set(vars)];
    // 自动同步到变量列表
    try {
      const currentVars = JSON.parse(formData.variables || '[]');
      const newVars = [...new Set([...currentVars, ...uniqueVars])];
      formData.variables = JSON.stringify(newVars, null, 2);
      variablesError.value = '';
    } catch {
      // ignore
    }
  }
}

async function handleSave() {
  if (!formRef.value) return;

  // 先校验变量格式
  if (!validateVariables()) {
    ElMessage.warning('请检查变量列表格式');
    return;
  }

  try {
    await formRef.value.validate();
    formLoading.value = true;
    await savePromptTemplate({
      id: formData.id,
      name: formData.name,
      code: formData.code,
      category: formData.category || undefined,
      systemPrompt: formData.systemPrompt,
      userPromptTemplate: formData.userPromptTemplate || undefined,
      variables: formData.variables || undefined,
      description: formData.description || undefined,
      enabled: formData.enabled,
      sort: formData.sort,
    });
    ElMessage.success('保存成功');
    dialogVisible.value = false;
    fetchData();
    fetchCategories();
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message);
    }
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: PromptTemplateVO) {
  try {
    await ElMessageBox.confirm('确定要删除该模板吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    await deletePromptTemplate(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message);
    }
  }
}

function showTestDialog(row: PromptTemplateVO) {
  testTemplateId.value = row.id;
  testTemplateName.value = row.name;
  testResponse.value = '';

  // 解析变量
  testVariables.value = {};
  if (row.variables) {
    try {
      const vars = JSON.parse(row.variables);
      if (Array.isArray(vars)) {
        vars.forEach((v: string) => {
          testVariables.value[v] = '';
        });
      }
    } catch {
      // ignore
    }
  }

  testDialogVisible.value = true;
}

async function handleTest() {
  testLoading.value = true;
  testResponse.value = '';
  try {
    // 后端返回的是 String，不是 AiChatVO
    const res = await testPromptTemplate(
      testTemplateId.value,
      testVariables.value,
    );
    // res 直接就是字符串响应
    testResponse.value =
      typeof res === 'string'
        ? res
        : (res as any)?.content || JSON.stringify(res);
  } catch (error: any) {
    testResponse.value = `错误: ${error?.message || '测试失败'}`;
  } finally {
    testLoading.value = false;
  }
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchCategories();
  fetchData();
});
</script>

<template>
  <Page auto-content-height class="ai-prompt-page">
    <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 搜索区域 -->
      <div class="mb-4 border-b border-border pb-4">
        <ElForm :model="searchForm" inline>
          <ElFormItem label="名称">
            <ElInput
              v-model="searchForm.name"
              placeholder="模板名称/关键词"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="代码">
            <ElInput
              v-model="searchForm.code"
              placeholder="模板代码"
              clearable
              style="width: 200px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="分类">
            <ElSelect
              v-model="searchForm.category"
              placeholder="全部分类"
              clearable
              style="width: 150px"
            >
              <ElOption
                v-for="cat in categories"
                :key="cat"
                :label="cat"
                :value="cat"
              />
              <template v-if="categories.length === 0">
                <ElOption label="暂无分类" value="" disabled />
              </template>
            </ElSelect>
          </ElFormItem>
          <ElFormItem label="状态">
            <ElSelect
              v-model="searchForm.enabled"
              placeholder="全部"
              clearable
              style="width: 120px"
            >
              <ElOption label="启用" :value="true" />
              <ElOption label="禁用" :value="false" />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['ai:prompt:query']"
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
            v-access:code="['ai:prompt:create']"
            @click="showAddDialog"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增模板
          </ElButton>
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <ElTable v-loading="loading" :data="tableData" border stripe>
          <ElTableColumn prop="id" label="ID" width="70" />
          <ElTableColumn prop="name" label="名称" width="160" />
          <ElTableColumn prop="code" label="代码" width="160" />
          <ElTableColumn prop="category" label="分类" width="120">
            <template #default="{ row }">
              <ElTag v-if="row.category" size="small">{{ row.category }}</ElTag>
              <span v-else class="text-gray-400">-</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="systemPrompt" label="系统提示" min-width="260">
            <template #default="{ row }">
              <span class="line-clamp-2">{{ row.systemPrompt }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="状态" width="90" align="center">
            <template #default="{ row }">
              <ElTag :type="row.enabled ? 'success' : 'info'" size="small">
                {{ row.enabled ? '启用' : '禁用' }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="sort" label="排序" width="80" align="center" />
          <ElTableColumn prop="updateTime" label="更新时间" width="170" />
          <ElTableColumn label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <ElButton
                type="primary"
                link
                size="small"
                @click="showTestDialog(row)"
              >
                测试
              </ElButton>
              <ElButton
                type="primary"
                link
                size="small"
                v-access:code="['ai:prompt:update']"
                @click="showEditDialog(row)"
              >
                编辑
              </ElButton>
              <ElButton
                type="danger"
                link
                size="small"
                v-access:code="['ai:prompt:delete']"
                @click="handleDelete(row)"
              >
                删除
              </ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>

      <!-- 分页 - 使用新版写法，移除废弃的事件监听 -->
      <div class="pagination-wrapper mt-4 flex justify-end">
        <ElPagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
        />
      </div>
    </div>

    <!-- 编辑对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElRow :gutter="16">
          <ElCol :span="12" :xs="24">
            <ElFormItem label="模板名称" prop="name">
              <ElInput v-model="formData.name" placeholder="请输入模板名称" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12" :xs="24">
            <ElFormItem label="模板代码" prop="code">
              <ElInput
                v-model="formData.code"
                placeholder="请输入模板代码"
                :disabled="!!formData.id"
              />
            </ElFormItem>
          </ElCol>
        </ElRow>
        <ElRow :gutter="16">
          <ElCol :span="9" :xs="24">
            <ElFormItem label="分类">
              <ElSelect
                v-model="formData.category"
                placeholder="请选择或输入分类"
                filterable
                allow-create
                default-first-option
                class="w-full"
              >
                <ElOption
                  v-for="cat in categories"
                  :key="cat"
                  :label="cat"
                  :value="cat"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="9" :xs="24">
            <ElFormItem label="排序">
              <ElInputNumber
                v-model="formData.sort"
                :min="0"
                :max="9999"
                controls-position="right"
                class="w-full"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="6" :xs="24">
            <ElFormItem label="启用">
              <ElSwitch v-model="formData.enabled" />
            </ElFormItem>
          </ElCol>
        </ElRow>
        <ElFormItem label="系统提示" prop="systemPrompt">
          <ElInput
            v-model="formData.systemPrompt"
            type="textarea"
            :rows="5"
            placeholder="请输入系统提示（System Prompt）"
            class="code-textarea"
          />
        </ElFormItem>
        <ElFormItem label="用户模板">
          <ElInput
            v-model="formData.userPromptTemplate"
            type="textarea"
            :rows="5"
            placeholder="用户消息模板，支持变量如 {{name}}、{{topic}}"
            class="code-textarea"
            @blur="formatUserTemplate"
          />
          <div class="mt-1 text-xs text-gray-400">
            提示：使用
            <code class="rounded bg-gray-100 px-1">{'{{ 变量名 }}'}</code>
            定义变量，失去焦点时自动同步到变量列表
          </div>
        </ElFormItem>
        <ElFormItem label="变量列表" :error="variablesError">
          <ElInput
            v-model="formData.variables"
            type="textarea"
            :rows="3"
            :placeholder="variablesPlaceholder"
            class="code-textarea"
            @blur="validateVariables"
          />
          <div class="mt-1 text-xs text-gray-400">
            格式：JSON 字符串数组，如
            <code class="rounded bg-gray-100 px-1">["var1", "var2"]</code>
          </div>
        </ElFormItem>
        <ElFormItem label="描述">
          <ElInput
            v-model="formData.description"
            type="textarea"
            :rows="2"
            placeholder="模板描述说明"
          />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="formLoading" @click="handleSave">
          确定
        </ElButton>
      </template>
    </ElDialog>

    <!-- 测试对话框 -->
    <ElDialog
      v-model="testDialogVisible"
      title="测试 Prompt 模板"
      width="700px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="100px">
        <ElFormItem label="模板名称">
          <ElTag type="primary">{{ testTemplateName }}</ElTag>
        </ElFormItem>
        <template v-if="Object.keys(testVariables).length > 0">
          <ElFormItem
            v-for="(_, key) in testVariables"
            :key="key"
            :label="String(key)"
          >
            <ElInput
              v-model="testVariables[key]"
              :placeholder="`请输入变量 ${key} 的值`"
            />
          </ElFormItem>
        </template>
        <ElFormItem v-else label="变量">
          <span class="text-gray-400">该模板没有定义变量</span>
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" :loading="testLoading" @click="handleTest">
            发送测试请求
          </ElButton>
        </ElFormItem>
        <ElFormItem label="AI 响应">
          <ElCard class="response-card w-full">
            <template v-if="testLoading">
              <div class="response-loading">
                <span>AI 正在思考中...</span>
              </div>
            </template>
            <template v-else-if="testResponse">
              <pre class="response-content">{{ testResponse }}</pre>
            </template>
            <template v-else>
              <div class="response-empty">
                <span class="text-gray-400">
                  点击"发送测试请求"查看 AI 响应
                </span>
              </div>
            </template>
          </ElCard>
        </ElFormItem>
      </ElForm>
    </ElDialog>
  </Page>
</template>

<style scoped>
@keyframes pulse {
  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.5;
  }
}

@media (max-width: 768px) {
  .ai-prompt-page :deep(.el-form--inline .el-form-item) {
    width: 100%;
    margin-right: 0;
  }

  .ai-prompt-page :deep(.el-form--inline .el-form-item__content) {
    flex: 1;
  }

  .ai-prompt-page :deep(.el-dialog) {
    width: 92vw !important;
    margin: 0 auto;
  }
}

.ai-prompt-page .table-wrapper {
  overflow-x: auto;
}

.ai-prompt-page .pagination-wrapper {
  flex-wrap: wrap;
  gap: 8px;
}

.line-clamp-2 {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

/* 代码风格的文本框 */
.code-textarea :deep(textarea) {
  font-family: Monaco, Menlo, 'Ubuntu Mono', Consolas, monospace;
  font-size: 13px;
  line-height: 1.6;
  background-color: #fafafa;
  border-color: #e5e7eb;
}

.code-textarea :deep(textarea:focus) {
  background-color: #fff;
}

/* 响应卡片 */
.response-card {
  min-height: 120px;
  max-height: 400px;
  overflow-y: auto;
}

.response-card :deep(.el-card__body) {
  padding: 16px;
}

.response-content {
  margin: 0;
  font-family: Monaco, Menlo, 'Ubuntu Mono', Consolas, monospace;
  font-size: 13px;
  line-height: 1.7;
  color: #1f2937;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.response-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80px;
  font-size: 14px;
  color: #6b7280;
}

.response-loading span {
  animation: pulse 1.5s infinite;
}

.response-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80px;
}

/* 输入框宽度 */
.w-full {
  width: 100%;
}
</style>
