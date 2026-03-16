/**
 * 部门选择组件
 * @description 树形部门选择器，支持无限层级
 * @example
 * <DeptSelector v-model="form.deptId" />
 */

<script lang="ts" setup>
import type { SysDeptTreeVO } from '@/api/types/system'
import { getDeptTree, getPublicDeptTree } from '@/api/system'

// ==================== 类型定义 ====================

interface FlatDeptNode {
  id: number
  name: string
  level: number
  hasChildren: boolean
  parentId: number | null
  originalNode: SysDeptTreeVO
}

// ==================== Props ====================

interface Props {
  /** 标题 */
  label?: string
  /** 占位符 */
  placeholder?: string
  /** 是否禁用 */
  disabled?: boolean
  /** 是否只读 */
  readonly?: boolean
  /** 使用 root-portal */
  rootPortal?: boolean
  /** 是否使用公开接口（无需登录） */
  usePublicApi?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  label: '部门',
  placeholder: '请选择部门',
  disabled: false,
  readonly: false,
  rootPortal: true,
  usePublicApi: false,
})

const emit = defineEmits<{
  change: [value: number | null]
}>()

/** 选中的部门ID */
const modelValue = defineModel<number | null>()

// ==================== 状态 ====================

const loading = ref(false)
const popupVisible = ref(false)
const deptTree = ref<SysDeptTreeVO[]>([])
const expandedKeys = ref<Set<number>>(new Set())
const selectedDept = ref<SysDeptTreeVO | null>(null)
const tempSelectedDept = ref<SysDeptTreeVO | null>(null)

// ==================== 计算属性 ====================

const displayText = computed(() => {
  if (selectedDept.value) {
    return selectedDept.value.deptName
  }
  return ''
})

/**
 * 将树形数据扁平化为列表，根据展开状态过滤
 */
const flattenedNodes = computed(() => {
  const result: FlatDeptNode[] = []

  function traverse(nodes: SysDeptTreeVO[], level: number, parentId: number | null) {
    for (const node of nodes) {
      const hasChildren = Boolean(node.children?.length)
      result.push({
        id: node.id,
        name: node.deptName,
        level,
        hasChildren,
        parentId,
        originalNode: node,
      })

      // 只有展开的节点才遍历子节点
      if (hasChildren && expandedKeys.value.has(node.id)) {
        traverse(node.children!, level + 1, node.id)
      }
    }
  }

  traverse(deptTree.value, 0, null)
  return result
})

// ==================== 方法 ====================

async function fetchDeptTree() {
  if (deptTree.value.length) {
    return
  }

  try {
    loading.value = true
    deptTree.value = props.usePublicApi ? await getPublicDeptTree() : await getDeptTree()
    syncSelection()
  }
  catch (error) {
    console.error('部门数据加载失败:', error)
    uni.showToast({ title: '部门数据加载失败', icon: 'none' })
  }
  finally {
    loading.value = false
  }
}

function findDeptById(tree: SysDeptTreeVO[], targetId: number | null): SysDeptTreeVO | null {
  if (!targetId)
    return null

  for (const node of tree) {
    if (node.id === targetId) {
      return node
    }
    if (node.children?.length) {
      const found = findDeptById(node.children, targetId)
      if (found)
        return found
    }
  }
  return null
}

function findDeptPath(tree: SysDeptTreeVO[], targetId: number | null): number[] {
  if (!targetId)
    return []

  for (const node of tree) {
    if (node.id === targetId) {
      return [node.id]
    }
    if (node.children?.length) {
      const childPath = findDeptPath(node.children, targetId)
      if (childPath.length) {
        return [node.id, ...childPath]
      }
    }
  }
  return []
}

function syncSelection() {
  if (modelValue.value && deptTree.value.length) {
    selectedDept.value = findDeptById(deptTree.value, modelValue.value)
    // 展开选中节点的所有祖先
    const path = findDeptPath(deptTree.value, modelValue.value)
    path.forEach(id => expandedKeys.value.add(id))
  }
  else {
    selectedDept.value = null
  }
}

function openPopup() {
  if (props.disabled || props.readonly)
    return

  fetchDeptTree()
  tempSelectedDept.value = selectedDept.value
  popupVisible.value = true
}

function closePopup() {
  popupVisible.value = false
}

function toggleExpand(node: FlatDeptNode) {
  if (expandedKeys.value.has(node.id)) {
    expandedKeys.value.delete(node.id)
  }
  else {
    expandedKeys.value.add(node.id)
  }
}

function selectDept(node: FlatDeptNode) {
  tempSelectedDept.value = node.originalNode
}

function handleConfirm() {
  selectedDept.value = tempSelectedDept.value
  modelValue.value = selectedDept.value?.id ?? null
  emit('change', modelValue.value)
  closePopup()
}

function handleClear() {
  tempSelectedDept.value = null
}

function isExpanded(node: FlatDeptNode): boolean {
  return expandedKeys.value.has(node.id)
}

function isSelected(node: FlatDeptNode): boolean {
  return tempSelectedDept.value?.id === node.id
}

