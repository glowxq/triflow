/**
 * 消息订阅模块 API 导出
 */

export {
  deleteNotifySubscribe,
  deleteNotifySubscribeBatch,
  getNotifySubscribeList,
  getNotifySubscribePage,
  updateNotifySubscribeStatus,
} from './subscribe';

export type { NotifySubscribeApi } from './types';
