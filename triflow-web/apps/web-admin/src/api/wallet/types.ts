/**
 * 钱包模块类型定义
 */

/** 分页结果 */
export interface PageResult<T> {
  records: T[];
  /** 总记录数 (MyBatis-Flex 返回 totalRow) */
  totalRow: number;
  /** 兼容字段 */
  total?: number;
}

/**
 * 钱包变动记录 API
 */
export namespace WalletApi {
  /** 钱包变动记录 VO */
  export interface TransactionVO {
    /** 记录ID */
    id: number;
    /** 用户ID */
    userId: number;
    /** 用户名 */
    username?: string;
    /** 用户昵称 */
    nickname?: string;
    /** 交易类型: points-积分, balance-余额 */
    type: 'balance' | 'points';
    /** 交易类型描述 */
    typeDesc?: string;
    /** 操作类型: income-收入, expense-支出, freeze-冻结, unfreeze-解冻 */
    action: 'expense' | 'freeze' | 'income' | 'unfreeze';
    /** 操作类型描述 */
    actionDesc?: string;
    /** 变动金额 */
    amount: number;
    /** 变动前金额 */
    beforeAmount: number;
    /** 变动后金额 */
    afterAmount: number;
    /** 业务类型 */
    bizType?: string;
    /** 业务ID */
    bizId?: string;
    /** 标题 */
    title: string;
    /** 备注说明 */
    remark?: string;
    /** 操作人ID */
    operatorId?: number;
    /** 操作人名称 */
    operatorName?: string;
    /** 创建时间 */
    createTime: string;
  }

  /** 查询参数 */
  export interface QueryParams {
    /** 页码 */
    pageNum?: number;
    /** 每页条数 */
    pageSize?: number;
    /** 用户ID */
    userId?: number;
    /** 用户名（模糊查询） */
    username?: string;
    /** 交易类型 */
    type?: 'balance' | 'points';
    /** 操作类型 */
    action?: 'expense' | 'freeze' | 'income' | 'unfreeze';
    /** 业务类型 */
    bizType?: string;
    /** 开始时间 */
    startTime?: string;
    /** 结束时间 */
    endTime?: string;
  }

  /** 钱包调整参数 */
  export interface AdjustParams {
    /** 用户ID */
    userId: number;
    /** 交易类型: points-积分, balance-余额 */
    type: 'balance' | 'points';
    /** 操作类型: income-收入, expense-支出 */
    action: 'expense' | 'income';
    /** 变动金额（正数） */
    amount: number;
    /** 标题 */
    title: string;
    /** 业务类型 */
    bizType?: string;
    /** 业务ID */
    bizId?: string;
    /** 备注说明 */
    remark?: string;
  }

  /** 充值配置 VO */
  export interface RechargeConfigVO {
    id: number;
    configName: string;
    type: 'balance' | 'points';
    payAmount: number;
    rewardAmount: number;
    bonusAmount?: number;
    status: number;
    sort?: number;
    remark?: string;
    createTime?: string;
  }

  /** 充值配置查询参数 */
  export interface RechargeConfigQuery {
    pageNum?: number;
    pageSize?: number;
    keyword?: string;
    type?: 'balance' | 'points';
    status?: number;
  }

  /** 充值配置创建参数 */
  export interface RechargeConfigCreateParams {
    configName: string;
    type: 'balance' | 'points';
    payAmount: number;
    rewardAmount: number;
    bonusAmount?: number;
    status: number;
    sort?: number;
    remark?: string;
  }

  /** 充值配置更新参数 */
  export interface RechargeConfigUpdateParams extends RechargeConfigCreateParams {
    id: number;
  }

  /** 签到记录 VO */
  export interface SignInVO {
    id: number;
    userId: number;
    username?: string;
    nickname?: string;
    signDate: string;
    points: number;
    consecutiveDays: number;
    createTime?: string;
  }

  /** 签到查询参数 */
  export interface SignInQuery {
    pageNum?: number;
    pageSize?: number;
    userId?: number;
    username?: string;
    startDate?: string;
    endDate?: string;
  }

  /** 充值订单 VO */
  export interface RechargeOrderVO {
    id: number;
    orderNo: string;
    userId: number;
    username?: string;
    nickname?: string;
    configId?: number;
    configName?: string;
    type: 'balance' | 'points';
    typeDesc?: string;
    payAmount: number;
    rewardAmount: number;
    status: 'pending' | 'paid' | 'refunded' | 'closed' | 'failed';
    statusDesc?: string;
    wechatTransactionId?: string;
    payTime?: string;
    refundNo?: string;
    refundAmount?: number;
    refundReason?: string;
    refundTime?: string;
    refundBy?: number;
    remark?: string;
    createTime?: string;
    updateTime?: string;
  }

  /** 充值订单查询参数 */
  export interface RechargeOrderQuery {
    pageNum?: number;
    pageSize?: number;
    userId?: number;
    username?: string;
    orderNo?: string;
    type?: 'balance' | 'points';
    status?: 'pending' | 'paid' | 'refunded' | 'closed' | 'failed';
    startTime?: string;
    endTime?: string;
  }

  /** 充值订单退款参数 */
  export interface RechargeOrderRefundParams {
    orderId: number;
    reason?: string;
  }

  /** 充值订单更新参数 */
  export interface RechargeOrderUpdateParams {
    id: number;
    status?: RechargeOrderVO['status'];
    payAmount?: number;
    rewardAmount?: number;
    remark?: string;
  }
}
