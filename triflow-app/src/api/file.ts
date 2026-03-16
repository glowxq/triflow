/**
 * 文件上传 API
 * @description 支持 OSS 前端直传（预签名 → 直传 → 确认）
 */

import type {
  ConfirmUploadDTO,
  DirectUploadOptions,
  FileInfoVO,
  PresignedUploadResult,
  PresignRequestDTO,
  UniChooseFileResult,
} from './types/file'
import { http } from '@/http/http'

const BASE_URL = '/base/admin/file/info'
const PUBLIC_URL = '/base/public'

// ==================== MIME 类型工具 ====================

/**
 * 扩展名到 MIME 类型的映射
 * 用于处理无法自动识别的文件类型
 */
const MIME_TYPE_MAP: Record<string, string> = {
  // 图片
  'jpg': 'image/jpeg',
  'jpeg': 'image/jpeg',
  'png': 'image/png',
  'gif': 'image/gif',
  'webp': 'image/webp',
  'bmp': 'image/bmp',
  'svg': 'image/svg+xml',
  'ico': 'image/x-icon',
  // 文档
  'pdf': 'application/pdf',
  'doc': 'application/msword',
  'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
  'xls': 'application/vnd.ms-excel',
  'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  'ppt': 'application/vnd.ms-powerpoint',
  'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
  'txt': 'text/plain',
  'md': 'text/markdown',
  'json': 'application/json',
  'xml': 'application/xml',
  'csv': 'text/csv',
  // 视频
  'mp4': 'video/mp4',
  'webm': 'video/webm',
  'mov': 'video/quicktime',
  'avi': 'video/x-msvideo',
  // 音频
  'mp3': 'audio/mpeg',
  'wav': 'audio/wav',
  'ogg': 'audio/ogg',
  // 压缩包
  'zip': 'application/zip',
  'rar': 'application/x-rar-compressed',
  '7z': 'application/x-7z-compressed',
  'tar': 'application/x-tar',
  'gz': 'application/gzip',
}

/**
 * 根据文件名获取 MIME 类型
 */
export function getMimeType(fileName: string, fallbackType?: string): string {
  const ext = fileName.split('.').pop()?.toLowerCase()
  if (ext && MIME_TYPE_MAP[ext]) {
    return MIME_TYPE_MAP[ext]
  }
  return fallbackType || 'application/octet-stream'
}

// ==================== 预签名上传接口 ====================

/**
 * 获取预签名上传URL
 * @description 用于前端直传 OSS（需要登录）
 */
export function getPresignedUrl(params: PresignRequestDTO) {
  return http.post<PresignedUploadResult>(`${BASE_URL}/presign`, params)
}

/**
 * 获取公开预签名上传URL
 * @description 用于未登录时的文件上传（仅限头像等特定场景）
 */
export function getPublicPresignedUrl(params: PresignRequestDTO) {
  return http.post<PresignedUploadResult>(`${PUBLIC_URL}/upload/presign`, params)
}

/**
 * 确认上传完成
 * @description 前端直传成功后，保存文件记录到数据库
 */
export function confirmUpload(params: ConfirmUploadDTO) {
  return http.post<FileInfoVO>(`${BASE_URL}/confirmUpload`, params)
}

/**
 * 直传文件到 OSS
 * @description 使用 uni.request 发送 PUT 请求到预签名 URL
 * @param uploadUrl 预签名上传URL
 * @param filePath 本地文件路径
 * @param contentType MIME类型
 * @param onProgress 进度回调
 */
