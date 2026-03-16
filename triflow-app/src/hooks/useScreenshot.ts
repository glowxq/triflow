/**
 * 截图监听 Hook
 *
 * 实现原理：通过轮询 MediaStore 检测新增的截图文件
 * - ContentObserver 是抽象类，plus.android.implements 只能实现接口，因此采用轮询方案
 * - 后台每 2 秒查询一次 MediaStore，检测新截图
 * - 切回前台时（onShow）也会检查一次
 *
 * 仅支持 Android App 环境
 */
import { readonly, ref } from 'vue'

export interface ScreenshotInfo {
  path: string
  timestamp: number
}

// 全局状态（跨组件共享）
const latestScreenshot = ref<ScreenshotInfo | null>(null)
const isListening = ref(false)

// 轮询 timer
let pollTimer: ReturnType<typeof setInterval> | null = null

// 记录监听开始时间（秒级，与 MediaStore.DATE_ADDED 对齐）
let listenStartTimeSec = 0
// 记录上次检测到的截图时间戳，避免重复触发
let lastDetectedTimeSec = 0

/**
 * 判断文件路径是否为截图
 */
function isScreenshotPath(filePath: string): boolean {
  if (!filePath)
    return false
  const lower = filePath.toLowerCase()
  return lower.includes('screenshot')
    || lower.includes('screen_shot')
    || lower.includes('screen capture')
    || lower.includes('截屏')
    || lower.includes('截图')
}

/**
 * 发送调试日志（通过事件总线）
 */
function emitDebug(msg: string): void {
  uni.$emit('screenshot-debug', msg)
}

/**
 * 从 MediaStore 查询最近的截图
 */
function queryLatestScreenshot(): ScreenshotInfo | null {
  // #ifdef APP-PLUS
  try {
    const main = plus.android.runtimeMainActivity()
    if (!main) {
      emitDebug('查询失败: main 为空')
      return null
    }

    // 通过 Uri.parse 获取外部图片的 content URI（最可靠的方式）
    const Uri = plus.android.importClass('android.net.Uri')
    const externalUri = Uri.parse('content://media/external/images/media')

    const contentResolver = main.getContentResolver()
    if (!contentResolver || !externalUri) {
      emitDebug('查询失败: contentResolver 或 URI 为空')
      return null
    }

    // 导入 ContentResolver 类以确保方法可用
    plus.android.importClass(contentResolver)

    // 使用列名字符串（避免依赖 MediaStore 静态常量的复杂访问）
    const cursor = contentResolver.query(
      externalUri,
      ['_data', 'date_added'],
      null,
      null,
      'date_added DESC',
    )

    if (!cursor) {
      emitDebug('查询失败: cursor 为空')
      return null
    }

    // 导入 Cursor 类以确保方法可用
    plus.android.importClass(cursor)

    let result: ScreenshotInfo | null = null
    let count = 0
    const debugPaths: string[] = []

    if (cursor.moveToFirst()) {
      do {
        if (count >= 10)
          break

        const filePath = cursor.getString(0)
        const dateAdded = cursor.getLong(1)

        // 记录前3条用于调试
        if (count < 3) {
          debugPaths.push(`${filePath?.split('/').pop() || 'null'}@${dateAdded}`)
        }

        if (filePath && isScreenshotPath(filePath)) {
          result = {
            path: filePath,
            timestamp: dateAdded,
          }
          break
        }

        count++
      } while (cursor.moveToNext())
    }

    cursor.close()

    // 发送调试信息
    if (debugPaths.length > 0) {
      emitDebug(`最近图片: ${debugPaths.join(' | ')}`)
    }

    return result
  }
  catch (e: any) {
    emitDebug(`查询异常: ${e?.message || e}`)
    return null
  }
  // #endif

  return null
}

/**
 * 轮询检测新截图
 */
function pollForScreenshot(): void {
  const screenshot = queryLatestScreenshot()

  if (!screenshot) {
    // 没找到截图（可能路径不匹配）
    return
  }

  // 调试：显示时间戳比较
  emitDebug(`截图ts=${screenshot.timestamp} 启动ts=${listenStartTimeSec} 上次ts=${lastDetectedTimeSec}`)

  // 只处理监听开始之后、且未曾处理过的截图
  if (screenshot.timestamp > listenStartTimeSec && screenshot.timestamp > lastDetectedTimeSec) {
    lastDetectedTimeSec = screenshot.timestamp
    latestScreenshot.value = screenshot

    emitDebug(`✓ 新截图! ${screenshot.path.split('/').pop()}`)

    // 通过事件总线通知所有监听者
    uni.$emit('screenshot-detected', screenshot)
  }
}

/**
 * 请求存储权限
 */
function requestStoragePermission(): Promise<boolean> {
  // #ifdef APP-PLUS
  return new Promise((resolve) => {
    plus.android.requestPermissions(
      [
        'android.permission.READ_EXTERNAL_STORAGE',
        'android.permission.READ_MEDIA_IMAGES',
      ],
      (result: any) => {
        const granted = result.granted && result.granted.length > 0
        resolve(granted)
      },
      (_error: any) => {
        resolve(false)
      },
    )
  })
  // #endif

  // #ifndef APP-PLUS
  return Promise.resolve(false)
  // #endif
}

/**
 * 启动截图监听
 */
async function startListening(): Promise<boolean> {
  // #ifdef APP-PLUS
  if (uni.getSystemInfoSync().platform !== 'android') {
    emitDebug('非 Android 平台，跳过')
    return false
  }

  if (isListening.value) {
    emitDebug('监听已在运行')
    return true
  }

  // 请求权限
  emitDebug('正在请求存储权限...')
  const hasPermission = await requestStoragePermission()
  if (!hasPermission) {
    emitDebug('✗ 权限被拒绝')
    return false
  }
  emitDebug('✓ 权限已获取')

  // 记录监听开始时间
  listenStartTimeSec = Math.floor(Date.now() / 1000)
  emitDebug(`启动时间戳: ${listenStartTimeSec}`)

  // 初始化：获取当前最新截图的时间，避免误触发
  const current = queryLatestScreenshot()
  if (current) {
    lastDetectedTimeSec = current.timestamp
    emitDebug(`初始截图: ${current.path.split('/').pop()} @ ${current.timestamp}`)
  }
  else {
    emitDebug('初始化: 未找到已有截图')
  }

  // 启动轮询（每 2 秒检查一次）
  pollTimer = setInterval(pollForScreenshot, 2000)

  isListening.value = true
  emitDebug('✓ 轮询已启动 (2秒/次)')
  return true
  // #endif

  // #ifndef APP-PLUS
  return false
  // #endif
}

/**
 * 停止截图监听
 */
function stopListening(): void {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  isListening.value = false
}

/**
 * 手动检查最新截图（用于 onShow 场景）
 */
function checkForNewScreenshot(): ScreenshotInfo | null {
  if (!isListening.value)
    return null

  const screenshot = queryLatestScreenshot()
  if (!screenshot)
    return null

  if (screenshot.timestamp > listenStartTimeSec && screenshot.timestamp > lastDetectedTimeSec) {
    lastDetectedTimeSec = screenshot.timestamp
    latestScreenshot.value = screenshot
    return screenshot
  }

  return null
}

/**
 * 清除最新截图记录
 */
function clearLatestScreenshot(): void {
  latestScreenshot.value = null
}

export function useScreenshot() {
  return {
    latestScreenshot: readonly(latestScreenshot),
    isListening: readonly(isListening),
    startListening,
    stopListening,
    checkForNewScreenshot,
    clearLatestScreenshot,
  }
}
