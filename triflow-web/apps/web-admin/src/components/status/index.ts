/**
 * 状态组件
 * @description 提供统一的状态显示和选择组件
 */

export { default as StatusSelect } from './StatusSelect.vue';
export { default as StatusSwitch } from './StatusSwitch.vue';
export { default as StatusTag } from './StatusTag.vue';

/**
 * 状态常量
 */
export const STATUS_OPTIONS = [
  { label: '正常', value: 1, type: 'success' as const },
  { label: '禁用', value: 0, type: 'danger' as const },
] as const;

/**
 * 获取状态标签类型
 */
export function getStatusType(status: number): 'danger' | 'success' {
  return status === 1 ? 'success' : 'danger';
}

/**
 * 获取状态标签文本
 */
export function getStatusText(
  status: number,
  enabledText = '正常',
  disabledText = '禁用',
): string {
  return status === 1 ? enabledText : disabledText;
}
