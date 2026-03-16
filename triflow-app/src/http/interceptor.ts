/**
 * HTTP 请求拦截器
 * @description 处理请求 URL、Token 注入、超时配置等
 */

import type { CustomRequestOptions } from '@/http/types'
import { useTokenStore } from '@/store'
import { getEnvBaseUrl } from '@/utils'
import { stringifyQuery } from './tools/queryString'

// 请求基准地址
const baseUrl = getEnvBaseUrl()

// 拦截器配置
const httpInterceptor = {
  /**
   * 拦截前触发
   */
  invoke(options: CustomRequestOptions) {
    // 如果您使用了 alova，则请把下面的代码放开注释
    // alova 执行流程：alova beforeRequest --> 本拦截器 --> alova responded
    // return options

    // ==================== URL 处理 ====================

    // 接口请求支持通过 query 参数配置 queryString
    if (options.query) {
      const queryStr = stringifyQuery(options.query)
      if (options.url.includes('?')) {
        options.url += `&${queryStr}`
      }
      else {
        options.url += `?${queryStr}`
      }
    }

    // 非 http 开头需拼接地址
    if (!options.url.startsWith('http')) {
      // #ifdef H5
      // H5 环境下判断是否启用代理
      const proxyEnable = String(import.meta.env.VITE_APP_PROXY_ENABLE) === 'true'
      if (proxyEnable) {
        // 自动拼接代理前缀
        options.url = (import.meta.env.VITE_APP_PROXY_PREFIX || '/api') + options.url
      }
      else {
        options.url = baseUrl + options.url
      }
      // #endif

      // 非 H5 正常拼接
      // #ifndef H5
      options.url = baseUrl + options.url
      // #endif
    }

    // ==================== 请求配置 ====================

    // 1. 请求超时（60 秒）
    options.timeout = 60000

    // 2. 添加请求头
    options.header = {
      'Content-Type': 'application/json',
      ...options.header,
    }

    // 3. 添加 Token 请求头
    const tokenStore = useTokenStore()
    const token = tokenStore.validToken

    if (token) {
      options.header.Authorization = `Bearer ${token}`
    }

    return options
  },
}

/**
 * 请求拦截器安装器
 */
export const requestInterceptor = {
  install() {
    // 拦截 request 请求
    uni.addInterceptor('request', httpInterceptor)
    // 拦截 uploadFile 文件上传
    uni.addInterceptor('uploadFile', httpInterceptor)
  },
}
