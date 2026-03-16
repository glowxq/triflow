import type { SysMenuApi } from './types';

/**
 * 菜单管理 API
 * @description 菜单的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/menu';

/**
 * 创建菜单
 */
export async function createMenu(data: SysMenuApi.CreateParams) {
  return requestClient.post<number>(`${BASE_URL}`, data);
}

/**
 * 更新菜单
 */
export async function updateMenu(data: SysMenuApi.UpdateParams) {
  return requestClient.put<boolean>(`${BASE_URL}`, data);
}

/**
 * 获取菜单详情
 */
export async function getMenuById(id: number) {
  return requestClient.get<SysMenuApi.MenuVO>(`${BASE_URL}/${id}`);
}

/**
 * 删除菜单
 */
export async function deleteMenu(id: number) {
  return requestClient.delete<boolean>(`${BASE_URL}/${id}`);
}

/**
 * 查询菜单列表
 */
export async function getMenuList(params?: SysMenuApi.QueryParams) {
  return requestClient.get<SysMenuApi.MenuVO[]>(`${BASE_URL}/list`, { params });
}

/**
 * 获取菜单树
 */
export async function getMenuTree() {
  return requestClient.get<SysMenuApi.MenuTreeVO[]>(`${BASE_URL}/tree`);
}

/**
 * 获取所有权限标识
 */
export async function getAllPermissions() {
  return requestClient.get<string[]>(`${BASE_URL}/permissions`);
}

/**
 * 导出菜单
 * @permission system:menu:export
 */
export async function exportMenu(params?: SysMenuApi.QueryParams) {
  return requestClient.get<Blob>(`${BASE_URL}/export`, {
    params,
    responseType: 'blob',
    responseReturn: 'body',
  });
}

/**
 * 导入菜单
 * @permission system:menu:import
 */
export async function importMenu(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post<{
    fail: number;
    message?: string;
    success: number;
  }>(`${BASE_URL}/import`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

/**
 * 下载菜单导入模板
 */
export async function downloadMenuTemplate() {
  return requestClient.get<Blob>(`${BASE_URL}/template`, {
    responseType: 'blob',
    responseReturn: 'body',
  });
}
