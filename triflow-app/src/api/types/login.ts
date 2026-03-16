/**
 * 登录相关类型定义
 * @description 根据 triflow-server 后端接口定义
 */

// ==================== 授权类型 ====================

/**
 * 授权类型枚举
 * @see triflow-server/common/common-security/src/main/java/com/glowxq/common/security/enums/GrantType.java
 */
export type GrantType = 'password' | 'sms' | 'wechat_miniapp' | 'feishu' | 'third' | 'test'

// ==================== 登录请求 DTO ====================

/**
 * 登录请求基础参数
 */
export interface LoginBaseDTO {
  /** 授权类型 */
  grantType: GrantType
}

/**
 * 密码登录参数
 */
export interface PasswordLoginDTO extends LoginBaseDTO {
  grantType: 'password'
  /** 用户名 */
  username: string
  /** 密码 */
  password: string
  /** 验证码 Key（如果启用验证码） */
  captchaKey?: string
  /** 验证码值（如果启用验证码） */
  captchaCode?: string
}

/**
 * 短信登录参数
 */
export interface SmsLoginDTO extends LoginBaseDTO {
  grantType: 'sms'
  /** 手机号 */
  phone: string
  /** 短信验证码 */
  smsCode: string
}

/**
 * 微信小程序登录参数
 */
export interface WechatMiniappLoginDTO extends LoginBaseDTO {
  grantType: 'wechat_miniapp'
  /** 微信授权码 */
  code: string
  /** 加密数据（可选，用于获取手机号） */
  encryptedData?: string
  /** 初始化向量（可选，用于获取手机号） */
  iv?: string
}

/**
 * 统一登录请求类型
 */
export type LoginDTO = PasswordLoginDTO | SmsLoginDTO | WechatMiniappLoginDTO

// ==================== 登录响应 VO ====================

/**
 * 登录响应（后端 LoginVO）
 * @see triflow-server/base/src/main/java/com/glowxq/triflow/base/auth/pojo/vo/LoginVO.java
 */
export interface LoginVO {
  /** 访问令牌 */
  accessToken: string
  /** 刷新令牌（可选） */
  refreshToken?: string | null
  /** 用户ID */
  userId: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName?: string
  /** 头像 */
  avatar?: string
  /** 角色编码列表 */
  roles: string[]
  /** 首页路径 */
  homePath?: string
  /** 手机号（脱敏显示，用于判断是否需要收集手机号） */
  phone?: string
  /** 用户信息是否完整（有头像且有昵称） */
  profileComplete?: boolean
}

/**
 * 兼容旧版 - 登录返回的信息
 * @deprecated 请使用 LoginVO
 */
export type IAuthLoginRes = LoginVO

// ==================== 用户信息 VO ====================

/**
 * 用户信息（后端 UserInfoVO）
 * @see triflow-server/base/src/main/java/com/glowxq/triflow/base/auth/pojo/vo/UserInfoVO.java
 */
export interface UserInfoVO {
  /** 用户ID */
  userId: number
  /** 用户名 */
  username: string
  /** 真实姓名 */
  realName?: string
  /** 昵称 */
  nickname?: string
  /** 头像 */
  avatar?: string
  /** 描述 */
  desc?: string
  /** 首页路径 */
  homePath?: string
  /** 角色编码列表 */
  roles: string[]
  /** 手机号 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 性别（0:未知, 1:男, 2:女） */
  gender?: number
  /** 是否已设置密码 */
  passwordSet?: boolean
  /** 可用积分 */
  points?: number
  /** 冻结积分 */
  frozenPoints?: number
  /** 奖励积分（非充值获得的积分统计，如签到、赠送、活动奖励等） */
  rewardPoints?: number
}

/**
 * 兼容旧版 - 用户信息
 * @deprecated 请使用 UserInfoVO
 */
export type IUserInfoRes = UserInfoVO

// ==================== 验证码相关 ====================

/**
 * 验证码状态响应
 */
export interface CaptchaStatusVO {
  /** 是否启用验证码 */
  captchaEnabled: boolean
}

/**
 * 图片验证码响应
 */
export interface CaptchaVO {
  /** 验证码 Key */
  captchaKey: string
  /** 验证码图片（Base64） */
  captchaImage: string
}

/**
 * 兼容旧版
 * @deprecated 请使用 CaptchaVO
 */
export interface ICaptcha {
  captchaEnabled: boolean
  uuid: string
  image: string
}

// ==================== 短信验证码 ====================

/**
 * 发送短信验证码请求
 */
export interface SendSmsCodeDTO {
  /** 手机号 */
  phone: string
  /** 验证码类型：login-登录, register-注册, reset-重置密码 */
  type: 'login' | 'register' | 'reset'
}

// ==================== 注册相关 ====================

/**
 * 用户注册请求（手机号+短信验证码注册）
 */
export interface RegisterDTO {
  /** 手机号 */
  phone: string
  /** 短信验证码 */
  smsCode: string
  /** 密码 */
  password: string
  /** 确认密码 */
  confirmPassword: string
  /** 昵称（可选） */
  nickname?: string
  /** 头像（可选） */
  avatar?: string
}

// ==================== 修改信息相关 ====================

/**
 * 更新个人资料请求
 */
export interface ProfileUpdateDTO {
  /** 昵称 */
  nickname?: string
  /** 真实姓名 */
  realName?: string
  /** 头像 */
  avatar?: string
  /** 性别（0:未知, 1:男, 2:女） */
  gender?: number
  /** 手机号 */
  phone?: string
  /** 邮箱 */
  email?: string
  /** 个人简介 */
  introduction?: string
}

/**
 * 修改密码请求
 */
export interface UpdatePasswordDTO {
  /** 旧密码 */
  oldPassword: string
  /** 新密码 */
  newPassword: string
  /** 确认密码 */
  confirmPassword: string
}

/**
 * 兼容旧版
 * @deprecated
 */
export interface IUpdateInfo {
  id: number
  name: string
  sex: string
}

/**
 * 兼容旧版
 * @deprecated
 */
export interface IUpdatePassword {
  id: number
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

// ==================== 文件上传 ====================

/**
 * 上传成功的信息
 */
export interface IUploadSuccessInfo {
  fileId: number
  originalName: string
  fileName: string
  storagePath: string
  fileHash: string
  fileType: string
  fileBusinessType: string
  fileSize: number
}

// ==================== 类型守卫函数 ====================

/**
 * 判断登录响应是否包含 refreshToken
 */
export function hasRefreshToken(loginRes: LoginVO): boolean {
  return !!loginRes.refreshToken
}

// ==================== 兼容旧版（保留但标记废弃） ====================

/** @deprecated */
export type AuthMode = 'single' | 'double'

/** @deprecated */
export interface ISingleTokenRes {
  token: string
  expiresIn: number
}

/** @deprecated */
export interface IDoubleTokenRes {
  accessToken: string
  refreshToken: string
  accessExpiresIn: number
  refreshExpiresIn: number
}

/** @deprecated */
export interface AuthStorage {
  mode: AuthMode
  tokens: ISingleTokenRes | IDoubleTokenRes
  userInfo?: IUserInfoRes
  loginTime: number
}

/** @deprecated */
export function isSingleTokenRes(tokenRes: any): tokenRes is ISingleTokenRes {
  return 'token' in tokenRes && !('refreshToken' in tokenRes)
}

/** @deprecated */
export function isDoubleTokenRes(tokenRes: any): tokenRes is IDoubleTokenRes {
  return 'accessToken' in tokenRes && 'refreshToken' in tokenRes
}
