import type { PageResult, SysConfigApi } from './types';

/**
 * 系统配置 API
 * @description 系统配置的增删改查接口
 */
import { baseRequestClient, requestClient } from '#/api/request';

const BASE_URL = '/base/admin/system/config';

/**
 * 配置版本信息
 */
export interface ConfigVersion {
  /** 应用版本号 */
  appVersion: string;
  /** 前端 preferences 缓存版本号 */
  preferencesVersion: string;
}

/**
 * 获取配置版本号（无需登录）
 * @description 用于前端检查配置版本，决定是否清除本地缓存
 */
export async function getConfigVersion() {
  const response = await baseRequestClient.get<{
    code: number;
    data: ConfigVersion;
  }>('/base/public/config/version');
  return response.data;
}

/**
 * 创建配置
 */
export async function createConfig(data: SysConfigApi.CreateParams) {
  return requestClient.post(`${BASE_URL}/create`, data);
}

/**
 * 更新配置
 */
export async function updateConfig(data: SysConfigApi.UpdateParams) {
  return requestClient.put(`${BASE_URL}/update`, data);
}

/**
 * 获取配置详情
 */
export async function getConfigById(id: number) {
  return requestClient.get<SysConfigApi.ConfigVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 根据配置键获取配置值
 */
export async function getConfigByKey(configKey: string) {
  return requestClient.get<string>(`${BASE_URL}/getByKey`, {
    params: { configKey },
  });
}

/**
 * 删除配置
 */
export async function deleteConfig(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除配置
 */
export async function deleteConfigBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 查询配置列表
 */
export async function getConfigList(params?: SysConfigApi.QueryParams) {
  return requestClient.post<SysConfigApi.ConfigVO[]>(
    `${BASE_URL}/list`,
    params,
  );
}

/**
 * 分页查询配置
 */
export async function getConfigPage(params?: SysConfigApi.QueryParams) {
  return requestClient.post<PageResult<SysConfigApi.ConfigVO>>(
    `${BASE_URL}/page`,
    params,
  );
}

/**
 * 根据分类获取配置
 */
export async function getConfigByCategory(category: string) {
  return requestClient.get<SysConfigApi.ConfigVO[]>(
    `${BASE_URL}/listByCategory`,
    {
      params: { category },
    },
  );
}

/**
 * 获取所有启用的配置
 */
export async function getAllEnabledConfigs() {
  return requestClient.get<SysConfigApi.ConfigVO[]>(`${BASE_URL}/listAll`);
}

/**
 * 刷新配置缓存
 */
export async function refreshConfigCache() {
  return requestClient.post(`${BASE_URL}/refreshCache`);
}

/**
 * 检查配置键是否存在
 */
export async function checkConfigKey(configKey: string, excludeId?: number) {
  return requestClient.get<boolean>(`${BASE_URL}/checkKey`, {
    params: { configKey, excludeId },
  });
}

// ==================== 枚举接口 ====================

/** 枚举项类型 */
export interface ConfigEnumItem {
  code: string;
  name: string;
}

/**
 * 获取配置分类枚举列表
 */
export async function getConfigCategoryEnums() {
  return requestClient.get<ConfigEnumItem[]>(`${BASE_URL}/enums/categories`);
}

/**
 * 获取配置类型枚举列表
 */
export async function getConfigTypeEnums() {
  return requestClient.get<ConfigEnumItem[]>(`${BASE_URL}/enums/types`);
}

/**
 * 获取配置值类型枚举列表
 */
export async function getConfigValueTypeEnums() {
  return requestClient.get<ConfigEnumItem[]>(`${BASE_URL}/enums/valueTypes`);
}

// ==================== 导入导出接口 ====================

/**
 * 导出配置列表
 */
export async function exportConfigList(params?: SysConfigApi.QueryParams) {
  return requestClient.post(`${BASE_URL}/export`, params, {
    responseType: 'blob',
  });
}

/**
 * 导入配置
 */
export async function importConfigData(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return requestClient.post<string>(`${BASE_URL}/import`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
}

/**
 * 下载配置导入模板
 */
export async function downloadConfigTemplate() {
  return requestClient.get(`${BASE_URL}/template`, {
    responseType: 'blob',
  });
}
