import type { NotifySubscribeItemDTO, NotifySubscribeStatus } from '@/api/types/notify'
import { getNotifyTemplates, submitNotifySubscribes } from '@/api/notify'

const DEFAULT_CHANNEL = 'wechat_miniapp'
const TEMPLATE_BATCH_SIZE = 3

function chunk<T>(list: T[], size: number) {
  const result: T[][] = []
  for (let i = 0; i < list.length; i += size) {
    result.push(list.slice(i, i + size))
  }
  return result
}

async function requestSubscribeMessage(templateIds: string[]) {
  return new Promise<Record<string, NotifySubscribeStatus>>((resolve, reject) => {
    // #ifdef MP-WEIXIN
    wx.requestSubscribeMessage({
      tmplIds: templateIds,
      success: (res) => resolve(res as Record<string, NotifySubscribeStatus>),
      fail: err => reject(err),
    })
    // #endif

    // #ifndef MP-WEIXIN
    reject(new Error('仅支持微信小程序订阅'))
    // #endif
  })
}

export async function subscribeWechatMessages() {
  const templates = await getNotifyTemplates(DEFAULT_CHANNEL)
  const templateIds = templates.map(item => item.templateId).filter(Boolean)

  if (!templateIds.length) {
    uni.showToast({ title: '暂无可订阅模板', icon: 'none' })
    return
  }

  const batches = chunk(templateIds, TEMPLATE_BATCH_SIZE)
  const resultMap: Record<string, NotifySubscribeStatus> = {}

  for (const batch of batches) {
    const result = await requestSubscribeMessage(batch)
    Object.assign(resultMap, result)
  }

  const items: NotifySubscribeItemDTO[] = templateIds.map((templateId) => ({
    templateId,
    subscribeStatus: resultMap[templateId] || 'reject',
  }))

  await submitNotifySubscribes({
    channel: DEFAULT_CHANNEL,
    items,
  })
}
