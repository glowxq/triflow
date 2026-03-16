import { baseRequestClient, requestClient } from '#/api/request';

export namespace AuthApi {
  /** 登录接口参数 */
  export interface LoginParams {
    password?: string;
    username?: string;
  }

  /** 登录接口返回值 */
  export interface LoginResult {
    accessToken: string;
  }

  export interface RefreshTokenResult {
    data: string;
    status: number;
  }
}

/**
 * 登录
 */
export async function loginApi(data: AuthApi.LoginParams) {
  return requestClient.post<AuthApi.LoginResult>('/base/client/auth/login', data);
}

/**
 * 刷新accessToken
 */
export async function refreshTokenApi() {
  return baseRequestClient.post<AuthApi.RefreshTokenResult>(
    '/base/client/auth/refresh',
    {
      withCredentials: true,
    },
  );
}

/**
 * 退出登录
 */
export async function logoutApi() {
  return baseRequestClient.post('/base/client/auth/logout', {
    withCredentials: true,
  });
}

/**
 * 获取用户权限码
 */
export async function getAccessCodesApi() {
  return requestClient.get<string[]>('/base/client/auth/codes');
}

// ==================== 短信验证码 ====================

/**
 * 发送短信验证码
 * @param phone 手机号
 * @param type 类型：login | register | reset
 */
export async function sendSmsCodeApi(phone: string, type: string = 'login') {
  return requestClient.post<null>('/base/public/sms/send', { phone, type });
}

// ==================== 飞书登录 ====================

/**
 * 获取飞书授权 URL
 * @param redirectUri 回调地址
 * @param state 状态码（防 CSRF）
 */
export async function getFeishuAuthUrl(redirectUri?: string, state?: string) {
  return requestClient.get<string>('/base/client/auth/feishu/authUrl', {
    params: { redirectUri, state },
  });
}

/**
 * 飞书登录
 * @param code 飞书授权码
 */
export async function feishuLoginApi(code: string) {
  return requestClient.post<AuthApi.LoginResult>('/base/client/auth/login', {
    grantType: 'feishu',
    code,
  });
}

// ==================== 个人资料管理 ====================

export namespace ProfileApi {
  /** 更新个人资料参数 */
  export interface UpdateParams {
    nickname?: string;
    realName?: string;
    avatar?: string;
    gender?: number;
    phone?: string;
    email?: string;
    introduction?: string;
  }

  /** 第三方绑定信息 */
  export interface UserSocialVO {
    id: number;
    socialType: string;
    nickname?: string;
    avatar?: string;
    bindTime?: string;
  }

  /** 绑定第三方账号参数 */
  export interface BindSocialParams {
    code: string;
    force?: boolean;
  }
}

/**
 * 更新个人资料
 */
export async function updateProfileApi(data: ProfileApi.UpdateParams) {
  return requestClient.put<null>('/base/client/auth/profile', data);
}

/**
 * 获取当前用户的第三方绑定列表
 */
export async function getUserSocialsApi() {
  return requestClient.get<ProfileApi.UserSocialVO[]>('/base/client/auth/socials');
}

/**
 * 绑定飞书账号
 */
export async function bindFeishuApi(data: ProfileApi.BindSocialParams) {
  return requestClient.post<null>('/base/client/auth/bind/feishu', data);
}

/**
 * 解绑第三方账号
 */
export async function unbindSocialApi(socialType: string) {
  return requestClient.delete<null>(`/base/client/auth/unbind/${socialType}`);
}