export function uploadToOss(
  uploadUrl: string,
  filePath: string,
  contentType: string,
  onProgress?: (progress: number) => void,
): Promise<void> {
  return new Promise((resolve, reject) => {
    // #ifdef MP-WEIXIN
    // 微信小程序：必须使用 PUT 方法上传到 OSS 预签名 URL
    // uni.uploadFile 默认使用 POST，不支持 PUT，所以需要先读取文件再用 request 发送
    const fs = uni.getFileSystemManager()
    fs.readFile({
      filePath,
      success: (readRes) => {
        // 通知开始上传（小程序 request 不支持真实进度，只能模拟）
        onProgress?.(10)

        uni.request({
          url: uploadUrl,
          method: 'PUT',
          header: {
            'Content-Type': contentType,
          },
          data: readRes.data,
          success: (res) => {
            if (res.statusCode >= 200 && res.statusCode < 300) {
              onProgress?.(100)
              resolve()
            }
            else {
              reject(new Error(`上传失败: ${res.statusCode} - ${JSON.stringify(res.data)}`))
            }
          },
          fail: (err) => {
            reject(new Error(err.errMsg || '上传失败'))
          },
        })
      },
      fail: (err) => {
        reject(new Error(`读取文件失败: ${err.errMsg}`))
      },
    })
    // #endif

    // #ifndef MP-WEIXIN
    // H5 / App：使用 XMLHttpRequest 发送 PUT 请求
    // 先读取文件内容
    // #ifdef H5
    fetch(filePath)
      .then(response => response.blob())
      .then((blob) => {
        const xhr = new XMLHttpRequest()
        xhr.open('PUT', uploadUrl, true)
        xhr.setRequestHeader('Content-Type', contentType)

        if (onProgress) {
          xhr.upload.addEventListener('progress', (event) => {
            if (event.lengthComputable) {
              const progress = Math.round((event.loaded / event.total) * 100)
              onProgress(progress)
            }
          })
        }

        xhr.addEventListener('load', () => {
          if (xhr.status >= 200 && xhr.status < 300) {
            resolve()
          }
          else {
            reject(new Error(`上传失败: ${xhr.status}`))
          }
        })

        xhr.addEventListener('error', () => {
          reject(new Error('网络错误'))
        })

        xhr.send(blob)
      })
      .catch(reject)
    // #endif

    // #ifdef APP-PLUS
    // App 端使用 plus.io 读取文件
    plus.io.resolveLocalFileSystemURL(
      filePath,
      (entry: any) => {
        entry.file((file: any) => {
          const reader = new plus.io.FileReader()
          reader.onloadend = (e: any) => {
            const arrayBuffer = e.target.result
            const xhr = new XMLHttpRequest()
            xhr.open('PUT', uploadUrl, true)
            xhr.setRequestHeader('Content-Type', contentType)

            if (onProgress) {
              xhr.upload.addEventListener('progress', (event) => {
                if (event.lengthComputable) {
                  const progress = Math.round((event.loaded / event.total) * 100)
                  onProgress(progress)
                }
              })
            }

            xhr.addEventListener('load', () => {
              if (xhr.status >= 200 && xhr.status < 300) {
                resolve()
              }
              else {
                reject(new Error(`上传失败: ${xhr.status}`))
              }
            })

            xhr.addEventListener('error', () => {
              reject(new Error('网络错误'))
            })

            xhr.send(arrayBuffer)
          }
          reader.readAsArrayBuffer(file)
        })
      },
      () => {
        reject(new Error('读取文件失败'))
      },
    )
    // #endif
    // #endif
  })
}

/**
 * 直传上传文件（封装完整流程）
 * @description 获取预签名URL -> 直传OSS -> 确认上传
 * @param file 文件信息 { path, name, size, type }
 * @param options 上传选项
 */
export async function directUpload(
  file: UniChooseFileResult,
  options?: DirectUploadOptions,
): Promise<FileInfoVO> {
  const fileName = file.name || file.path.split('/').pop() || 'unknown'
  const contentType = getMimeType(fileName, file.type)

  const presignParams = {
    originalName: fileName,
    contentType,
    fileSize: file.size,
    bizType: options?.bizType,
    bizId: options?.bizId,
    configId: options?.configId,
  }

  // 1. 获取预签名 URL（根据选项决定使用公开接口还是需要认证的接口）
  const presigned = options?.usePublicApi
    ? await getPublicPresignedUrl(presignParams)
    : await getPresignedUrl(presignParams)

  // 2. 直传到 OSS
  await uploadToOss(
    presigned.uploadUrl,
    file.path,
    contentType,
    options?.onProgress,
  )

  // 3. 确认上传完成（公开上传时跳过确认步骤，直接返回基本信息）
  if (options?.usePublicApi) {
    // 公开上传时返回基本信息（不调用确认接口，因为用户可能还没登录）
    return {
      id: 0,
      fileName,
      originalName: fileName,
      filePath: presigned.objectKey,
      fileUrl: presigned.accessUrl,
      previewUrl: presigned.accessUrl,
      accessUrl: presigned.accessUrl,
      fileSize: file.size,
      fileType: contentType,
      status: 1,
      createTime: new Date().toISOString(),
    }
  }

  return confirmUpload({
    objectKey: presigned.objectKey,
    originalName: fileName,
    fileSize: file.size,
    contentType,
    accessUrl: presigned.accessUrl,
    bucketName: presigned.bucketName,
    bizType: options?.bizType,
    bizId: options?.bizId,
    configId: options?.configId,
  })
}

