/**
 * 钱包相关类型定义
 */

/** 钱包变动记录 */
export interface WalletTransactionVO {
  /** 记录ID */
  id: number
  /** 用户ID */
  userId: number
  /** 用户名 */
  username?: string
  /** 用户昵称 */
  nickname?: string
  /** 交易类型: points-积分, balance-余额 */
  type: 'points' | 'balance'
  /** 交易类型描述 */
  typeDesc?: string
  /** 操作类型: income-收入, expense-支出, freeze-冻结, unfreeze-解冻 */
  action: 'income' | 'expense' | 'freeze' | 'unfreeze'
  /** 操作类型描述 */
  actionDesc?: string
  /** 变动金额 */
  amount: number
  /** 变动前金额 */
  beforeAmount: number
  /** 变动后金额 */
  afterAmount: number
  /** 业务类型 */
  bizType?: string
  /** 业务ID */
  bizId?: string
  /** 标题 */
  title: string
  /** 备注说明 */
  remark?: string
  /** 操作人ID */
  operatorId?: number
  /** 操作人名称 */
  operatorName?: string
  /** 创建时间 */
  createTime: string
}

/** 钱包变动查询参数 */
export interface WalletTransactionQuery {
  /** 页码 */
  pageNum?: number
  /** 每页条数 */
  pageSize?: number
  /** 交易类型 */
  type?: 'points' | 'balance'
  /** 操作类型 */
  action?: 'income' | 'expense' | 'freeze' | 'unfreeze'
  /** 业务类型 */
  bizType?: string
  /** 开始时间 */
  startTime?: string
  /** 结束时间 */
  endTime?: string
}

/** 充值配置 */
export interface WalletRechargeConfigVO {
  id: number
  configName: string
  type: 'points' | 'balance'
  payAmount: number
  rewardAmount: number
  bonusAmount?: number
  status: number
  sort?: number
  remark?: string
  createTime?: string
}

/** 充值下单参数 */
export interface WalletRechargeCreateDTO {
  configId: number
}

/** 充值支付结果 */
export interface WalletRechargePayVO {
  orderNo: string
  type: 'points' | 'balance'
  payAmount: number
  rewardAmount: number
  paymentData: {
    appId: string
    timeStamp: string
    nonceStr: string
    package: string
    signType: string
    paySign: string
  }
}

/** 充值订单 */
export interface WalletRechargeOrderVO {
  id: number
  orderNo: string
  userId: number
  username?: string
  nickname?: string
  configId?: number
  configName?: string
  type: 'points' | 'balance'
  typeDesc?: string
  payAmount: number
  rewardAmount: number
  status: 'pending' | 'paid' | 'refunded' | 'closed' | 'failed'
  statusDesc?: string
  wechatTransactionId?: string
  payTime?: string
  refundNo?: string
  refundAmount?: number
  refundReason?: string
  refundTime?: string
  refundBy?: number
  remark?: string
  createTime: string
  updateTime?: string
}

/** 充值订单查询参数 */
export interface WalletRechargeOrderQuery {
  pageNum?: number
  pageSize?: number
  orderNo?: string
  type?: 'points' | 'balance'
  status?: 'pending' | 'paid' | 'refunded' | 'closed' | 'failed'
  startTime?: string
  endTime?: string
}

/** 签到状态 */
export interface WalletSignInStatusVO {
  signedToday: boolean
  consecutiveDays: number
  rewardPoints: number
  lastSignDate?: string
}

/** 签到记录 */
export interface WalletSignInVO {
  id: number
  userId: number
  signDate: string
  points: number
  consecutiveDays: number
  createTime?: string
}
