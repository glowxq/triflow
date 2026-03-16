/**
 * 缓存管理工具
 * @description 管理前端 preferences 缓存的版本控制
 */

const PREFERENCES_VERSION_KEY = 'triflow-preferences-version';

/**
 * 检查并清除过期的 preferences 缓存
 * @param serverVersion 服务端返回的 preferences 版本号
 * @returns true 表示缓存已被清除，需要重新加载
 */
export function checkAndClearPreferencesCache(serverVersion: string): boolean {
  const cachedVersion = localStorage.getItem(PREFERENCES_VERSION_KEY);

  if (cachedVersion !== serverVersion) {
    // 清除所有 triflow 相关的 preferences 缓存
    clearAllPreferencesCache();

    // 更新版本号
    localStorage.setItem(PREFERENCES_VERSION_KEY, serverVersion);

    return true;
  }

  return false;
}

/**
 * 清除所有 preferences 缓存
 */
export function clearAllPreferencesCache(): void {
  const keysToRemove: string[] = [];

  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (key && key.includes('triflow') && key.endsWith('-preferences')) {
      keysToRemove.push(key);
    }
  }

  keysToRemove.forEach((key) => {
    localStorage.removeItem(key);
  });
}

/**
 * 获取当前缓存的 preferences 版本号
 */
export function getCachedPreferencesVersion(): null | string {
  return localStorage.getItem(PREFERENCES_VERSION_KEY);
}
