import type { PageResult, SysSwitchApi } from './types';

/**
 * 开关管理 API
 * @description 系统开关的增删改查接口，支持灰度发布
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/switch';

/**
 * 创建开关
 */
export async function createSwitch(data: SysSwitchApi.CreateParams) {
  return requestClient.post(`${BASE_URL}/create`, data);
}

/**
 * 更新开关
 */
export async function updateSwitch(data: SysSwitchApi.UpdateParams) {
  return requestClient.put(`${BASE_URL}/update`, data);
}

/**
 * 获取开关详情
 */
export async function getSwitchById(id: number) {
  return requestClient.get<SysSwitchApi.SwitchVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 切换开关状态
 */
export async function toggleSwitch(
  id: number,
  switchValue: number,
  changeReason?: string,
) {
  return requestClient.put(
    `${BASE_URL}/toggle`,
    { switchValue, changeReason },
    {
      params: { id },
    },
  );
}

/**
 * 检查开关是否开启（支持灰度）
 */
export async function isEnabled(switchKey: string, userId?: number) {
  return requestClient.get<boolean>(`${BASE_URL}/check`, {
    params: { switchKey, userId },
  });
}

/**
 * 根据键获取开关信息
 */
export async function getSwitchByKey(switchKey: string) {
  return requestClient.get<SysSwitchApi.SwitchVO>(`${BASE_URL}/getByKey`, {
    params: { switchKey },
  });
}

/**
 * 检查开关键是否存在
 */
export async function checkSwitchKey(switchKey: string, excludeId?: number) {
  return requestClient.get<boolean>(`${BASE_URL}/checkKey`, {
    params: { switchKey, excludeId },
  });
}

/**
 * 删除开关
 */
export async function deleteSwitch(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除开关
 */
export async function deleteSwitchBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 查询开关列表
 */
export async function getSwitchList(params?: SysSwitchApi.QueryParams) {
  return requestClient.post<SysSwitchApi.SwitchVO[]>(
    `${BASE_URL}/list`,
    params,
  );
}

/**
 * 分页查询开关
 */
export async function getSwitchPage(params?: SysSwitchApi.QueryParams) {
  return requestClient.post<PageResult<SysSwitchApi.SwitchVO>>(
    `${BASE_URL}/page`,
    params,
  );
}

/**
 * 根据分类获取开关
 */
export async function getSwitchByCategory(category: string) {
  return requestClient.get<SysSwitchApi.SwitchVO[]>(
    `${BASE_URL}/listByCategory`,
    {
      params: { category },
    },
  );
}

/**
 * 根据类型获取开关
 */
export async function getSwitchByType(switchType: string) {
  return requestClient.get<SysSwitchApi.SwitchVO[]>(`${BASE_URL}/listByType`, {
    params: { switchType },
  });
}

/**
 * 获取所有开启的开关
 */
export async function getAllEnabledSwitches() {
  return requestClient.get<SysSwitchApi.SwitchVO[]>(`${BASE_URL}/listAll`);
}

/**
 * 获取开关操作日志
 */
export async function getSwitchLogs(id: number) {
  return requestClient.get<SysSwitchApi.SwitchLogVO[]>(`${BASE_URL}/logs`, {
    params: { id },
  });
}

/**
 * 分页获取开关操作日志
 */
export async function getSwitchLogPage(
  id: number,
  pageNum: number = 1,
  pageSize: number = 10,
) {
  return requestClient.get<PageResult<SysSwitchApi.SwitchLogVO>>(
    `${BASE_URL}/logs/page`,
    {
      params: { id, pageNum, pageSize },
    },
  );
}

/**
 * 刷新开关缓存
 */
export async function refreshSwitchCache() {
  return requestClient.post(`${BASE_URL}/refreshCache`);
}

// ==================== 枚举接口 ====================

/** 枚举项类型 */
export interface EnumItem {
  code: string;
  name: string;
}

/**
 * 获取开关分类枚举列表
 */
export async function getSwitchCategoryEnums() {
  return requestClient.get<EnumItem[]>(`${BASE_URL}/enums/categories`);
}

/**
 * 获取开关类型枚举列表
 */
export async function getSwitchTypeEnums() {
  return requestClient.get<EnumItem[]>(`${BASE_URL}/enums/types`);
}

/**
 * 获取开关作用范围枚举列表
 */
export async function getSwitchScopeEnums() {
  return requestClient.get<EnumItem[]>(`${BASE_URL}/enums/scopes`);
}

/**
 * 获取开关生效策略枚举列表
 */
export async function getSwitchStrategyEnums() {
  return requestClient.get<EnumItem[]>(`${BASE_URL}/enums/strategies`);
}

// ==================== 导入导出接口 ====================

/**
 * 导出开关列表
 */
export async function exportSwitchList(params?: SysSwitchApi.QueryParams) {
  return requestClient.post(`${BASE_URL}/export`, params, {
    responseType: 'blob',
  });
}

/**
 * 导入开关
 */
export async function importSwitchData(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post<string>(`${BASE_URL}/import`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

/**
 * 下载开关导入模板
 */
export async function downloadSwitchTemplate() {
  return requestClient.get(`${BASE_URL}/template`, {
    responseType: 'blob',
  });
}

// ==================== 枚举对比接口 ====================

/** 枚举开关项 */
export interface EnumSwitchItem {
  enumName: string;
  switchKey: string;
  description: string;
}

/** 枚举对比结果 */
export interface SwitchEnumCompareResult {
  /** 枚举中有但数据库没有的开关 */
  missingInDatabase: EnumSwitchItem[];
  /** 数据库中有但枚举没有的开关 */
  missingInEnum: string[];
  /** 枚举与数据库同步的开关 */
  synced: string[];
  /** 枚举定义总数 */
  enumCount: number;
  /** 数据库开关总数 */
  databaseCount: number;
}

/**
 * 获取开关键枚举列表
 */
export async function getSwitchKeyEnums() {
  return requestClient.get<
    Array<{ code: string; enumName: string; name: string }>
  >(`${BASE_URL}/enums/keys`);
}

/**
 * 对比枚举定义与数据库中的开关
 */
export async function compareSwitchWithEnum() {
  return requestClient.get<SwitchEnumCompareResult>(`${BASE_URL}/compare`);
}

/**
 * 一键删除枚举中不存在的开关
 */
export async function deleteNotInEnum() {
  return requestClient.delete<number>(`${BASE_URL}/deleteNotInEnum`);
}

/**
 * 一键初始化枚举中的开关
 */
export async function initFromEnum() {
  return requestClient.post<number>(`${BASE_URL}/initFromEnum`);
}
