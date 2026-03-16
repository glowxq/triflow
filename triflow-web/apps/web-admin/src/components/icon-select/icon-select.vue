<script lang="ts" setup>
/**
 * 图标选择器组件
 * @description 基于 Iconify 的图标选择器，适配 Element Plus
 */
import { computed, ref, watch } from 'vue';

import { IconifyIcon } from '@vben/icons';

import {
  ElInput,
  ElPagination,
  ElPopover,
  ElScrollbar,
  ElTabPane,
  ElTabs,
} from 'element-plus';

withDefaults(defineProps<Props>(), {
  modelValue: '',
  placeholder: '请选择图标',
});

const emit = defineEmits<{
  change: [value: string];
  'update:modelValue': [value: string];
}>();

// Lucide 图标集 - 常用图标
const LUCIDE_ICONS = [
  'home',
  'user',
  'users',
  'settings',
  'menu',
  'search',
  'plus',
  'minus',
  'check',
  'x',
  'edit',
  'trash',
  'trash-2',
  'save',
  'download',
  'upload',
  'file',
  'file-text',
  'folder',
  'folder-open',
  'image',
  'camera',
  'video',
  'music',
  'mail',
  'phone',
  'message-circle',
  'message-square',
  'bell',
  'calendar',
  'clock',
  'map-pin',
  'navigation',
  'globe',
  'link',
  'external-link',
  'eye',
  'eye-off',
  'lock',
  'unlock',
  'key',
  'shield',
  'shield-check',
  'star',
  'heart',
  'thumbs-up',
  'thumbs-down',
  'bookmark',
  'flag',
  'tag',
  'filter',
  'sort-asc',
  'sort-desc',
  'list',
  'grid',
  'layout-grid',
  'layout-list',
  'chevron-up',
  'chevron-down',
  'chevron-left',
  'chevron-right',
  'arrow-up',
  'arrow-down',
  'arrow-left',
  'arrow-right',
  'refresh-cw',
  'rotate-cw',
  'copy',
  'clipboard',
  'scissors',
  'move',
  'maximize',
  'minimize',
  'sun',
  'moon',
  'cloud',
  'zap',
  'battery',
  'wifi',
  'bluetooth',
  'monitor',
  'smartphone',
  'tablet',
  'laptop',
  'server',
  'database',
  'hard-drive',
  'cpu',
  'code',
  'terminal',
  'git-branch',
  'git-commit',
  'git-merge',
  'package',
  'box',
  'archive',
  'shopping-cart',
  'shopping-bag',
  'credit-card',
  'dollar-sign',
  'percent',
  'trending-up',
  'trending-down',
  'bar-chart',
  'pie-chart',
  'activity',
  'layers',
  'layout',
  'sidebar',
  'panel-left',
  'panel-right',
  'building',
  'building-2',
  'warehouse',
  'store',
  'briefcase',
  'award',
  'users-round',
  'user-check',
  'user-plus',
  'user-minus',
  'user-x',
  'log-in',
  'log-out',
  'power',
  'play',
  'pause',
  'stop',
  'skip-forward',
  'skip-back',
  'volume',
  'volume-1',
  'volume-2',
  'volume-x',
  'mic',
  'mic-off',
  'printer',
  'scan',
  'qr-code',
  'fingerprint',
  'share',
  'share-2',
  'info',
  'help-circle',
  'alert-circle',
  'alert-triangle',
  'ban',
  'circle-check',
  'circle-x',
  'circle-alert',
  'octagon',
  'square',
  'circle',
  'triangle',
].map((name) => `lucide:${name}`);

// Carbon 图标集 - 补充图标
const CARBON_ICONS = [
  'workspace',
  'dashboard',
  'analytics',
  'report',
  'document',
  'template',
  'application',
  'api',
  'connect',
  'data-base',
  'cloud-upload',
  'cloud-download',
  'security',
  'policy',
  'rule',
  'flow',
  'task',
  'notebook',
].map((name) => `carbon:${name}`);

// 所有图标
const ALL_ICONS = [...LUCIDE_ICONS, ...CARBON_ICONS];

interface Props {
  modelValue?: string;
  placeholder?: string;
}

// 状态
const visible = ref(false);
const keyword = ref('');
const currentPage = ref(1);
const pageSize = 60;
const activeTab = ref('all');