// ==================== 文件查询接口 ====================

/**
 * 获取文件详情
 */
export function getFileById(id: number) {
  return http.get<FileInfoVO>(`${BASE_URL}/detail`, { id })
}

/**
 * 删除文件
 */
export function deleteFile(id: number) {
  return http.delete(`${BASE_URL}/delete`, { id })
}

/**
 * 获取文件预览URL
 * @description 对于私有存储，返回预签名下载URL
 */
export function getPreviewUrl(id: number) {
  return http.get<string>(`${BASE_URL}/previewUrl`, { id })
}

// ==================== 选择文件工具函数 ====================

/**
 * 选择图片
 * @param count 最多可选择的图片数量
 * @param sizeType 图片尺寸类型
 * @param sourceType 图片来源
 */
export function chooseImage(
  count = 1,
  sizeType: ('original' | 'compressed')[] = ['compressed'],
  sourceType: ('album' | 'camera')[] = ['album', 'camera'],
): Promise<UniChooseFileResult[]> {
  return new Promise((resolve, reject) => {
    uni.chooseImage({
      count,
      sizeType,
      sourceType,
      success: (res) => {
        const files: UniChooseFileResult[] = res.tempFiles.map((file: any) => ({
          path: file.path,
          name: file.name || file.path.split('/').pop(),
          size: file.size,
          type: file.type,
        }))
        resolve(files)
      },
      fail: (err) => {
        if (err.errMsg?.includes('cancel')) {
          // 用户取消选择，不视为错误
          resolve([])
        }
        else {
          reject(new Error(err.errMsg || '选择图片失败'))
        }
      },
    })
  })
}

/**
 * 选择并上传单张图片
 * @param options 上传选项
 */
export async function chooseAndUploadImage(
  options?: DirectUploadOptions & {
    sizeType?: ('original' | 'compressed')[]
    sourceType?: ('album' | 'camera')[]
    maxSize?: number // 最大文件大小 (MB)
  },
): Promise<FileInfoVO | null> {
  const files = await chooseImage(
    1,
    options?.sizeType || ['compressed'],
    options?.sourceType || ['album', 'camera'],
  )

  if (files.length === 0) {
    return null // 用户取消
  }

  const file = files[0]

  // 检查文件大小
  const maxSize = options?.maxSize || 10 // 默认 10MB
  if (file.size > maxSize * 1024 * 1024) {
    throw new Error(`图片大小不能超过 ${maxSize}MB`)
  }

  return directUpload(file, {
    bizType: options?.bizType || 'image',
    bizId: options?.bizId,
    configId: options?.configId,
    onProgress: options?.onProgress,
    usePublicApi: options?.usePublicApi,
  })
}

/**
 * 获取文件信息（大小等）
 * @param filePath 本地文件路径
 */
function getFileInfo(filePath: string): Promise<{ size: number }> {
  return new Promise((resolve) => {
    // #ifdef MP-WEIXIN
    // 小程序使用新版 FileSystemManager API
    const fs = uni.getFileSystemManager()
    fs.getFileInfo({
      filePath,
      success: (res) => {
        resolve({ size: res.size })
      },
      fail: (err) => {
        console.warn('获取文件信息失败:', err)
        resolve({ size: 1024 })
      },
    })
    // #endif
    // #ifndef MP-WEIXIN
    uni.getFileInfo({
      filePath,
      success: (res) => {
        resolve({ size: res.size })
      },
      fail: (err) => {
        console.warn('获取文件信息失败:', err)
        resolve({ size: 1024 })
      },
    })
    // #endif
  })
}

/**
 * 直接上传文件路径（不需要选择文件）
 * @param filePath 本地文件路径
 * @param fileName 文件名
 * @param options 上传选项
 * @description 用于上传微信头像等已知文件路径的场景
 */
export async function uploadImageFromPath(
  filePath: string,
  fileName: string = 'image.jpg',
  options?: DirectUploadOptions,
): Promise<FileInfoVO> {
  const contentType = getMimeType(fileName)

  // 获取文件实际大小
  const fileInfo = await getFileInfo(filePath)

  const file: UniChooseFileResult = {
    path: filePath,
    name: fileName,
    size: fileInfo.size,
    type: contentType,
  }

  return directUpload(file, {
    bizType: options?.bizType || 'avatar',
    bizId: options?.bizId,
    configId: options?.configId,
    onProgress: options?.onProgress,
    usePublicApi: options?.usePublicApi,
  })
}
