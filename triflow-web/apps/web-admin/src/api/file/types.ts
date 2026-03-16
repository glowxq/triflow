/**
 * 文件管理模块类型定义
 * @description 文件配置、文件信息相关的 TypeScript 接口
 */

import type { PageParams } from '../system/types';

// ==================== 文件配置相关类型 ====================

export namespace FileConfigApi {
  /** 文件配置列表项 */
  export interface ConfigVO {
    /** 配置ID */
    id: number;
    /** 配置名称 */
    configName: string;
    /** 配置标识 */
    configKey: string;
    /** 存储类型 */
    storageType: string;
    /** 是否默认 */
    isDefault: number;
    /** 存储桶名称 */
    bucketName?: string;
    /** 访问域名 */
    domain?: string;
    /** 基础路径 */
    basePath?: string;
    /** 端点地址 */
    endpoint?: string;
    /** Access Key */
    accessKey?: string;
    /** 区域 */
    region?: string;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime: string;
    /** 更新时间 */
    updateTime?: string;
  }

  /** 创建文件配置参数 */
  export interface CreateParams {
    /** 配置名称 */
    configName: string;
    /** 配置标识（唯一标识） */
    configKey: string;
    /** 存储类型 */
    storageType: string;
    /** 是否默认 */
    isDefault?: number;
    /** 存储桶名称 */
    bucketName?: string;
    /** 访问域名 */
    domain?: string;
    /** 基础路径 */
    basePath?: string;
    /** 端点地址 */
    endpoint?: string;
    /** Access Key */
    accessKey?: string;
    /** Secret Key */
    secretKey?: string;
    /** 区域 */
    region?: string;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新文件配置参数 */
  export interface UpdateParams {
    /** 配置ID */
    id: number;
    /** 配置名称 */
    configName?: string;
    /** 配置标识 */
    configKey?: string;
    /** 存储类型 */
    storageType?: string;
    /** 是否默认 */
    isDefault?: number;
    /** 存储桶名称 */
    bucketName?: string;
    /** 访问域名 */
    domain?: string;
    /** 基础路径 */
    basePath?: string;
    /** 端点地址 */
    endpoint?: string;
    /** Access Key */
    accessKey?: string;
    /** Secret Key */
    secretKey?: string;
    /** 区域 */
    region?: string;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 文件配置查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 存储类型 */
    storageType?: string;
    /** 状态筛选 */
    status?: number;
  }
}

// ==================== 文件信息相关类型 ====================

export namespace FileInfoApi {
  /** 文件信息列表项 */
  export interface FileVO {
    /** 文件ID */
    id: number;
    /** 配置ID */
    configId?: number;
    /** 文件名（存储名） */
    fileName: string;
    /** 原始文件名 */
    originalName: string;
    /** 文件路径 */
    filePath: string;
    /** 文件URL */
    fileUrl: string;
    /** 预览URL（预签名，可直接访问） */
    previewUrl?: string;
    /** 文件大小（字节） */
    fileSize: number;
    /** 文件类型 */
    fileType?: string;
    /** 文件扩展名 */
    fileExt?: string;
    /** 存储类型 */
    storageType?: string;
    /** 存储桶名称 */
    bucketName?: string;
    /** 对象键 */
    objectKey?: string;
    /** 分类 */
    category?: string;
    /** 业务类型 */
    bizType?: string;
    /** 业务ID */
    bizId?: number;
    /** 上传者ID */
    uploaderId?: number;
    /** 上传者名称 */
    uploaderName?: string;
    /** 上传时间 */
    uploadTime?: string;
    /** 是否公开 */
    publicAccess?: number;
    /** 下载次数 */
    downloadCount?: number;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 创建时间 */
    createTime: string;
  }

  /** 预签名请求参数 */
  export interface PresignRequest {
    /** 原始文件名 */
    originalName: string;
    /** 文件 MIME 类型 */
    contentType: string;
    /** 文件大小（字节） */
    fileSize: number;
    /** 业务类型（可选） */
    bizType?: string;
    /** 业务ID（可选） */
    bizId?: number;
    /** 存储配置ID（可选） */
    configId?: number;
  }

  /** 预签名上传结果 */
  export interface PresignedUploadResult {
    /** 预签名上传 URL */
    uploadUrl: string;
    /** 对象键（存储路径） */
    objectKey: string;
    /** 文件访问 URL */
    accessUrl: string;
    /** 存储桶名称 */
    bucketName: string;
    /** 有效期（秒） */
    expiresInSeconds: number;
  }

  /** 确认上传参数 */
  export interface ConfirmUploadParams {
    /** OSS 对象键 */
    objectKey: string;
    /** 原始文件名 */
    originalName: string;
    /** 文件大小（字节） */
    fileSize: number;
    /** 文件 MIME 类型 */
    contentType: string;
    /** 文件访问 URL */
    accessUrl: string;
    /** 存储桶名称 */
    bucketName: string;
    /** 业务类型（可选） */
    bizType?: string;
    /** 业务ID（可选） */
    bizId?: number;
    /** 存储配置ID（可选） */
    configId?: number;
  }

  /** 文件查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 文件分类 (image, video, audio, document, other) */
    category?: string;
    /** 文件扩展名 */
    fileExt?: string;
    /** 业务类型 */
    bizType?: string;
    /** 业务ID */
    bizId?: number;
    /** 上传者ID */
    uploaderId?: number;
    /** 状态筛选 */
    status?: number;
    /** 开始时间 */
    startTime?: string;
    /** 结束时间 */
    endTime?: string;
  }
}
