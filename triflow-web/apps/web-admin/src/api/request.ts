/**
 * HTTP 请求配置
 *
 * 错误码规范说明 (TMMCCC 格式)：
 * - T (1位): 错误类型 (1=业务异常, 2=告警异常, 3=客户端处理异常)
 * - MM (2位): 模块代码 (01=AUTH, 02=USER, ...)
 * - CCC (3位): 错误序列号
 *
 * 客户端处理异常 (3xxxxx) 需要前端特殊处理：
 * - 301xxx: 认证模块错误，需跳转登录页
 *
 * @see triflow-server/common/common-core/src/main/java/com/glowxq/triflow/common/core/enums/ErrorCodeEnum.java
 */
import type { RequestClientOptions } from '@vben/request';

import { useAppConfig } from '@vben/hooks';
import { preferences } from '@vben/preferences';
import {
  authenticateResponseInterceptor,
  defaultResponseInterceptor,
  errorMessageResponseInterceptor,
  RequestClient,
} from '@vben/request';
import { useAccessStore } from '@vben/stores';

import { ElMessage } from 'element-plus';

import { useAuthStore } from '#/store';

import { refreshTokenApi } from './core';
import { getErrorMessage, isAuthError } from './utils/error-code';

const { apiURL } = useAppConfig(import.meta.env, import.meta.env.PROD);

function createRequestClient(baseURL: string, options?: RequestClientOptions) {
  const client = new RequestClient({
    ...options,
    baseURL,
  });

  /**
   * 重新认证逻辑
   */
  async function doReAuthenticate() {
    console.warn('Access token or refresh token is invalid or expired. ');
    const accessStore = useAccessStore();
    const authStore = useAuthStore();
    accessStore.setAccessToken(null);
    if (
      preferences.app.loginExpiredMode === 'modal' &&
      accessStore.isAccessChecked
    ) {
      accessStore.setLoginExpired(true);
    } else {
      await authStore.logout();
    }
  }

  /**
   * 刷新token逻辑
   */
  async function doRefreshToken() {
    const accessStore = useAccessStore();
    const resp = await refreshTokenApi();
    const newToken = resp.data;
    accessStore.setAccessToken(newToken);
    return newToken;
  }

  function formatToken(token: null | string) {
    return token ? `Bearer ${token}` : null;
  }

  // 请求头处理
  client.addRequestInterceptor({
    fulfilled: async (config) => {
      const accessStore = useAccessStore();

      config.headers.Authorization = formatToken(accessStore.accessToken);
      config.headers['Accept-Language'] = preferences.app.locale;
      return config;
    },
  });

  // 处理返回的响应数据格式
  client.addResponseInterceptor(
    defaultResponseInterceptor({
      codeField: 'code',
      dataField: 'data',
      successCode: 0,
    }),
  );

  // token过期的处理
  client.addResponseInterceptor(
    authenticateResponseInterceptor({
      client,
      doReAuthenticate,
      doRefreshToken,
      enableRefreshToken: preferences.app.enableRefreshToken,
      formatToken,
    }),
  );

  /**
   * TMMCCC 错误码处理 - 客户端认证异常 (301xxx)
   *
   * 处理所有需要重新登录的场景：
   * - 301001: 未登录
   * - 301002: Token 无效
   * - 301003: Token 已过期
   * - 301004: 被踢下线
   * - 301005: 被顶下线
   * - 301006: 无权限
   * - 301007: 无角色
   */
  client.addResponseInterceptor({
    rejected: async (error) => {
      const responseData = error?.response?.data;
      const code = responseData?.code;

      // 检测 TMMCCC 格式的认证错误 (301xxx)
      if (code !== undefined && isAuthError(code)) {
        const authStore = useAuthStore();
        const errorMessage = getErrorMessage(code);
        ElMessage.warning(
          responseData?.message || errorMessage || '请重新登录',
        );
        await authStore.logout();
        throw error;
      }

      throw error;
    },
  });

  // 通用的错误处理,如果没有进入上面的错误处理逻辑，就会进入这里
  client.addResponseInterceptor(
    errorMessageResponseInterceptor((msg: string, error) => {
      // 这里可以根据业务进行定制,你可以拿到 error 内的信息进行定制化处理，根据不同的 code 做不同的提示，而不是直接使用 message.error 提示 msg
      // 当前mock接口返回的错误字段是 error 或者 message
      const responseData = error?.response?.data ?? {};
      const errorMessage = responseData?.error ?? responseData?.message ?? '';
      // 如果没有错误信息，则会根据状态码进行提示
      ElMessage.error(errorMessage || msg);
    }),
  );

  return client;
}

export const requestClient = createRequestClient(apiURL, {
  responseReturn: 'data',
});

export const baseRequestClient = new RequestClient({ baseURL: apiURL });
