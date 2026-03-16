<script lang="ts" setup>
import type { CmsTextApi, CmsTextCategoryApi } from '#/api/cms';

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
  ElTag,
  ElTreeSelect,
} from 'element-plus';

import {
  createText,
  deleteText,
  deleteTextBatch,
  getCategoryTree,
  getTextById,
  getTextPage,
  publishText,
  setTextRecommend,
  setTextTop,
  unpublishText,
  updateText,
} from '#/api/cms';
import { ContentEditor, ContentPreview } from '#/components/editor';
import { ImageUpload } from '#/components/image-upload';

type ContentType = 'html' | 'markdown' | 'text';

// ==================== 状态定义 ====================

const tableData = ref<CmsTextApi.TextVO[]>([]);
const loading = ref(false);
const selectedRows = ref<CmsTextApi.TextVO[]>([]);
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0,
});
const searchForm = reactive<CmsTextApi.QueryParams>({
  keyword: '',
  categoryId: undefined,
  status: undefined,
  top: undefined,
  recommend: undefined,
});

const dialogVisible = ref(false);
const dialogTitle = ref('');
const dialogType = ref<'create' | 'edit'>('create');
const formLoading = ref(false);
const formRef = ref();

// 预览对话框
const previewVisible = ref(false);
const previewData = ref<{
  content: string;
  contentType: ContentType;
  title: string;
}>({
  title: '',
  content: '',
  contentType: 'html',
});

const formData = reactive<CmsTextApi.CreateParams & { id?: number }>({
  categoryId: undefined,
  textKey: '',
  textTitle: '',
  textSubtitle: '',
  textSummary: '',
  textContent: '',
  textType: 'article',
  contentType: 'html',
  coverImage: '',
  author: '',
  source: '',
  linkUrl: '',
  keywords: '',
  tags: '',
  sort: 0,
  top: 0,
  recommend: 0,
  status: 0,
  remark: '',
});

// 分类树
const categoryTree = ref<CmsTextCategoryApi.CategoryVO[]>([]);

// ==================== 常量 ====================

const statusOptions = [
  { label: '已发布', value: 1, type: 'success' as const },
  { label: '草稿', value: 0, type: 'info' as const },
];

const textTypeOptions = [
  { label: '文章', value: 'article' },
  { label: '公告', value: 'notice' },
  { label: '帮助', value: 'help' },
  { label: '协议', value: 'agreement' },
  { label: '关于', value: 'about' },
  { label: '其他', value: 'other' },
];

const contentTypeOptions = [
  { label: 'HTML', value: 'html' },
  { label: 'Markdown', value: 'markdown' },
  { label: '纯文本', value: 'text' },
];

const yesNoOptions = [
  { label: '是', value: 1, type: 'success' as const },
  { label: '否', value: 0, type: 'info' as const },
];

const formRules = {
  textKey: [{ required: true, message: '请输入文本标识', trigger: 'blur' }],
  textTitle: [{ required: true, message: '请输入标题', trigger: 'blur' }],
};

// ==================== 计算属性 ====================

const hasSelected = computed(() => selectedRows.value.length > 0);
const editorContentType = computed(() => formData.contentType as ContentType);

// ==================== 方法 ====================

async function fetchData() {
  loading.value = true;
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
    };
    const res = await getTextPage(params);
    tableData.value = res.records;
    pagination.total = res.totalRow;
  } catch {
    ElMessage.error('获取文本列表失败');
  } finally {
    loading.value = false;
  }
}

async function fetchCategoryTree() {
  try {
    categoryTree.value = await getCategoryTree();
  } catch {
    console.error('获取分类树失败');
  }
}

function handleSearch() {
  pagination.pageNum = 1;
  fetchData();
}

