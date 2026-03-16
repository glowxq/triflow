import type { PageResult } from '../system/types';
import type { FileInfoApi } from './types';

/**
 * 文件信息 API
 * @description 文件预签名上传、查询等接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/file/info';

// ==================== MIME 类型工具 ====================

/**
 * 扩展名到 MIME 类型的映射
 * 用于处理浏览器无法识别的文件类型
 */
const MIME_TYPE_MAP: Record<string, string> = {
  // 文档类型
  md: 'text/markdown',
  markdown: 'text/markdown',
  txt: 'text/plain',
  json: 'application/json',
  xml: 'application/xml',
  yaml: 'application/x-yaml',
  yml: 'application/x-yaml',
  csv: 'text/csv',
  // 代码文件
  js: 'application/javascript',
  ts: 'application/typescript',
  jsx: 'text/jsx',
  tsx: 'text/tsx',
  vue: 'text/x-vue',
  css: 'text/css',
  scss: 'text/x-scss',
  less: 'text/x-less',
  html: 'text/html',
  // 图片
  svg: 'image/svg+xml',
  ico: 'image/x-icon',
  webp: 'image/webp',
  // 压缩包
  gz: 'application/gzip',
  tar: 'application/x-tar',
  '7z': 'application/x-7z-compressed',
  // 其他
  woff: 'font/woff',
  woff2: 'font/woff2',
  ttf: 'font/ttf',
  eot: 'application/vnd.ms-fontobject',
};

/**
 * 获取文件的 MIME 类型
 * @description 优先使用浏览器识别的类型，否则根据扩展名推断
 */
export function getFileMimeType(file: File): string {
  // 浏览器已识别类型
  if (file.type && file.type !== '') {
    return file.type;
  }

  // 根据扩展名推断
  const ext = file.name.split('.').pop()?.toLowerCase();
  if (ext && MIME_TYPE_MAP[ext]) {
    return MIME_TYPE_MAP[ext];
  }

  // 默认类型
  return 'application/octet-stream';
}

// ==================== 预签名上传接口 ====================

/**
 * 获取预签名上传URL
 * @description 用于前端直传 OSS
 */
export async function getPresignedUrl(params: FileInfoApi.PresignRequest) {
  return requestClient.post<FileInfoApi.PresignedUploadResult>(
    `${BASE_URL}/presign`,
    params,
  );
}

/**
 * 确认上传完成
 * @description 前端直传成功后，保存文件记录到数据库
 */
export async function confirmUpload(params: FileInfoApi.ConfirmUploadParams) {
  return requestClient.post<FileInfoApi.FileVO>(
    `${BASE_URL}/confirmUpload`,
    params,
  );
}

/**
 * 直传文件到 OSS
 * @description 使用 XHR 发送 PUT 请求到预签名 URL
 * @param uploadUrl 预签名上传URL
 * @param file 文件对象
 * @param contentType MIME类型（必须与预签名时一致）
 * @param onProgress 进度回调
 */
export async function uploadToOss(
  uploadUrl: string,
  file: File,
  contentType: string,
  onProgress?: (progress: number) => void,
): Promise<Response> {
  return new Promise((resolve, reject) => {
    const xhr = new XMLHttpRequest();

    xhr.open('PUT', uploadUrl, true);
    // 使用传入的 contentType，确保与预签名时一致
    xhr.setRequestHeader('Content-Type', contentType);

    // 上传进度
    if (onProgress) {
      xhr.upload.addEventListener('progress', (event) => {
        if (event.lengthComputable) {
          const progress = Math.round((event.loaded / event.total) * 100);
          onProgress(progress);
        }
      });
    }

    xhr.addEventListener('load', () => {
      if (xhr.status >= 200 && xhr.status < 300) {
        resolve(new Response(xhr.responseText, { status: xhr.status }));
      } else {
        reject(new Error(`上传失败: ${xhr.status} ${xhr.statusText}`));
      }
    });

    xhr.addEventListener('error', () => {
      reject(new Error('网络错误'));
    });

    xhr.send(file);
  });
}

