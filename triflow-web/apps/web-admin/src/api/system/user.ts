/**
 * 用户管理 API
 * @description 用户的增删改查接口
 */
import type { PageResult, SysUserApi } from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/user';

/**
 * 创建用户
 */
export async function createUser(data: SysUserApi.CreateParams) {
  return requestClient.post<number>(`${BASE_URL}`, data);
}

/**
 * 更新用户
 */
export async function updateUser(data: SysUserApi.UpdateParams) {
  return requestClient.put<boolean>(`${BASE_URL}`, data);
}

/**
 * 获取用户详情
 */
export async function getUserById(id: number) {
  return requestClient.get<SysUserApi.UserDetailVO>(`${BASE_URL}/${id}`);
}

/**
 * 删除用户
 */
export async function deleteUser(id: number) {
  return requestClient.delete<boolean>(`${BASE_URL}/${id}`);
}

/**
 * 批量删除用户
 */
export async function deleteUserBatch(ids: number[]) {
  return requestClient.delete<boolean>(`${BASE_URL}/batch`, { data: ids });
}

/**
 * 查询用户列表
 */
export async function getUserList(params?: SysUserApi.QueryParams) {
  return requestClient.get<SysUserApi.UserVO[]>(`${BASE_URL}/list`, { params });
}

/**
 * 分页查询用户
 */
export async function getUserPage(params?: SysUserApi.QueryParams) {
  return requestClient.get<PageResult<SysUserApi.UserVO>>(`${BASE_URL}/page`, {
    params,
  });
}

/**
 * 重置密码
 */
export async function resetPassword(id: number, newPassword: string) {
  return requestClient.put<boolean>(
    `${BASE_URL}/${id}/reset-password`,
    {},
    { params: { newPassword } },
  );
}

/**
 * 分配角色
 */
export async function assignRoles(id: number, roleIds: number[]) {
  return requestClient.put<null>(`${BASE_URL}/${id}/roles`, roleIds);
}

/**
 * 导出用户
 * @permission system:user:export
 */
export async function exportUser(params?: SysUserApi.QueryParams) {
  return requestClient.get<Blob>(`${BASE_URL}/export`, {
    params,
    responseType: 'blob',
    responseReturn: 'body',
  });
}

/**
 * 导入用户
 * @permission system:user:import
 */
export async function importUser(file: File) {
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
 * 下载用户导入模板
 */
export async function downloadUserTemplate() {
  return requestClient.get<Blob>(`${BASE_URL}/template`, {
    responseType: 'blob',
    responseReturn: 'body',
  });
}

/**
 * 踢用户下线
 * @permission system:user:kickout
 */
export async function kickoutUser(id: number) {
  return requestClient.post<null>(`${BASE_URL}/${id}/kickout`);
}

/**
 * 批量踢用户下线
 * @permission system:user:kickout
 */
export async function kickoutUserBatch(ids: number[]) {
  return requestClient.post<null>(`${BASE_URL}/kickout/batch`, ids);
}

/**
 * 获取用户第三方绑定列表
 */
export async function getUserSocials(userId: number) {
  return requestClient.get<SysUserApi.UserSocialVO[]>(
    `${BASE_URL}/${userId}/socials`,
  );
}

/**
 * 解绑第三方账号
 */
export async function unbindSocial(userId: number, socialType: string) {
  return requestClient.delete<boolean>(
    `${BASE_URL}/${userId}/socials/${socialType}`,
  );
}

/**
 * 批量设置数据权限
 * @permission system:user:edit
 */
export async function batchUpdateDataScope(
  userIds: number[],
  dataScope: string,
) {
  return requestClient.put<number>(`${BASE_URL}/batch/data-scope`, null, {
    params: { userIds: userIds.join(','), dataScope },
  });
}
