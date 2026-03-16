/**
 * 文件管理模块类型定义
 * @description 文件上传相关的 TypeScript 接口
 */

// ==================== 文件信息相关类型 ====================

/** 文件信息 VO */
export interface FileInfoVO {
  /** 文件ID */
  id: number
  /** 配置ID */
  configId?: number
  /** 文件名（存储名） */
  fileName: string
  /** 原始文件名 */
  originalName: string
  /** 文件路径 */
  filePath: string
  /** 文件URL */
  fileUrl: string
  /** 预览URL（预签名，可直接访问） */
  previewUrl?: string
  /** 访问URL（无签名参数） */
  accessUrl?: string
  /** 文件大小（字节） */
  fileSize: number
  /** 文件类型 */
  fileType?: string
  /** 文件扩展名 */
  fileExt?: string
  /** 存储类型 */
  storageType?: string
  /** 存储桶名称 */
  bucketName?: string
  /** 对象键 */
  objectKey?: string
  /** 分类 */
  category?: string
  /** 业务类型 */
  bizType?: string
  /** 业务ID */
  bizId?: number
  /** 上传者ID */
  uploaderId?: number
  /** 上传者名称 */
  uploaderName?: string
  /** 上传时间 */
  uploadTime?: string
  /** 是否公开 */
  publicAccess?: number
  /** 下载次数 */
  downloadCount?: number
  /** 状态（0:禁用, 1:正常） */
  status: number
  /** 创建时间 */
  createTime: string
}

/** 预签名请求参数 */
export interface PresignRequestDTO {
  /** 原始文件名 */
  originalName: string
  /** 文件 MIME 类型 */
  contentType: string
  /** 文件大小（字节） */
  fileSize: number
  /** 业务类型（可选） */
  bizType?: string
  /** 业务ID（可选） */
  bizId?: number
  /** 存储配置ID（可选） */
  configId?: number
}

/** 预签名上传结果 */
export interface PresignedUploadResult {
  /** 预签名上传 URL */
  uploadUrl: string
  /** 对象键（存储路径） */
  objectKey: string
  /** 文件访问 URL */
  accessUrl: string
  /** 存储桶名称 */
  bucketName: string
  /** 有效期（秒） */
  expiresInSeconds: number
}

/** 确认上传参数 */
export interface ConfirmUploadDTO {
  /** OSS 对象键 */
  objectKey: string
  /** 原始文件名 */
  originalName: string
  /** 文件大小（字节） */
  fileSize: number
  /** 文件 MIME 类型 */
  contentType: string
  /** 文件访问 URL */
  accessUrl: string
  /** 存储桶名称 */
  bucketName: string
  /** 业务类型（可选） */
  bizType?: string
  /** 业务ID（可选） */
  bizId?: number
  /** 存储配置ID（可选） */
  configId?: number
}

/** 直传上传选项 */
export interface DirectUploadOptions {
  /** 业务类型 */
  bizType?: string
  /** 业务ID */
  bizId?: number
  /** 存储配置ID */
  configId?: number
  /** 上传进度回调 */
  onProgress?: (progress: number) => void
  /** 是否使用公开API（不需要登录，仅限头像等特定场景） */
  usePublicApi?: boolean
}

/** UniApp 选择文件结果 */
export interface UniChooseFileResult {
  /** 本地临时文件路径 */
  path: string
  /** 文件名 */
  name?: string
  /** 文件大小 */
  size: number
  /** 文件类型 */
  type?: string
}
