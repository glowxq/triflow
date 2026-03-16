/**
 * 消息通知类型定义
 * @description 根据 triflow-server 后端接口定义
 */

export type NotifyChannel = 'wechat_miniapp' | 'sms' | 'email'

export type NotifySubscribeStatus = 'accept' | 'reject' | 'ban'

/** 消息通知模板 */
export interface NotifyTemplateVO {
  id: number
  templateKey: string
  templateName: string
  templateId: string
  channel: NotifyChannel
  sort?: number
  status?: number
  remark?: string
  createTime?: string
  updateTime?: string
}

/** 消息订阅记录 */
export interface NotifySubscribeVO {
  id: number
  userId: number
  templateId: string
  templateKey?: string
  channel: NotifyChannel
  subscribeStatus: NotifySubscribeStatus
  subscribeTime?: string
}

export interface NotifySubscribeItemDTO {
  templateId: string
  subscribeStatus: NotifySubscribeStatus
}

export interface NotifySubscribeSubmitDTO {
  channel: NotifyChannel
  items: NotifySubscribeItemDTO[]
}
