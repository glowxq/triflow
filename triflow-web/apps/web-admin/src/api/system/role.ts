import type { PageResult, SysRoleApi } from './types';

/**
 * 角色管理 API
 * @description 角色的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/role';

/**
 * 创建角色
 */
export async function createRole(data: SysRoleApi.CreateParams) {
  return requestClient.post<number>(`${BASE_URL}`, data);
}

/**
 * 更新角色
 */
export async function updateRole(data: SysRoleApi.UpdateParams) {
  return requestClient.put<boolean>(`${BASE_URL}`, data);
}

/**
 * 获取角色详情
 */
export async function getRoleById(id: number) {
  return requestClient.get<SysRoleApi.RoleVO>(`${BASE_URL}/${id}`);
}

/**
 * 删除角色
 */
export async function deleteRole(id: number) {
  return requestClient.delete<boolean>(`${BASE_URL}/${id}`);
}

/**
 * 批量删除角色
 */
export async function deleteRoleBatch(ids: number[]) {
  return requestClient.delete<boolean>(`${BASE_URL}/batch`, { data: ids });
}

/**
 * 查询角色列表
 */
export async function getRoleList(params?: SysRoleApi.QueryParams) {
  return requestClient.get<SysRoleApi.RoleVO[]>(`${BASE_URL}/list`, { params });
}

/**
 * 分页查询角色
 */
export async function getRolePage(params?: SysRoleApi.QueryParams) {
  return requestClient.get<PageResult<SysRoleApi.RoleVO>>(`${BASE_URL}/page`, {
    params,
  });
}

/**
 * 获取所有启用的角色
 */
export async function getAllRoles() {
  return requestClient.get<SysRoleApi.RoleVO[]>(`${BASE_URL}/all`);
}

/**
 * 分配菜单权限
 */
export async function assignMenus(id: number, menuIds: number[]) {
  return requestClient.put<null>(`${BASE_URL}/${id}/menus`, menuIds);
}

/**
 * 获取角色菜单ID列表
 */
export async function getRoleMenus(id: number) {
  return requestClient.get<number[]>(`${BASE_URL}/${id}/menus`);
}

/**
 * 导出角色
 * @permission system:role:export
 */
export async function exportRole(params?: SysRoleApi.QueryParams) {
  return requestClient.get<Blob>(`${BASE_URL}/export`, {
    params,
    responseType: 'blob',
    responseReturn: 'body',
  });
}

/**
 * 导入角色
 * @permission system:role:import
 */
export async function importRole(file: File) {
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
 * 下载角色导入模板
 */
export async function downloadRoleTemplate() {
  return requestClient.get<Blob>(`${BASE_URL}/template`, {
    responseType: 'blob',
    responseReturn: 'body',
  });
}
