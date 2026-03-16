import type { SysDeptApi } from './types';

/**
 * 部门管理 API
 * @description 部门的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/dept';

/**
 * 创建部门
 */
export async function createDept(data: SysDeptApi.CreateParams) {
  return requestClient.post<number>(`${BASE_URL}`, data);
}

/**
 * 更新部门
 */
export async function updateDept(data: SysDeptApi.UpdateParams) {
  return requestClient.put<boolean>(`${BASE_URL}`, data);
}

/**
 * 获取部门详情
 */
export async function getDeptById(id: number) {
  return requestClient.get<SysDeptApi.DeptVO>(`${BASE_URL}/${id}`);
}

/**
 * 删除部门
 */
export async function deleteDept(id: number) {
  return requestClient.delete<boolean>(`${BASE_URL}/${id}`);
}

/**
 * 查询部门列表
 */
export async function getDeptList(params?: SysDeptApi.QueryParams) {
  return requestClient.get<SysDeptApi.DeptVO[]>(`${BASE_URL}/list`, { params });
}

/**
 * 获取部门树
 */
export async function getDeptTree() {
  return requestClient.get<SysDeptApi.DeptTreeVO[]>(`${BASE_URL}/tree`);
}

/**
 * 导出部门
 * @permission system:dept:export
 */
export async function exportDept(params?: SysDeptApi.QueryParams) {
  return requestClient.get<Blob>(`${BASE_URL}/export`, {
    params,
    responseType: 'blob',
    responseReturn: 'body',
  });
}

/**
 * 导入部门
 * @permission system:dept:import
 */
export async function importDept(file: File) {
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
 * 下载部门导入模板
 */
export async function downloadDeptTemplate() {
  return requestClient.get<Blob>(`${BASE_URL}/template`, {
    responseType: 'blob',
    responseReturn: 'body',
  });
}