// 计算属性
const filteredIcons = computed(() => {
  let icons = ALL_ICONS;

  if (activeTab.value === 'lucide') {
    icons = LUCIDE_ICONS;
  } else if (activeTab.value === 'carbon') {
    icons = CARBON_ICONS;
  }

  if (keyword.value) {
    const kw = keyword.value.toLowerCase();
    icons = icons.filter((icon) => icon.toLowerCase().includes(kw));
  }

  return icons;
});

const pagedIcons = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredIcons.value.slice(start, start + pageSize);
});

const total = computed(() => filteredIcons.value.length);

// 方法
function handleSelect(icon: string) {
  emit('update:modelValue', icon);
  emit('change', icon);
  visible.value = false;
}

function handleClear() {
  emit('update:modelValue', '');
  emit('change', '');
}

function handlePageChange(page: number) {
  currentPage.value = page;
}

// 监听搜索重置分页
watch(keyword, () => {
  currentPage.value = 1;
});

watch(activeTab, () => {
  currentPage.value = 1;
});
</script>

<template>
  <ElPopover
    v-model:visible="visible"
    :width="480"
    placement="bottom-start"
    trigger="click"
  >
    <template #reference>
      <ElInput
        :model-value="modelValue"
        :placeholder="placeholder"
        readonly
        class="cursor-pointer"
      >
        <template #prefix>
          <IconifyIcon
            v-if="modelValue"
            :icon="modelValue"
            class="size-4 text-primary"
          />
          <span v-else class="text-xs text-gray-400">无</span>
        </template>
        <template #suffix>
          <span
            v-if="modelValue"
            class="cursor-pointer text-xs text-gray-400 hover:text-red-500"
            @click.stop="handleClear"
          >
            清除
          </span>
        </template>
      </ElInput>
    </template>

    <div class="icon-select-panel">
      <!-- 搜索框 -->
      <div class="mb-3">
        <ElInput
          v-model="keyword"
          placeholder="搜索图标..."
          clearable
          size="small"
        >
          <template #prefix>
            <IconifyIcon icon="lucide:search" class="size-4 text-gray-400" />
          </template>
        </ElInput>
      </div>

      <!-- 图标分类 -->
      <ElTabs v-model="activeTab" class="icon-tabs">
        <ElTabPane label="全部" name="all" />
        <ElTabPane label="Lucide" name="lucide" />
        <ElTabPane label="Carbon" name="carbon" />
      </ElTabs>

      <!-- 图标列表 -->
      <ElScrollbar height="280px">
        <div class="icon-grid">
          <div
            v-for="icon in pagedIcons"
            :key="icon"
            class="icon-item"
            :class="{ 'is-active': modelValue === icon }"
            :title="icon"
            @click="handleSelect(icon)"
          >
            <IconifyIcon :icon="icon" class="size-5" />
            <span class="icon-name">{{ icon.split(':')[1] }}</span>
          </div>
        </div>
        <div v-if="pagedIcons.length === 0" class="empty-tip">
          没有找到匹配的图标
        </div>
      </ElScrollbar>

      <!-- 分页 -->
      <div v-if="total > pageSize" class="mt-3 flex justify-center">
        <ElPagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :pager-count="5"
          background
          layout="prev, pager, next"
          small
          @current-change="handlePageChange"
        />
      </div>
    </div>
  </ElPopover>
</template>

<style scoped>
.icon-select-panel {
  padding: 4px;
}

.icon-tabs :deep(.el-tabs__header) {
  margin-bottom: 12px;
}

.icon-tabs :deep(.el-tabs__item) {
  padding: 0 12px;
  font-size: 13px;
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 6px;
}

.icon-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  align-items: center;
  justify-content: center;
  padding: 8px 4px;
  cursor: pointer;
  border: 1px solid transparent;
  border-radius: 6px;
  transition: all 0.2s;
}

.icon-item:hover {
  background-color: #f5f7fa;
  border-color: #dcdfe6;
}

.icon-item.is-active {
  color: #409eff;
  background-color: #ecf5ff;
  border-color: #409eff;
}

.icon-name {
  max-width: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 10px;
  color: #909399;
  text-align: center;
  white-space: nowrap;
}

.icon-item.is-active .icon-name {
  color: #409eff;
}

.empty-tip {
  padding: 40px 0;
  font-size: 14px;
  color: #909399;
  text-align: center;
}
</style>