/**
 * 直传上传文件（封装完整流程）
 * @description 获取预签名URL -> 直传OSS -> 确认上传
 */
export async function directUpload(
  file: File,
  options?: {
    bizId?: number;
    bizType?: string;
    configId?: number;
    onProgress?: (progress: number) => void;
  },
): Promise<FileInfoApi.FileVO> {
  // 统一获取 MIME 类型，确保预签名和上传使用相同的类型
  const contentType = getFileMimeType(file);

  // 1. 获取预签名 URL
  const presigned = await getPresignedUrl({
    originalName: file.name,
    contentType,
    fileSize: file.size,
    bizType: options?.bizType,
    bizId: options?.bizId,
    configId: options?.configId,
  });

  // 2. 直传到 OSS（使用相同的 contentType）
  const response = await uploadToOss(
    presigned.uploadUrl,
    file,
    contentType,
    options?.onProgress,
  );

  if (!response.ok) {
    throw new Error('文件上传到OSS失败');
  }

  // 3. 确认上传完成
  return confirmUpload({
    objectKey: presigned.objectKey,
    originalName: file.name,
    fileSize: file.size,
    contentType,
    accessUrl: presigned.accessUrl,
    bucketName: presigned.bucketName,
    bizType: options?.bizType,
    bizId: options?.bizId,
    configId: options?.configId,
  });
}

/**
 * 批量直传上传文件
 */
export async function directUploadBatch(
  files: File[],
  options?: {
    bizId?: number;
    bizType?: string;
    configId?: number;
    onProgress?: (index: number, progress: number) => void;
  },
): Promise<FileInfoApi.FileVO[]> {
  const results: FileInfoApi.FileVO[] = [];
  let index = 0;
  for (const file of files) {
    const currentIndex = index;
    const result = await directUpload(file, {
      ...options,
      onProgress: options?.onProgress
        ? (progress) => options.onProgress?.(currentIndex, progress)
        : undefined,
    });
    results.push(result);
    index++;
  }
  return results;
}

// ==================== 查询接口 ====================

/**
 * 获取文件详情
 */
export async function getFileById(id: number) {
  return requestClient.get<FileInfoApi.FileVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 删除文件
 */
export async function deleteFile(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除文件
 */
export async function deleteFileBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 物理删除文件（同时删除OSS文件）
 */
export async function physicalDeleteFile(id: number) {
  return requestClient.delete(`${BASE_URL}/physicalDelete`, {
    params: { id },
  });
}

/**
 * 批量物理删除文件（同时删除OSS文件）
 */
export async function physicalDeleteFileBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/physicalDeleteBatch`, {
    data: ids,
  });
}

/**
 * 查询文件列表
 */
export async function getFileList(params?: FileInfoApi.QueryParams) {
  return requestClient.post<FileInfoApi.FileVO[]>(`${BASE_URL}/list`, params);
}

/**
 * 分页查询文件
 */
export async function getFilePage(params?: FileInfoApi.QueryParams) {
  return requestClient.post<PageResult<FileInfoApi.FileVO>>(
    `${BASE_URL}/page`,
    params,
  );
}

/**
 * 根据业务查询文件
 */
export async function getFilesByBiz(bizType: string, bizId: number) {
  return requestClient.get<FileInfoApi.FileVO[]>(`${BASE_URL}/listByBiz`, {
    params: { bizType, bizId },
  });
}

/**
 * 根据分类查询文件
 */
export async function getFilesByCategory(category: string) {
  return requestClient.get<FileInfoApi.FileVO[]>(`${BASE_URL}/listByCategory`, {
    params: { category },
  });
}

/**
 * 获取文件预览/下载URL
 * @description 对于私有存储，返回预签名下载URL
 */
export async function getPreviewUrl(id: number) {
  return requestClient.get<string>(`${BASE_URL}/previewUrl`, {
    params: { id },
  });
}

// ==================== 导出接口 ====================

/**
 * 导出文件列表
 */
export async function exportFileList(params?: FileInfoApi.QueryParams) {
  return requestClient.post(`${BASE_URL}/export`, params, {
    responseType: 'blob',
  });
}
