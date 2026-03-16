/**
 * 登录相关 API 接口
 * @description 根据 triflow-server 后端接口定义
 * @see triflow-server/base/src/main/java/com/glowxq/triflow/base/auth/controller/AuthController.java
 */

import type {
  CaptchaStatusVO,
  CaptchaVO,
  LoginDTO,
  LoginVO,
  PasswordLoginDTO,
  ProfileUpdateDTO,
  RegisterDTO,
  SendSmsCodeDTO,
  SmsLoginDTO,
  UpdatePasswordDTO,
  UserInfoVO,
  WechatMiniappLoginDTO,
} from './types/login'
import { http } from '@/http/http'

// ==================== 登录相关 ====================

/**
 * 统一登录接口
 * @param data 登录参数（支持密码、短信、微信等多种方式）
 */
export function login(data: LoginDTO) {
  return http.post<LoginVO>('/base/client/auth/login', data)
}

/**
 * 密码登录
 * @param username 用户名
 * @param password 密码
 * @param captchaKey 验证码 Key（可选）
 * @param captchaCode 验证码值（可选）
 */
export function passwordLogin(
  username: string,
  password: string,
  captchaKey?: string,
  captchaCode?: string,
) {
  const data: PasswordLoginDTO = {
    grantType: 'password',
    username,
    password,
    captchaKey,
    captchaCode,
  }
  return login(data)
}

/**
 * 短信验证码登录
 * @param phone 手机号
 * @param smsCode 短信验证码
 */
export function smsLogin(phone: string, smsCode: string) {
  const data: SmsLoginDTO = {
    grantType: 'sms',
    phone,
    smsCode,
  }
  return login(data)
}

/**
 * 微信小程序登录
 * @param code 微信授权码
 * @param encryptedData 加密数据（可选）
 * @param iv 初始化向量（可选）
 */
export function wechatMiniappLogin(code: string, encryptedData?: string, iv?: string) {
  const data: WechatMiniappLoginDTO = {
    grantType: 'wechat_miniapp',
    code,
    encryptedData,
    iv,
  }
  return login(data)
}

/**
 * 获取微信登录凭证
 * @returns Promise 包含微信登录凭证(code)
 */
export function getWxCode(): Promise<UniApp.LoginRes> {
  return new Promise((resolve, reject) => {
    uni.login({
      provider: 'weixin',
      success: res => resolve(res),
      fail: err => reject(new Error(err.errMsg || '获取微信登录凭证失败')),
    })
  })
}

/**
 * 微信一键登录（组合调用）
 * @description 先获取微信 code，再调用登录接口
 */
export async function wxQuickLogin(): Promise<LoginVO> {
  const wxRes = await getWxCode()
  if (!wxRes.code) {
    throw new Error('获取微信授权码失败')
  }
  return wechatMiniappLogin(wxRes.code)
}

// ==================== 登出相关 ====================

/**
 * 退出登录
 */
export function logout() {
  return http.post<void>('/base/client/auth/logout')
}

// ==================== Token 刷新 ====================

/**
 * 刷新 Token
 */
export function refreshToken() {
  return http.post<LoginVO>('/base/client/auth/refresh')
}

// ==================== 用户信息 ====================

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return http.get<UserInfoVO>('/base/client/auth/user/info')
}

/**
 * 获取权限码列表
 */
export function getAccessCodes() {
  return http.get<string[]>('/base/client/auth/codes')
}

/**
 * 更新个人资料
 */
export function updateProfile(data: ProfileUpdateDTO) {
  return http.put<void>('/base/client/auth/profile', data)
}

/**
 * 修改密码
 */
export function updatePassword(data: UpdatePasswordDTO) {
  return http.put<void>('/base/client/auth/password', data)
}

// ==================== 验证码相关 ====================

/**
 * 获取验证码状态（是否启用）
 */
export function getCaptchaStatus() {
  return http.get<CaptchaStatusVO>('/base/public/captcha/status')
}

/**
 * 获取图片验证码
 */
export function getCaptcha() {
  return http.get<CaptchaVO>('/base/public/captcha/image')
}

/**
 * 发送短信验证码
 * @param phone 手机号
 * @param type 验证码类型
 */
export function sendSmsCode(phone: string, type: SendSmsCodeDTO['type'] = 'login') {
  return http.post<void>('/base/public/sms/send', { phone, type })
}

// ==================== 注册相关 ====================

/**
 * 用户注册
 * @returns 登录信息（注册成功后自动登录）
 */
export function register(data: RegisterDTO) {
  return http.post<LoginVO>('/base/client/auth/register', data)
}

// ==================== 微信手机号绑定 ====================

/**
 * 绑定微信手机号
 * @param code 手机号授权码（微信小程序获取手机号返回的 code）
 */
export function bindWechatPhone(code: string) {
  return http.post<void>('/base/client/auth/bind/wechat-phone', { code })
}

// ==================== 兼容旧版导出（标记废弃） ====================

/** @deprecated 请使用 passwordLogin */
export interface ILoginForm {
  username: string
  password: string
}

/** @deprecated 请使用 getCaptcha */
export function getCode() {
  return getCaptcha()
}

/** @deprecated 请使用 wxQuickLogin */
export function wxLogin(data: { code: string }) {
  return wechatMiniappLogin(data.code)
}

/** @deprecated 请使用 updateProfile */
export function updateInfo(data: any) {
  return updateProfile(data)
}

/** @deprecated 请使用 updatePassword */
export function updateUserPassword(data: any) {
  return updatePassword(data)
}
