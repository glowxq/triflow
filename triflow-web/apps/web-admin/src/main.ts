import { initPreferences } from '@vben/preferences';
import { unmountGlobalLoading } from '@vben/utils';

import { getConfigVersion } from '#/api/system/config';
import { checkAndClearPreferencesCache } from '#/utils/cache-manager';

import { overridesPreferences } from './preferences';

/**
 * 清除旧的偏好设置缓存（当 logo 配置变更时需要）
 * StorageManager 存储格式为 { value: Preferences, expiry?: number }
 */
function clearStalePreferencesCache(namespace: string) {
  // 清除当前 namespace 的缓存
  const cacheKey = `${namespace}-preferences`;
  clearSinglePreferencesCache(cacheKey);

  // 同时扫描所有 triflow-admin 开头的 preferences 缓存（兼容不同版本）
  const keysToCheck: string[] = [];
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (
      key &&
      key.startsWith('triflow-admin') &&
      key.endsWith('-preferences')
    ) {
      keysToCheck.push(key);
    }
  }
  for (const key of keysToCheck) {
    clearSinglePreferencesCache(key);
  }
}

/**
 * 清除单个缓存项（如果包含旧的 logo URL）
 */
function clearSinglePreferencesCache(cacheKey: string) {
  const cached = localStorage.getItem(cacheKey);
  if (cached) {
    try {
      const parsed = JSON.parse(cached);
      // StorageManager 格式：{ value: { logo: { source: '...' } } }
      const logoSource = parsed?.value?.logo?.source;
      // 如果缓存的 logo 配置是旧的外部 URL，清除缓存
      if (logoSource && logoSource.includes('unpkg.com')) {
        localStorage.removeItem(cacheKey);
      }
    } catch {
      // 忽略解析错误
    }
  }
}

/**
 * 应用初始化完成之后再进行页面加载渲染
 */
async function initApplication() {
  // name用于指定项目唯一标识
  // 用于区分不同项目的偏好设置以及存储数据的key前缀以及其他一些需要隔离的数据
  const env = import.meta.env.PROD ? 'prod' : 'dev';
  const appVersion = import.meta.env.VITE_APP_VERSION;
  const namespace = `${import.meta.env.VITE_APP_NAMESPACE}-${appVersion}-${env}`;

  // 清除旧的 logo 缓存配置
  clearStalePreferencesCache(namespace);

  // 检查服务端配置版本，决定是否清除缓存
  try {
    const configVersion = await getConfigVersion();
    if (configVersion?.preferencesVersion) {
      checkAndClearPreferencesCache(configVersion.preferencesVersion);
    }
  } catch (error) {
    console.warn('[Triflow] 获取配置版本失败，跳过缓存检查:', error);
  }

  // app偏好设置初始化
  await initPreferences({
    namespace,
    overrides: overridesPreferences,
  });

  // 启动应用并挂载
  // vue应用主要逻辑及视图
  const { bootstrap } = await import('./bootstrap');
  await bootstrap(namespace);

  // 移除并销毁loading
  unmountGlobalLoading();
}

initApplication();
