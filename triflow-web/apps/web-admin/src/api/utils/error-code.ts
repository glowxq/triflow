/**
 * TMMCCC 错误码规范工具
 *
 * 错误码格式: TMMCCC (6位数字)
 * - T (1位): 错误类型 (1=业务异常, 2=告警异常, 3=客户端处理异常)
 * - MM (2位): 模块代码 (00-99)
 * - CCC (3位): 错误序列号 (000-999)
 *
 * @see triflow-server/common/common-core/src/main/java/com/glowxq/triflow/common/core/enums/ErrorCodeEnum.java
 */

// ==================== 错误类型枚举 ====================

/** 错误类型 */
export enum ErrorType {
  /** 告警异常 - 需要记录日志并告警，对用户展示通用错误信息 */
  ALERT = 2,
  /** 业务异常 - 通用业务错误，可直接展示给用户 */
  BUSINESS = 1,
  /** 客户端处理异常 - 需要客户端特殊处理（如跳转登录、刷新页面等） */
  CLIENT = 3,
}

// ==================== 模块代码枚举 ====================

/** 模块代码 */
export enum ErrorModule {
  /** 认证模块 */
  AUTH = 1,
  /** 通用模块 */
  COMMON = 0,
  /** 文件模块 */
  FILE = 4,
  /** 消息模块 */
  MESSAGE = 8,
  /** 订单模块 */
  ORDER = 6,
  /** 支付模块 */
  PAY = 5,
  /** 商品模块 */
  PRODUCT = 7,
  /** 系统模块 */
  SYSTEM = 3,
  /** 用户模块 */
  USER = 2,
}

// ==================== 常用错误码枚举 ====================

/** 常用错误码（客户端处理异常） */
export enum ErrorCode {
  // ==================== 认证模块 (AUTH = 01) ====================

  /** Token 无效 */
  INVALID_TOKEN = 301_002,
  /** 被踢下线 */
  KICKED_OUT = 301_004,
  /** 无权限 */
  NO_PERMISSION = 301_006,
  /** 无角色 */
  NO_ROLE = 301_007,
  /** 未登录 */
  NOT_LOGIN = 301_001,
  /** 被顶下线 */
  REPLACED = 301_005,
  /** Token 已过期 */
  TOKEN_EXPIRED = 301_003,
}

// ==================== 错误码解析函数 ====================

/** 解析后的错误码结构 */
export interface ParsedErrorCode {
  /** 错误类型 */
  type: ErrorType;
  /** 模块代码 */
  module: number;
  /** 错误序列号 */
  sequence: number;
}

/**
 * 解析 TMMCCC 错误码
 * @param code 错误码
 * @returns 解析结果，无效返回 null
 */
export function parseErrorCode(code: number): null | ParsedErrorCode {
  // 有效范围: 100000 - 399999
  if (code < 100_000 || code > 399_999) {
    return null;
  }

  const type = Math.floor(code / 100_000) as ErrorType;
  const module = Math.floor((code % 100_000) / 1000);
  const sequence = code % 1000;

  return { type, module, sequence };
}

/**
 * 判断是否是客户端处理异常
 * @param code 错误码
 */
export function isClientError(code: number): boolean {
  const parsed = parseErrorCode(code);
  return parsed !== null && parsed.type === ErrorType.CLIENT;
}

/**
 * 判断是否是认证相关错误（需要跳转登录）
 * @param code 错误码
 */
export function isAuthError(code: number): boolean {
  const parsed = parseErrorCode(code);
  return (
    parsed !== null &&
    parsed.type === ErrorType.CLIENT &&
    parsed.module === ErrorModule.AUTH
  );
}

/**
 * 判断是否是需要跳转登录的错误
 * @param code 业务错误码
 * @param statusCode HTTP 状态码
 */
export function isTokenExpiredError(
  code: number | undefined,
  statusCode?: number,
): boolean {
  // 1. HTTP 401 状态码
  if (statusCode === 401) {
    return true;
  }

  // 2. 兼容旧版 code === 401
  if (code === 401) {
    return true;
  }

  // 3. TMMCCC 格式的认证错误 (301xxx)
  if (code !== undefined && isAuthError(code)) {
    return true;
  }

  return false;
}

/**
 * 获取错误码对应的错误消息
 * @param code 错误码
 * @returns 错误消息
 */
export function getErrorMessage(code: number): string {
  const messages: Record<number, string> = {
    [ErrorCode.NOT_LOGIN]: '请先登录',
    [ErrorCode.INVALID_TOKEN]: 'Token 无效，请重新登录',
    [ErrorCode.TOKEN_EXPIRED]: 'Token 已过期，请重新登录',
    [ErrorCode.KICKED_OUT]: '您已被管理员强制下线',
    [ErrorCode.REPLACED]: '您已在其他设备登录',
    [ErrorCode.NO_PERMISSION]: '权限不足',
    [ErrorCode.NO_ROLE]: '角色权限不足',
  };

  return messages[code] || '未知错误';
}