function handleReset() {
  searchForm.keyword = '';
  searchForm.categoryId = undefined;
  searchForm.status = undefined;
  searchForm.top = undefined;
  searchForm.recommend = undefined;
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

function handleSelectionChange(rows: CmsTextApi.TextVO[]) {
  selectedRows.value = rows;
}

function resetForm() {
  formData.id = undefined;
  formData.categoryId = undefined;
  formData.textKey = '';
  formData.textTitle = '';
  formData.textSubtitle = '';
  formData.textSummary = '';
  formData.textContent = '';
  formData.textType = 'article';
  formData.contentType = 'html';
  formData.coverImage = '';
  formData.author = '';
  formData.source = '';
  formData.linkUrl = '';
  formData.keywords = '';
  formData.tags = '';
  formData.sort = 0;
  formData.top = 0;
  formData.recommend = 0;
  formData.status = 0;
  formData.remark = '';
}

async function handleCreate() {
  resetForm();
  dialogType.value = 'create';
  dialogTitle.value = '新增文本';

  // 确保分类树已加载
  if (categoryTree.value.length === 0) {
    await fetchCategoryTree();
  }

  dialogVisible.value = true;
}

async function handleEdit(row: CmsTextApi.TextVO) {
  resetForm();
  dialogType.value = 'edit';
  dialogTitle.value = '编辑文本';
  formLoading.value = true;

  try {
    // 确保分类树已加载
    if (categoryTree.value.length === 0) {
      await fetchCategoryTree();
    }

    const detail = await getTextById(row.id);
    Object.assign(formData, {
      id: detail.id,
      categoryId: detail.categoryId,
      textKey: detail.textKey,
      textTitle: detail.textTitle,
      textSubtitle: detail.textSubtitle || '',
      textSummary: detail.textSummary || '',
      textContent: detail.textContent || '',
      textType: detail.textType || 'article',
      contentType: detail.contentType || 'html',
      coverImage: detail.coverImage || '',
      author: detail.author || '',
      source: detail.source || '',
      linkUrl: detail.linkUrl || '',
      keywords: detail.keywords || '',
      tags: detail.tags || '',
      sort: detail.sort || 0,
      top: detail.top || 0,
      recommend: detail.recommend || 0,
      status: detail.status,
      remark: '',
    });
    dialogVisible.value = true;
  } catch {
    ElMessage.error('获取文本详情失败');
  } finally {
    formLoading.value = false;
  }
}

async function handleSubmit() {
  formLoading.value = true;
  try {
    if (dialogType.value === 'create') {
      await createText(formData as CmsTextApi.CreateParams);
      ElMessage.success('创建成功');
    } else {
      await updateText(formData as CmsTextApi.UpdateParams);
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

async function handleDelete(row: CmsTextApi.TextVO) {
  try {
    await ElMessageBox.confirm(
      `确定要删除文本「${row.textTitle}」吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    await deleteText(row.id);
    ElMessage.success('删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handleBatchDelete() {
  if (!hasSelected.value) {
    ElMessage.warning('请先选择要删除的文本');
    return;
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个文本吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );
    const ids = selectedRows.value.map((row) => row.id);
    await deleteTextBatch(ids);
    ElMessage.success('批量删除成功');
    fetchData();
  } catch {
    // 用户取消
  }
}

async function handlePublish(row: CmsTextApi.TextVO) {
  try {
    if (row.status === 1) {
      await unpublishText(row.id);
      ElMessage.success('已下架');
    } else {
      await publishText(row.id);
      ElMessage.success('已发布');
    }
    fetchData();
  } catch {
    ElMessage.error('操作失败');
  }
}

async function handleToggleTop(row: CmsTextApi.TextVO) {
  try {
    const newValue = row.top === 1 ? 0 : 1;
    await setTextTop(row.id, newValue);
    ElMessage.success(newValue === 1 ? '已置顶' : '已取消置顶');
    fetchData();
  } catch {
    ElMessage.error('操作失败');
  }
}

async function handleToggleRecommend(row: CmsTextApi.TextVO) {
  try {
    const newValue = row.recommend === 1 ? 0 : 1;
    await setTextRecommend(row.id, newValue);
    ElMessage.success(newValue === 1 ? '已推荐' : '已取消推荐');
    fetchData();
  } catch {
    ElMessage.error('操作失败');
  }
}

function getStatusOption(status: number) {
  return (
    statusOptions.find((item) => item.value === status) || statusOptions[1]
  );
}

function getYesNoOption(value?: number) {
  return yesNoOptions.find((item) => item.value === value) || yesNoOptions[1];
}

function getTextTypeLabel(type?: string) {
  return textTypeOptions.find((item) => item.value === type)?.label || '其他';
}

async function handlePreview(row: CmsTextApi.TextVO) {
  try {
    const detail = await getTextById(row.id);
    previewData.value = {
      title: detail.textTitle,
      content: detail.textContent || '',
      contentType:
        (detail.contentType as 'html' | 'markdown' | 'text') || 'html',
    };
    previewVisible.value = true;
  } catch {
    ElMessage.error('获取文本详情失败');
  }
}

function handleFormPreview() {
  previewData.value = {
    title: formData.textTitle || '内容预览',
    content: formData.textContent || '',
    contentType:
      (formData.contentType as 'html' | 'markdown' | 'text') || 'html',
  };
  previewVisible.value = true;
}

// ==================== 生命周期 ====================

onMounted(() => {
  fetchData();
  fetchCategoryTree();
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
              placeholder="标题/标识"
              clearable
              style="width: 180px"
              @keyup.enter="handleSearch"
            />
          </ElFormItem>
          <ElFormItem label="分类">
            <ElTreeSelect
              v-model="searchForm.categoryId"
              :data="categoryTree"
              :props="{
                label: 'categoryName',
                value: 'id',
                children: 'children',
              }"
              check-strictly
              clearable
              placeholder="全部"
              style="width: 150px"
            />
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
          <ElFormItem label="置顶">
            <ElSelect
              v-model="searchForm.top"
              placeholder="全部"
              clearable
              style="width: 80px"
            >
              <ElOption
                v-for="item in yesNoOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </ElSelect>
          </ElFormItem>
          <ElFormItem>
            <ElButton
              type="primary"
              v-access:code="['cms:text:query']"
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
            v-access:code="['cms:text:create']"
            @click="handleCreate"
          >
            <span class="i-lucide-plus mr-1"></span>
            新增文本
          </ElButton>
          <ElButton
            type="danger"
            plain
            :disabled="!hasSelected"
            v-access:code="['cms:text:delete']"
            @click="handleBatchDelete"
          >
            批量删除
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
          label="标题"
          prop="textTitle"
          min-width="200"
          show-overflow-tooltip
        />
        <ElTableColumn
          label="标识"
          prop="textKey"
          width="150"
          show-overflow-tooltip
        />
        <ElTableColumn label="分类" prop="categoryName" width="100" />
        <ElTableColumn label="类型" width="80" align="center">
          <template #default="{ row }">
            {{ getTextTypeLabel(row.textType) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="置顶" width="70" align="center">
          <template #default="{ row }">
            <ElTag :type="getYesNoOption(row.top).type" size="small">
              {{ getYesNoOption(row.top).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="推荐" width="70" align="center">
          <template #default="{ row }">
            <ElTag :type="getYesNoOption(row.recommend).type" size="small">
              {{ getYesNoOption(row.recommend).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn
          label="浏览"
          prop="viewCount"
          width="70"
          align="center"
        />
        <ElTableColumn label="状态" width="80" align="center">
          <template #default="{ row }">
            <ElTag :type="getStatusOption(row.status).type" size="small">
              {{ getStatusOption(row.status).label }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="创建时间" prop="createTime" width="170" />
        <ElTableColumn label="操作" width="380" fixed="right" align="center">
          <template #default="{ row }">
            <ElButton
              type="success"
              link
              size="small"
              @click="handlePreview(row)"
            >
              预览
            </ElButton>
            <ElButton
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              v-access:code="[
                row.status === 1 ? 'cms:text:unpublish' : 'cms:text:publish',
              ]"
              @click="handlePublish(row)"
            >
              {{ row.status === 1 ? '下架' : '发布' }}
            </ElButton>
            <ElButton
              type="info"
              link
              size="small"
              @click="handleToggleTop(row)"
            >
              {{ row.top === 1 ? '取消置顶' : '置顶' }}
            </ElButton>
            <ElButton link size="small" @click="handleToggleRecommend(row)">
              {{ row.recommend === 1 ? '取消推荐' : '推荐' }}
            </ElButton>
            <ElButton
              type="primary"
              link
              size="small"
              v-access:code="['cms:text:update']"
              @click="handleEdit(row)"
            >
              编辑
            </ElButton>
            <ElButton
              type="danger"
              link
              size="small"
              v-access:code="['cms:text:delete']"
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
      width="900px"
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
          <ElCol :span="8">
            <ElFormItem label="文本标识" prop="textKey">
              <ElInput
                v-model="formData.textKey"
                :disabled="dialogType === 'edit'"
                placeholder="请输入文本标识"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="所属分类">
              <ElTreeSelect
                v-model="formData.categoryId"
                :data="categoryTree"
                :props="{
                  label: 'categoryName',
                  value: 'id',
                  children: 'children',
                }"
                check-strictly
                clearable
                placeholder="请选择分类"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="文本类型">
              <ElSelect
                v-model="formData.textType"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in textTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="16">
            <ElFormItem label="标题" prop="textTitle">
              <ElInput v-model="formData.textTitle" placeholder="请输入标题" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="副标题">
              <ElInput
                v-model="formData.textSubtitle"
                placeholder="请输入副标题"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="24">
            <ElFormItem label="摘要">
              <ElInput
                v-model="formData.textSummary"
                type="textarea"
                :rows="2"
                placeholder="请输入摘要"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="内容格式">
              <ElSelect
                v-model="formData.contentType"
                placeholder="请选择"
                style="width: 100%"
              >
                <ElOption
                  v-for="item in contentTypeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </ElSelect>
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="作者">
              <ElInput v-model="formData.author" placeholder="请输入作者" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="8">
            <ElFormItem label="来源">
              <ElInput v-model="formData.source" placeholder="请输入来源" />
            </ElFormItem>
          </ElCol>
          <ElCol :span="24">
            <ElFormItem label="正文内容">
              <div class="w-full">
                <div class="mb-2 flex items-center justify-end">
                  <ElButton
                    type="primary"
                    plain
                    size="small"
                    :disabled="!formData.textContent"
                    @click="handleFormPreview"
                  >
                    预览内容
                  </ElButton>
                </div>
                <ContentEditor
                  v-model="formData.textContent"
                  :content-type="editorContentType"
                  placeholder="请输入正文内容"
                  height="350px"
                />
              </div>
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="封面图片">
              <ImageUpload
                v-model="formData.coverImage"
                biz-type="cms-cover"
                width="120px"
                height="80px"
                tip="建议尺寸：16:9 比例"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="跳转链接">
              <ElInput
                v-model="formData.linkUrl"
                placeholder="请输入跳转链接"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="关键词">
              <ElInput
                v-model="formData.keywords"
                placeholder="多个关键词用逗号分隔"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="12">
            <ElFormItem label="标签">
              <ElInput
                v-model="formData.tags"
                placeholder="多个标签用逗号分隔"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="6">
            <ElFormItem label="排序">
              <ElInputNumber
                v-model="formData.sort"
                :min="0"
                :max="9999"
                style="width: 100%"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="6">
            <ElFormItem label="置顶">
              <ElSwitch
                v-model="formData.top"
                :active-value="1"
                :inactive-value="0"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="6">
            <ElFormItem label="推荐">
              <ElSwitch
                v-model="formData.recommend"
                :active-value="1"
                :inactive-value="0"
              />
            </ElFormItem>
          </ElCol>
          <ElCol :span="6">
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
        </ElRow>
      </ElForm>
      <template #footer>
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" :loading="formLoading" @click="handleSubmit">
          确定
        </ElButton>
      </template>
    </ElDialog>

    <!-- 预览对话框 -->
    <ElDialog
      v-model="previewVisible"
      :title="`预览：${previewData.title}`"
      width="900px"
      destroy-on-close
    >
      <ContentPreview
        :content="previewData.content"
        :content-type="previewData.contentType"
      />
      <template #footer>
        <ElButton @click="previewVisible = false">关闭</ElButton>
      </template>
    </ElDialog>
  </Page>
</template>
