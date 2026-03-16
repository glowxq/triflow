/**
 * 系统开关 API
 * @description 公共开关配置接口，无需登录即可访问
 */
import { baseRequestClient } from '#/api/request';

const BASE_URL = '/base/public/switch';

/**
 * 系统开关枚举
 * @description 与后端 SwitchKeyEnum 保持一致
 */
export enum SwitchKey {
  /** 邮件通知开关 */
  NOTIFY_EMAIL = 'notify.email.enabled',
  // ========== 通知模块 ==========
  /** 短信通知开关 */
  NOTIFY_SMS = 'notify.sms.enabled',
  /** 微信通知开关 */
  NOTIFY_WECHAT = 'notify.wechat.enabled',
  // ========== 安全模块 ==========
  /** 验证码开关 */
  SECURITY_CAPTCHA = 'security.captcha.enabled',

  /** 接口限流开关 */
  SECURITY_RATE_LIMIT = 'security.rateLimit.enabled',
  /** 接口防重放开关 */
  SECURITY_REPLAY = 'security.replay.enabled',
  /** 敏感词过滤开关 */
  SECURITY_SENSITIVE_WORD = 'security.sensitiveWord.enabled',
  /** API文档开关 */
  SYSTEM_APIDOC = 'system.apidoc.enabled',

  /** 登录日志开关 */
  SYSTEM_LOGIN_LOG = 'system.loginLog.enabled',
  // ========== 系统模块 ==========
  /** 系统维护模式 */
  SYSTEM_MAINTENANCE = 'system.maintenance.enabled',
  /** 操作日志开关 */
  SYSTEM_OPERATE_LOG = 'system.operateLog.enabled',
  /** 手机号登录开关 */
  USER_LOGIN_PHONE = 'user.login.phone.enabled',

  /** 第三方登录开关 */
  USER_LOGIN_SOCIAL = 'user.login.social.enabled',
  /** 密码找回开关 */
  USER_PASSWORD_RESET = 'user.password.reset.enabled',
  // ========== 用户模块 ==========
  /** 用户注册开关 */
  USER_REGISTER = 'user.register.enabled',
}

/**
 * 开关状态缓存
 * @description 避免重复请求相同开关状态
 */
const switchCache = new Map<string, boolean>();

/**
 * 获取单个开关状态
 * @param switchKey 开关键
 * @returns 开关状态
 */
export async function getSwitchStatus(switchKey: SwitchKey): Promise<boolean> {
  // 优先从缓存获取
  const cached = switchCache.get(switchKey);
  if (cached !== undefined) {
    return cached;
  }

  try {
    const response = await baseRequestClient.get<{
      code: number;
      data: boolean;
    }>(`${BASE_URL}/${switchKey}`);
    // baseRequestClient 返回完整响应，需要从 response.data.data 获取实际值
    const enabled = response.data.data;

    // 缓存结果
    switchCache.set(switchKey, enabled);

    return enabled;
  } catch {
    // 请求失败时默认返回 false
    return false;
  }
}

/**
 * 批量获取开关状态
 * @param switchKeys 开关键数组
 * @returns 开关状态映射表
 */
export async function getBatchSwitchStatus(
  switchKeys: SwitchKey[],
): Promise<Record<string, boolean>> {
  // 找出未缓存的开关
  const uncached = switchKeys.filter((key) => !switchCache.has(key));

  if (uncached.length > 0) {
    try {
      const response = await baseRequestClient.post<{
        code: number;
        data: Record<string, boolean>;
      }>(`${BASE_URL}/batch`, uncached);

      // baseRequestClient 返回完整响应，需要从 response.data.data 获取实际值
      for (const [key, value] of Object.entries(response.data.data)) {
        switchCache.set(key, value);
      }
    } catch {
      // 请求失败时，将未缓存的开关设为 false
      for (const key of uncached) {
        switchCache.set(key, false);
      }
    }
  }

  // 从缓存构建返回结果
  const result: Record<string, boolean> = {};
  for (const key of switchKeys) {
    result[key] = switchCache.get(key) ?? false;
  }

  return result;
}

/**
 * 清除开关缓存
 * @param switchKey 指定开关键，不传则清除所有
 */
export function clearSwitchCache(switchKey?: SwitchKey): void {
  if (switchKey) {
    switchCache.delete(switchKey);
  } else {
    switchCache.clear();
  }
}
