import type { PageResult } from '../system/types';
import type { NotifySubscribeApi } from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/notify/subscribe';

/**
 * 获取订阅列表
 */
export function getNotifySubscribeList(params: NotifySubscribeApi.QueryParams) {
  return requestClient.post<NotifySubscribeApi.SubscribeVO[]>(
    `${BASE_URL}/list`,
    params,
  );
}

/**
 * 分页获取订阅列表
 */
export function getNotifySubscribePage(params: NotifySubscribeApi.QueryParams) {
  return requestClient.post<PageResult<NotifySubscribeApi.SubscribeVO>>(
    `${BASE_URL}/page`,
    params,
  );
}

/**
 * 更新订阅状态
 */
export function updateNotifySubscribeStatus(
  data: NotifySubscribeApi.UpdateStatusParams,
) {
  return requestClient.put(`${BASE_URL}/updateStatus`, data);
}

/**
 * 删除订阅记录
 */
export function deleteNotifySubscribe(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除订阅记录
 */
export function deleteNotifySubscribeBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}
