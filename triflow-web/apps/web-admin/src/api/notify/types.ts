/**
 * 消息订阅模块类型定义
 */

export namespace NotifySubscribeApi {
  /** 订阅记录 */
  export interface SubscribeVO {
    /** 主键ID */
    id: number;
    /** 用户ID */
    userId: number;
    /** 模板ID */
    templateId: string;
    /** 模板标识 */
    templateKey: string;
    /** 通知渠道 */
    channel: string;
    /** 订阅状态 */
    subscribeStatus: string;
    /** 订阅时间 */
    subscribeTime: string;
  }

  /** 查询参数 */
  export interface QueryParams {
    /** 搜索关键词（模板标识/模板ID） */
    keyword?: string;
    /** 用户ID */
    userId?: number;
    /** 通知渠道 */
    channel?: string;
    /** 订阅状态 */
    subscribeStatus?: string;
    /** 页码 */
    pageNum?: number;
    /** 每页数量 */
    pageSize?: number;
  }

  /** 更新订阅状态参数 */
  export interface UpdateStatusParams {
    /** 订阅ID */
    id: number;
    /** 订阅状态 */
    subscribeStatus: string;
  }
}
