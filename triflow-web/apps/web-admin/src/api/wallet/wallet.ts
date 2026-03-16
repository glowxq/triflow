/**
 * 钱包管理 API
 * @description 积分、余额变动管理接口
 */
import type { PageResult, WalletApi } from './types';

import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/wallet/transaction';
const RECHARGE_CONFIG_URL = '/base/admin/wallet/recharge-config';
const SIGNIN_URL = '/base/admin/wallet/signin';
const RECHARGE_ORDER_URL = '/base/admin/wallet/recharge-order';

/**
 * 分页查询变动记录
 */
export async function getTransactionPage(params?: WalletApi.QueryParams) {
  return requestClient.get<PageResult<WalletApi.TransactionVO>>(
    `${BASE_URL}/page`,
    { params },
  );
}

/**
 * 调整用户钱包
 */
export async function adjustWallet(data: WalletApi.AdjustParams) {
  return requestClient.post<null>(`${BASE_URL}/adjust`, data);
}

/**
 * 充值配置分页
 */
export async function getRechargeConfigPage(
  params?: WalletApi.RechargeConfigQuery,
) {
  return requestClient.post<PageResult<WalletApi.RechargeConfigVO>>(
    `${RECHARGE_CONFIG_URL}/page`,
    params,
  );
}

/**
 * 充值配置详情
 */
export async function getRechargeConfigDetail(id: number) {
  return requestClient.get<WalletApi.RechargeConfigVO>(
    `${RECHARGE_CONFIG_URL}/detail`,
    { params: { id } },
  );
}

/**
 * 创建充值配置
 */
export async function createRechargeConfig(
  data: WalletApi.RechargeConfigCreateParams,
) {
  return requestClient.post<number>(`${RECHARGE_CONFIG_URL}/create`, data);
}

/**
 * 更新充值配置
 */
export async function updateRechargeConfig(
  data: WalletApi.RechargeConfigUpdateParams,
) {
  return requestClient.put<boolean>(`${RECHARGE_CONFIG_URL}/update`, data);
}

/**
 * 删除充值配置
 */
export async function deleteRechargeConfig(id: number) {
  return requestClient.delete<boolean>(`${RECHARGE_CONFIG_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除充值配置
 */
export async function deleteRechargeConfigBatch(ids: number[]) {
  return requestClient.delete<boolean>(`${RECHARGE_CONFIG_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 签到记录分页
 */
export async function getSignInPage(params?: WalletApi.SignInQuery) {
  return requestClient.get<PageResult<WalletApi.SignInVO>>(
    `${SIGNIN_URL}/page`,
    { params },
  );
}

/**
 * 充值订单分页
 */
export async function getRechargeOrderPage(
  params?: WalletApi.RechargeOrderQuery,
) {
  return requestClient.get<PageResult<WalletApi.RechargeOrderVO>>(
    `${RECHARGE_ORDER_URL}/page`,
    { params },
  );
}

/**
 * 充值订单退款
 */
export async function refundRechargeOrder(
  data: WalletApi.RechargeOrderRefundParams,
) {
  return requestClient.post<null>(`${RECHARGE_ORDER_URL}/refund`, data);
}

/**
 * 更新充值订单
 */
export async function updateRechargeOrder(
  data: WalletApi.RechargeOrderUpdateParams,
) {
  return requestClient.put<boolean>(`${RECHARGE_ORDER_URL}/update`, data);
}
