export enum ResultEnum {
  // 0和200当做成功都很普遍，这里直接兼容两者（PS：0和200通常都不会当做错误码，但是有的接口会返回0，有的接口会返回200）
  Success0 = 0, // 成功
  Success200 = 200, // 成功
  Error = 400, // 错误
  Unauthorized = 401, // 未授权
  Forbidden = 403, // 禁止访问（原为forbidden）
  NotFound = 404, // 未找到（原为notFound）
  MethodNotAllowed = 405, // 方法不允许（原为methodNotAllowed）
  RequestTimeout = 408, // 请求超时（原为requestTimeout）
  InternalServerError = 500, // 服务器错误（原为internalServerError）
  NotImplemented = 501, // 未实现（原为notImplemented）
  BadGateway = 502, // 网关错误（原为badGateway）
  ServiceUnavailable = 503, // 服务不可用（原为serviceUnavailable）
  GatewayTimeout = 504, // 网关超时（原为gatewayTimeout）
  HttpVersionNotSupported = 505, // HTTP版本不支持（原为httpVersionNotSupported）
}

// ==================== TMMCCC 错误码规范 ====================
/**
 * 错误码格式: TMMCCC
 * - T (1位): 错误类型 (1=业务异常, 2=告警异常, 3=客户端处理异常)
 * - MM (2位): 模块代码 (00-99)
 * - CCC (3位): 错误序列号 (000-999)
 *
 * @see triflow-server/common/common-core/src/main/java/com/glowxq/triflow/common/core/enums/ErrorCodeEnum.java
 */

/** 错误类型枚举 */
export enum ErrorType {
  /** 业务异常 - 通用业务错误，可直接展示给用户 */
  BUSINESS = 1,
  /** 告警异常 - 需要记录日志并告警，对用户展示通用错误信息 */
  ALERT = 2,
  /** 客户端处理异常 - 需要客户端特殊处理（如跳转登录、刷新页面等） */
  CLIENT = 3,
}

/** 模块代码枚举 */
export enum ErrorModule {
  /** 通用模块 */
  COMMON = 0,
  /** 认证模块 */
  AUTH = 1,
  /** 用户模块 */
  USER = 2,
  /** 系统模块 */
  SYSTEM = 3,
  /** 文件模块 */
  FILE = 4,
  /** 支付模块 */
  PAY = 5,
  /** 订单模块 */
  ORDER = 6,
  /** 商品模块 */
  PRODUCT = 7,
  /** 消息模块 */
  MESSAGE = 8,
}

/** 常用错误码枚举 */
export enum ErrorCode {
  // ==================== 客户端处理异常 (TYPE_CLIENT = 3) ====================

  // 认证模块 (AUTH = 01)
  /** 未登录 */
  NOT_LOGIN = 301001,
  /** Token 无效 */
  INVALID_TOKEN = 301002,
  /** Token 已过期 */
  TOKEN_EXPIRED = 301003,
  /** 被踢下线 */
  KICKED_OUT = 301004,
  /** 被顶下线 */
  REPLACED = 301005,
  /** 无权限 */
  NO_PERMISSION = 301006,
  /** 无角色 */
  NO_ROLE = 301007,
}

/**
 * 解析 TMMCCC 错误码
 * @param code 错误码
 * @returns { type: ErrorType, module: number, sequence: number } | null
 */
export function parseErrorCode(code: number): { type: ErrorType, module: number, sequence: number } | null {
  // 有效范围: 100000 - 399999
  if (code < 100000 || code > 399999) {
    return null
  }

  const type = Math.floor(code / 100000) as ErrorType
  const module = Math.floor((code % 100000) / 1000)
  const sequence = code % 1000

  return { type, module, sequence }
}

/**
 * 判断是否是客户端处理异常
 * @param code 错误码
 */
export function isClientError(code: number): boolean {
  const parsed = parseErrorCode(code)
  return parsed !== null && parsed.type === ErrorType.CLIENT
}

/**
 * 判断是否是认证相关错误（需要跳转登录）
 * @param code 错误码
 */
export function isAuthError(code: number): boolean {
  const parsed = parseErrorCode(code)
  return parsed !== null && parsed.type === ErrorType.CLIENT && parsed.module === ErrorModule.AUTH
}

/**
 * 判断是否是需要跳转登录的错误
 * @param code 业务错误码
 * @param statusCode HTTP 状态码
 */
export function isTokenExpiredError(code: number, statusCode?: number): boolean {
  // 1. HTTP 401 状态码
  if (statusCode === 401) {
    return true
  }

  // 2. 兼容旧版 code === 401
  if (code === 401) {
    return true
  }

  // 3. TMMCCC 格式的认证错误 (301xxx)
  if (isAuthError(code)) {
    return true
  }

  return false
}
export enum ContentTypeEnum {
  JSON = 'application/json;charset=UTF-8',
  FORM_URLENCODED = 'application/x-www-form-urlencoded;charset=UTF-8',
  FORM_DATA = 'multipart/form-data;charset=UTF-8',
}
/**
 * 根据状态码，生成对应的错误信息
 * @param {number|string} status 状态码
 * @returns {string} 错误信息
 */
export function ShowMessage(status: number | string): string {
  let message: string
  switch (status) {
    case 400:
      message = '请求错误(400)'
      break
    case 401:
      message = '未授权，请重新登录(401)'
      break
    case 403:
      message = '拒绝访问(403)'
      break
    case 404:
      message = '请求出错(404)'
      break
    case 408:
      message = '请求超时(408)'
      break
    case 500:
      message = '服务器错误(500)'
      break
    case 501:
      message = '服务未实现(501)'
      break
    case 502:
      message = '网络错误(502)'
      break
    case 503:
      message = '服务不可用(503)'
      break
    case 504:
      message = '网络超时(504)'
      break
    case 505:
      message = 'HTTP版本不受支持(505)'
      break
    default:
      message = `连接出错(${status})!`
  }
  return `${message}，请检查网络或联系管理员！`
}