function getIndentStyle(level: number) {
  return { paddingLeft: `${level * 32 + 16}rpx` }
}

watch(() => modelValue.value, () => {
  syncSelection()
})

watch(() => deptTree.value, () => {
  syncSelection()
})
</script>

<template>
  <wd-cell
    :title="label"
    :value="displayText"
    :placeholder="placeholder"
    is-link
    :clickable="!disabled && !readonly"
    @click="openPopup"
  />

  <wd-popup
    v-model="popupVisible"
    position="bottom"
    :close-on-click-modal="true"
    :safe-area-inset-bottom="true"
    :root-portal="rootPortal"
    @close="closePopup"
  >
    <view class="dept-popup">
      <!-- 头部 -->
      <view class="dept-popup__header">
        <view class="dept-popup__cancel" @click="closePopup">
          取消
        </view>
        <view class="dept-popup__title">
          选择部门
        </view>
        <view class="dept-popup__confirm" @click="handleConfirm">
          确定
        </view>
      </view>

      <!-- 已选中显示 -->
      <view v-if="tempSelectedDept" class="dept-popup__selected">
        <text class="dept-popup__selected-label">已选择:</text>
        <text class="dept-popup__selected-value">{{ tempSelectedDept.deptName }}</text>
        <view class="dept-popup__clear" @click="handleClear">
          <view class="i-carbon-close" />
        </view>
      </view>

      <!-- 加载中 -->
      <view v-if="loading" class="dept-popup__loading">
        <wd-loading />
        <text>加载中...</text>
      </view>

      <!-- 树形列表（扁平化渲染） -->
      <scroll-view v-else class="dept-popup__tree" scroll-y>
        <template v-if="flattenedNodes.length">
          <view
            v-for="node in flattenedNodes"
            :key="node.id"
            class="dept-tree-item"
            :class="{ 'is-selected': isSelected(node) }"
            :style="getIndentStyle(node.level)"
            @click="selectDept(node)"
          >
            <view
              v-if="node.hasChildren"
              class="dept-tree-expand"
              :class="{ 'is-expanded': isExpanded(node) }"
              @click.stop="toggleExpand(node)"
            >
              <view class="i-carbon-chevron-right" />
            </view>
            <view v-else class="dept-tree-expand dept-tree-expand--placeholder" />
            <text class="dept-tree-label">{{ node.name }}</text>
            <view v-if="isSelected(node)" class="dept-tree-check">
              <view class="i-carbon-checkmark" />
            </view>
          </view>
        </template>

        <view v-else class="dept-popup__empty">
          暂无部门数据
        </view>
      </scroll-view>
    </view>
  </wd-popup>
</template>

<style lang="scss" scoped>
$primary: #0ea5e9;
$text-primary: #0f172a;
$text-secondary: #64748b;
$bg-page: #f8fafc;
$border: #e2e8f0;

.dept-popup {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.dept-popup__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid $border;
}

.dept-popup__cancel {
  font-size: 28rpx;
  color: $text-secondary;
  padding: 8rpx 16rpx;
}

.dept-popup__title {
  font-size: 32rpx;
  font-weight: 600;
  color: $text-primary;
}

.dept-popup__confirm {
  font-size: 28rpx;
  color: $primary;
  font-weight: 500;
  padding: 8rpx 16rpx;
}

.dept-popup__selected {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  background: rgba($primary, 0.05);
  border-bottom: 1rpx solid $border;
}

.dept-popup__selected-label {
  font-size: 26rpx;
  color: $text-secondary;
  margin-right: 12rpx;
}

.dept-popup__selected-value {
  flex: 1;
  font-size: 28rpx;
  color: $primary;
  font-weight: 500;
}

.dept-popup__clear {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-secondary;
  font-size: 28rpx;
}

.dept-popup__loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  color: $text-secondary;
  font-size: 26rpx;

  text {
    margin-top: 16rpx;
  }
}

.dept-popup__tree {
  flex: 1;
  max-height: 60vh;
  padding-bottom: calc(32rpx + env(safe-area-inset-bottom));
}

.dept-popup__empty {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80rpx 0;
  color: $text-secondary;
  font-size: 28rpx;
}

.dept-tree-item {
  display: flex;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 1rpx solid rgba($border, 0.5);
  transition: background-color 0.2s;

  &:active {
    background: $bg-page;
  }

  &.is-selected {
    background: rgba($primary, 0.08);

    .dept-tree-label {
      color: $primary;
      font-weight: 500;
    }
  }
}

.dept-tree-expand {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 8rpx;
  color: $text-secondary;
  font-size: 24rpx;
  transition: transform 0.2s;

  &.is-expanded {
    transform: rotate(90deg);
  }

  &--placeholder {
    // 占位，保持对齐
  }
}

.dept-tree-label {
  flex: 1;
  font-size: 28rpx;
  color: $text-primary;
}

.dept-tree-check {
  width: 40rpx;
  height: 40rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $primary;
  font-size: 28rpx;
}
</style>
