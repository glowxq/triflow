/**
 * 文件管理模块 API 导出
 */

// 文件配置 API
export {
  createFileConfig,
  deleteFileConfig,
  deleteFileConfigBatch,
  getAllEnabledFileConfigs,
  getDefaultFileConfig,
  getFileConfigById,
  getFileConfigList,
  getFileConfigPage,
  getOssTemplate,
  setDefaultFileConfig,
  testFileConfig,
  updateFileConfig,
} from './config';

// 文件配置类型导出
export type { OssTemplate } from './config';

// 文件信息 API
export {
  confirmUpload,
  deleteFile,
  deleteFileBatch,
  directUpload,
  directUploadBatch,
  exportFileList,
  getFileById,
  getFileList,
  getFilePage,
  getFilesByBiz,
  getFilesByCategory,
  getPresignedUrl,
  getPreviewUrl,
  physicalDeleteFile,
  physicalDeleteFileBatch,
  uploadToOss,
} from './info';

// 类型导出
export type { FileConfigApi, FileInfoApi } from './types';
