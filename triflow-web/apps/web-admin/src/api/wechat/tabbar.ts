import type { PageResult } from '../system/types';
import type { WechatTabbarApi } from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/wechat/tabbar';

/**
 * 创建底部菜单
 */
export function createWechatTabbar(data: WechatTabbarApi.CreateParams) {
  return requestClient.post<number>(`${BASE_URL}/create`, data);
}

/**
 * 更新底部菜单
 */
export function updateWechatTabbar(data: WechatTabbarApi.UpdateParams) {
  return requestClient.put<boolean>(`${BASE_URL}/update`, data);
}

/**
 * 获取底部菜单详情
 */
export function getWechatTabbarById(id: number) {
  return requestClient.get<WechatTabbarApi.TabbarVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 删除底部菜单
 */
export function deleteWechatTabbar(id: number) {
  return requestClient.delete<boolean>(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除底部菜单
 */
export function deleteWechatTabbarBatch(ids: number[]) {
  return requestClient.delete<boolean>(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 获取底部菜单列表
 */
export function getWechatTabbarList(params: WechatTabbarApi.QueryParams) {
  return requestClient.post<WechatTabbarApi.TabbarVO[]>(
    `${BASE_URL}/list`,
    params,
  );
}

/**
 * 分页获取底部菜单列表
 */
export function getWechatTabbarPage(params: WechatTabbarApi.QueryParams) {
  return requestClient.post<PageResult<WechatTabbarApi.TabbarVO>>(
    `${BASE_URL}/page`,
    params,
  );
}
