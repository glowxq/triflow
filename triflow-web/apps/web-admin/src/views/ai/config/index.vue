<script lang="ts" setup>
import type { AiConfigVO } from '#/api/ai';

import { onMounted, reactive, ref } from 'vue';

import { Page } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElInputNumber,
  ElMessage,
  ElMessageBox,
  ElOption,
  ElSelect,
  ElSwitch,
  ElTable,
  ElTableColumn,
  ElTag,
} from 'element-plus';

import {
  aiChat,
  deleteAiConfig,
  getAiConfigList,
  getAiProviders,
  saveAiConfig,
  setDefaultProvider,
} from '#/api/ai';

// ==================== 状态定义 ====================

const tableData = ref<AiConfigVO[]>([]);
const loading = ref(false);

/** 提供商代码 → 显示名称映射 (与 Spring AI 自动检测的类名对应) */
const PROVIDER_NAMES: Record<string, string> = {
  deepseek: 'DeepSeek',
  openai: 'OpenAI / 通义千问',
  zhipuai: '智谱 GLM',
  anthropic: 'Anthropic Claude',
  ollama: 'Ollama 本地模型',
};

const providers = ref<{ code: string; name: string }[]>(
  Object.entries(PROVIDER_NAMES).map(([code, name]) => ({ code, name })),
);

// 编辑对话框
const dialogVisible = ref(false);
const dialogTitle = ref('');
const formRef = ref();
const formLoading = ref(false);
const formData = reactive({
  id: undefined as number | undefined,
  provider: '',
  apiKey: '',
  endpoint: '',
  defaultModel: '',
  enabled: true,
  isDefault: false,
  timeout: 120,
});

const formRules = {
  apiKey: [{ message: '请输入 API Key', required: true, trigger: 'blur' }],
  provider: [{ message: '请选择提供商', required: true, trigger: 'change' }],
};

// 测试对话框
const testDialogVisible = ref(false);
const testLoading = ref(false);
const testMessage = ref('');
const testResponse = ref('');
const testProvider = ref('');

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    tableData.value = await getAiConfigList();
  } catch (error) {
    console.error('获取配置列表失败:', error);
    // 如果接口不存在，显示空列表
    tableData.value = [];
  } finally {
    loading.value = false;
  }
}

async function fetchProviders() {
  try {
    const list = await getAiProviders();
    if (list && list.length > 0) {
      // 合并后端可用提供商与本地显示名称
      providers.value = list.map((p) => ({
        code: p.code,
        name: PROVIDER_NAMES[p.code] || p.name || p.code,
      }));
    }
  } catch (error) {
    console.error('获取提供商列表失败:', error);
  }
}

function showAddDialog() {
  dialogTitle.value = '新增 AI 配置';
  formData.id = undefined;
  formData.provider = '';
  formData.apiKey = '';
  formData.endpoint = '';
  formData.defaultModel = '';
  formData.enabled = true;
  formData.isDefault = false;
  formData.timeout = 120;
  dialogVisible.value = true;
}

function showEditDialog(row: AiConfigVO) {
  dialogTitle.value = '编辑 AI 配置';
  formData.id = row.id;
  formData.provider = row.provider;
  formData.apiKey = '';
  formData.endpoint = row.endpoint || '';
  formData.defaultModel = row.defaultModel || '';
  formData.enabled = row.enabled;
  formData.isDefault = row.isDefault;
  formData.timeout = row.timeout || 120;
  dialogVisible.value = true;
}

async function handleSave() {
  if (!formRef.value) return;

  try {
    await formRef.value.validate();
    formLoading.value = true;
    await saveAiConfig({
      id: formData.id,
      provider: formData.provider,
      apiKey: formData.apiKey,
      endpoint: formData.endpoint || undefined,
      defaultModel: formData.defaultModel || undefined,
      enabled: formData.enabled,
      isDefault: formData.isDefault,
      timeout: formData.timeout,
    });
    ElMessage.success('保存成功');
    dialogVisible.value = false;
    fetchData();
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message);
    }
  } finally {
    formLoading.value = false;
  }
}

async function handleDelete(row: AiConfigVO) {
  try {
    await ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });
    await deleteAiConfig(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch (error: any) {
    if (error !== 'cancel' && error?.message) {
      ElMessage.error(error.message);
    }
  }
}

async function handleSetDefault(row: AiConfigVO) {
  try {
    await setDefaultProvider(row.id);
    ElMessage.success('设置成功');
    fetchData();
  } catch (error: any) {
    if (error?.message) {
      ElMessage.error(error.message);
    }
  }
}

function showTestDialog(row: AiConfigVO) {
  testProvider.value = row.provider;
  testMessage.value = '';
  testResponse.value = '';
  testDialogVisible.value = true;
}

