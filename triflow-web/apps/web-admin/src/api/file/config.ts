import type { PageResult } from '../system/types';
import type { FileConfigApi } from './types';

/**
 * 文件配置 API
 * @description 文件存储配置的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/file/config';

/**
 * 创建文件配置
 */
export async function createFileConfig(data: FileConfigApi.CreateParams) {
  return requestClient.post(`${BASE_URL}/create`, data);
}

/**
 * 更新文件配置
 */
export async function updateFileConfig(data: FileConfigApi.UpdateParams) {
  return requestClient.put(`${BASE_URL}/update`, data);
}

/**
 * 获取文件配置详情
 */
export async function getFileConfigById(id: number) {
  return requestClient.get<FileConfigApi.ConfigVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 删除文件配置
 */
export async function deleteFileConfig(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除文件配置
 */
export async function deleteFileConfigBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 查询文件配置列表
 */
export async function getFileConfigList(params?: FileConfigApi.QueryParams) {
  return requestClient.post<FileConfigApi.ConfigVO[]>(
    `${BASE_URL}/list`,
    params,
  );
}

/**
 * 分页查询文件配置
 */
export async function getFileConfigPage(params?: FileConfigApi.QueryParams) {
  return requestClient.post<PageResult<FileConfigApi.ConfigVO>>(
    `${BASE_URL}/page`,
    params,
  );
}

/**
 * 设置默认配置
 */
export async function setDefaultFileConfig(id: number) {
  return requestClient.put(`${BASE_URL}/setDefault`, null, {
    params: { id },
  });
}

/**
 * 获取默认配置
 */
export async function getDefaultFileConfig() {
  return requestClient.get<FileConfigApi.ConfigVO>(`${BASE_URL}/getDefault`);
}

/**
 * 测试配置连接
 */
export async function testFileConfig(id: number) {
  return requestClient.post<boolean>(`${BASE_URL}/testConnection`, null, {
    params: { id },
  });
}

/**
 * 获取所有启用的配置
 */
export async function getAllEnabledFileConfigs() {
  return requestClient.get<FileConfigApi.ConfigVO[]>(`${BASE_URL}/listAll`);
}

/** OSS配置模板类型 */
export interface OssTemplate {
  provider: string;
  endpoint: string;
  accessKey: string;
  secretKey: string;
  bucketName: string;
  domain: string;
  scheme: string;
  storageType: string;
}

/**
 * 获取OSS配置模板
 * @description 从配置文件读取默认OSS配置（凭证脱敏显示）
 */
export async function getOssTemplate() {
  return requestClient.get<OssTemplate>(`${BASE_URL}/getOssTemplate`);
}
