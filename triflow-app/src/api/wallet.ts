/**
 * 钱包相关 API
 */
import type { PageResult } from './types/common'
import type {
  WalletRechargeConfigVO,
  WalletRechargeCreateDTO,
  WalletRechargeOrderQuery,
  WalletRechargeOrderVO,
  WalletRechargePayVO,
  WalletSignInStatusVO,
  WalletSignInVO,
  WalletTransactionQuery,
  WalletTransactionVO,
} from './types/wallet'
import { http } from '@/http/http'

/**
 * 获取我的钱包变动记录
 */
export function getMyTransactions(query: WalletTransactionQuery = {}): Promise<PageResult<WalletTransactionVO>> {
  return http.get('/base/client/wallet/transactions', query)
}

/**
 * 获取充值配置
 */
export function getRechargeConfigs(type?: 'points' | 'balance'): Promise<WalletRechargeConfigVO[]> {
  return http.get('/base/client/wallet/recharge/configs', { type })
}

/**
 * 创建充值订单
 */
export function createRechargeOrder(data: WalletRechargeCreateDTO): Promise<WalletRechargePayVO> {
  return http.post('/base/client/wallet/recharge', data)
}

/**
 * 获取我的充值订单
 */
export function getMyRechargeOrders(query: WalletRechargeOrderQuery = {}): Promise<PageResult<WalletRechargeOrderVO>> {
  return http.get('/base/client/wallet/recharge/orders', query)
}

/**
 * 获取签到状态
 */
export function getSignInStatus(): Promise<WalletSignInStatusVO> {
  return http.get('/base/client/wallet/signin/status')
}

/**
 * 执行签到
 */
export function signIn(): Promise<WalletSignInVO> {
  return http.post('/base/client/wallet/signin')
}