async function handleTest() {
  if (!testMessage.value.trim()) {
    ElMessage.warning('请输入测试消息');
    return;
  }
  testLoading.value = true;
  try {
    const res = await aiChat({
      provider: testProvider.value,
      message: testMessage.value,
    });
    testResponse.value = res.content;
  } catch (error: any) {
    testResponse.value = `错误: ${error?.message || '测试失败'}`;
  } finally {
    testLoading.value = false;
  }
}

function getProviderName(code: string) {
  const p = providers.value.find((item) => item.code === code);
  return p?.name || code;
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchProviders();
  fetchData();
});
</script>

<template>
  <Page
    auto-content-height
    class="ai-config-page"
    v-access:code="['ai:config:query']"
  >
    <div class="flex h-full flex-col rounded-lg bg-card p-4">
      <!-- 操作按钮 -->
      <div class="mb-4 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <ElButton type="primary" @click="showAddDialog">
            <span class="i-lucide-plus mr-1"></span>
            新增配置
          </ElButton>
        </div>
      </div>

      <!-- 表格 -->
      <div class="table-wrapper">
        <ElTable v-loading="loading" :data="tableData" border stripe>
          <ElTableColumn prop="id" label="ID" width="70" />
          <ElTableColumn label="提供商" width="140">
            <template #default="{ row }">
              <ElTag>{{ getProviderName(row.provider) }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="API Key" width="180">
            <template #default="{ row }">
              <span class="text-gray-400">{{ row.apiKey || '******' }}</span>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="endpoint" label="端点" min-width="200">
            <template #default="{ row }">
              {{ row.endpoint || '默认端点' }}
            </template>
          </ElTableColumn>
          <ElTableColumn prop="defaultModel" label="默认模型" width="140" />
          <ElTableColumn label="状态" width="90" align="center">
            <template #default="{ row }">
              <ElTag :type="row.enabled ? 'success' : 'info'" size="small">
                {{ row.enabled ? '启用' : '禁用' }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="默认" width="80" align="center">
            <template #default="{ row }">
              <ElTag v-if="row.isDefault" type="warning" size="small">
                默认
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作" width="230" fixed="right">
            <template #default="{ row }">
              <ElButton
                type="primary"
                link
                size="small"
                v-access:code="['ai:config:test']"
                @click="showTestDialog(row)"
              >
                测试
              </ElButton>
              <ElButton
                type="primary"
                link
                size="small"
                v-access:code="['ai:config:update']"
                @click="showEditDialog(row)"
              >
                编辑
              </ElButton>
              <ElButton
                v-if="!row.isDefault"
                type="primary"
                link
                size="small"
                @click="handleSetDefault(row)"
              >
                设为默认
              </ElButton>
              <ElButton
                type="danger"
                link
                size="small"
                @click="handleDelete(row)"
              >
                删除
              </ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <ElDialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <ElForm
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <ElFormItem label="提供商" prop="provider">
          <ElSelect
            v-model="formData.provider"
            placeholder="请选择提供商"
            :disabled="!!formData.id"
            style="width: 100%"
          >
            <ElOption
              v-for="item in providers"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="API Key" prop="apiKey">
          <ElInput
            v-model="formData.apiKey"
            type="password"
            show-password
            :placeholder="formData.id ? '留空则不修改' : '请输入 API Key'"
          />
        </ElFormItem>
        <ElFormItem label="端点">
          <ElInput v-model="formData.endpoint" placeholder="留空使用默认端点" />
        </ElFormItem>
        <ElFormItem label="默认模型">
          <ElInput
            v-model="formData.defaultModel"
            placeholder="留空使用提供商默认模型"
          />
        </ElFormItem>
        <ElFormItem label="超时时间">
          <ElInputNumber
            v-model="formData.timeout"
            :min="10"
            :max="600"
            style="width: 100%"
          />
          <span class="ml-2 text-xs text-gray-400">秒</span>
        </ElFormItem>
        <ElFormItem label="启用">
          <ElSwitch v-model="formData.enabled" />
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
      title="测试 AI 配置"
      width="600px"
      :close-on-click-modal="false"
    >
      <ElForm label-width="80px">
        <ElFormItem label="提供商">
          <ElTag>{{ getProviderName(testProvider) }}</ElTag>
        </ElFormItem>
        <ElFormItem label="测试消息">
          <ElInput
            v-model="testMessage"
            type="textarea"
            :rows="3"
            placeholder="请输入测试消息"
          />
        </ElFormItem>
        <ElFormItem>
          <ElButton type="primary" :loading="testLoading" @click="handleTest">
            发送测试
          </ElButton>
        </ElFormItem>
        <ElFormItem v-if="testResponse" label="响应">
          <ElCard class="w-full">
            <pre class="whitespace-pre-wrap text-sm">{{ testResponse }}</pre>
          </ElCard>
        </ElFormItem>
      </ElForm>
    </ElDialog>
  </Page>
</template>

<style scoped>
.ai-config-page .table-wrapper {
  overflow-x: auto;
}

@media (max-width: 768px) {
  .ai-config-page :deep(.el-dialog) {
    width: 92vw !important;
    margin: 0 auto;
  }
}
</style>
