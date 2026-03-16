/**
 * 数据分析 API 类型定义
 */

/** 概览数据 */
export interface AnalyticsOverviewVO {
  /** 总用户数 */
  totalUsers: number;
  /** 今日新增用户 */
  todayNewUsers: number;
  /** 总订单数 */
  totalOrders: number;
  /** 今日订单数 */
  todayOrders: number;
  /** 总交易流水 */
  totalTransactionAmount: number;
  /** 今日交易流水 */
  todayTransactionAmount: number;
  /** 总积分发放 */
  totalPointsIssued: number;
  /** 今日积分发放 */
  todayPointsIssued: number;
  /** 总 AI 调用次数 */
  totalAiCalls: number;
  /** 今日 AI 调用次数 */
  todayAiCalls: number;
  /** 总 token 消耗 */
  totalAiTokens: number;
  /** 今日 token 消耗 */
  todayAiTokens: number;
}

/** 趋势数据 */
export interface AnalyticsTrendVO {
  /** 日期列表 */
  dates: string[];
  /** 用户注册趋势 */
  userRegistrations: number[];
  /** 订单数量趋势 */
  orderCounts: number[];
  /** 交易金额趋势 */
  transactionAmounts: number[];
}

/** 分布项 */
export interface DistributionItem {
  /** 名称 */
  name: string;
  /** 数值 */
  value: number;
}

/** 分布数据 */
export interface AnalyticsDistributionVO {
  /** 用户角色分布 */
  userRoleDistribution: DistributionItem[];
  /** 交易类型分布 */
  transactionTypeDistribution: DistributionItem[];
  /** 订单状态分布 */
  orderStatusDistribution: DistributionItem[];
}
