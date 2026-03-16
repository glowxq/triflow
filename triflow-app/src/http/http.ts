/**
 * HTTP 请求核心模块
 * @description 封装 uni.request，处理响应拦截和错误处理
 */

/**
 * HTTP 请求核心模块
 * @description 封装 uni.request，处理响应拦截和错误处理
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

import type { CustomRequestOptions, IResponse } from '@/http/types'
import { useTokenStore } from '@/store/token'
import { toLoginPage } from '@/utils/toLoginPage'
import { ErrorType, isTokenExpiredError, parseErrorCode, ResultEnum } from './tools/enum'

export function http<T>(options: CustomRequestOptions) {
  // 1. 返回 Promise 对象
  return new Promise<T>((resolve, reject) => {
    uni.request({
      ...options,
      dataType: 'json',
      // #ifndef MP-WEIXIN
      responseType: 'json',
      // #endif
      // 响应成功
      success: async (res) => {
        const responseData = res.data as IResponse<T>
        const { code } = responseData

        // 检查是否是认证错误（需要跳转登录）
        // 支持：HTTP 401、业务码 401、TMMCCC 格式 301xxx（客户端认证错误）
        if (isTokenExpiredError(code, res.statusCode)) {
          const tokenStore = useTokenStore()
          if (tokenStore.tokenExpiredNotified) {
            return reject(res)
          }
          tokenStore.markTokenExpiredNotified()
          // Token 过期，清理用户信息，跳转到登录页
          await tokenStore.logout({ skipApi: true })
          uni.showToast({
            title: responseData.msg || responseData.message || '登录已过期，请重新登录',
            icon: 'none',
          })
          setTimeout(() => {
            toLoginPage()
          }, 1500)
          return reject(res)
        }

        // 处理其他成功状态（HTTP状态码200-299）
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 判断业务是否成功
          // 1. 检查 success 字段（优先，后端明确返回）
          // 2. 检查 code 是否为成功码（0 或 200）
          const isBusinessSuccess = responseData.success === true
            || (responseData.success === undefined && (code === ResultEnum.Success0 || code === ResultEnum.Success200))

          // 处理业务逻辑错误
          if (!isBusinessSuccess) {
            const errorMessage = responseData.msg || responseData.message || '请求错误'
            const parsed = parseErrorCode(code)
            const showToast = !options.hideErrorToast

            if (showToast) {
              if (parsed?.type === ErrorType.ALERT) {
                uni.showToast({
                  icon: 'none',
                  title: '系统繁忙，请稍后重试',
                })
              }
              else {
                uni.showToast({
                  icon: 'none',
                  title: errorMessage,
                })
              }
            }
            return reject(new Error(errorMessage))
          }
          return resolve(responseData.data)
        }

        // 处理其他 HTTP 错误（4xx、5xx）
        const errorMessage = (res.data as any)?.msg || (res.data as any)?.message || `请求错误 (${res.statusCode})`
        if (!options.hideErrorToast) {
          uni.showToast({
            icon: 'none',
            title: errorMessage,
          })
        }
        reject(new Error(errorMessage))
      },
      // 响应失败
      fail(err) {
        uni.showToast({
          icon: 'none',
          title: '网络错误，换个网络试试',
        })
        reject(err)
      },
    })
  })
}

/**
 * GET 请求
 * @param url 后台地址
 * @param query 请求query参数
 * @param header 请求头，默认为json格式
 * @returns
 */
export function httpGet<T>(url: string, query?: Record<string, any>, header?: Record<string, any>, options?: Partial<CustomRequestOptions>) {
  return http<T>({
    url,
    query,
    method: 'GET',
    header,
    ...options,
  })
}

/**
 * POST 请求
 * @param url 后台地址
 * @param data 请求body参数
 * @param query 请求query参数，post请求也支持query，很多微信接口都需要
 * @param header 请求头，默认为json格式
 * @returns
 */
export function httpPost<T>(url: string, data?: Record<string, any>, query?: Record<string, any>, header?: Record<string, any>, options?: Partial<CustomRequestOptions>) {
  return http<T>({
    url,
    query,
    data,
    method: 'POST',
    header,
    ...options,
  })
}
/**
 * PUT 请求
 */
export function httpPut<T>(url: string, data?: Record<string, any>, query?: Record<string, any>, header?: Record<string, any>, options?: Partial<CustomRequestOptions>) {
  return http<T>({
    url,
    data,
    query,
    method: 'PUT',
    header,
    ...options,
  })
}

/**
 * DELETE 请求（无请求体，仅 query）
 */
export function httpDelete<T>(url: string, query?: Record<string, any>, header?: Record<string, any>, options?: Partial<CustomRequestOptions>) {
  return http<T>({
    url,
    query,
    method: 'DELETE',
    header,
    ...options,
  })
}

// 支持与 axios 类似的API调用
http.get = httpGet
http.post = httpPost
http.put = httpPut
http.delete = httpDelete

// 支持与 alovaJS 类似的API调用
http.Get = httpGet
http.Post = httpPost
http.Put = httpPut
http.Delete = httpDelete
