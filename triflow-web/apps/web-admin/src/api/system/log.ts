/**
 * 操作日志 API
 * @description 操作日志的查询、删除、导出接口
 */
import type { LogOperationApi, PageResult } from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/log';

/**
 * 分页查询操作日志
 */
export async function getLogPage(params?: LogOperationApi.QueryParams) {
  return requestClient.get<PageResult<LogOperationApi.LogVO>>(
    `${BASE_URL}/page`,
    { params },
  );
}

/**
 * 获取日志详情
 */
export async function getLogById(id: number) {
  return requestClient.get<LogOperationApi.LogVO>(`${BASE_URL}/${id}`);
}

/**
 * 批量删除操作日志
 */
export async function deleteLogBatch(ids: number[]) {
  return requestClient.delete<boolean>(`${BASE_URL}/batch`, { data: ids });
}

/**
 * 清空操作日志
 */
export async function clearLog() {
  return requestClient.delete<number>(`${BASE_URL}/clear`);
}

/**
 * 导出操作日志
 * @permission system:log:export
 */
export async function exportLog(params?: LogOperationApi.QueryParams) {
  return requestClient.get<Blob>(`${BASE_URL}/export`, {
    params,
    responseType: 'blob',
    responseReturn: 'body',
  });
}
