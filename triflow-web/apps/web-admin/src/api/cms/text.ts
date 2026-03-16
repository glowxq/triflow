import type { PageResult } from '../system/types';
import type { CmsTextApi } from './types';

/**
 * 文本内容 API
 * @description 文本内容的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/cms/text';

/**
 * 创建文本内容
 */
export async function createText(data: CmsTextApi.CreateParams) {
  return requestClient.post(`${BASE_URL}/create`, data);
}

/**
 * 更新文本内容
 */
export async function updateText(data: CmsTextApi.UpdateParams) {
  return requestClient.put(`${BASE_URL}/update`, data);
}

/**
 * 获取文本详情
 */
export async function getTextById(id: number) {
  return requestClient.get<CmsTextApi.TextVO>(`${BASE_URL}/detail`, {
    params: { id },
  });
}

/**
 * 根据标识获取文本内容
 */
export async function getTextByKey(textKey: string) {
  return requestClient.get<CmsTextApi.TextVO>(`${BASE_URL}/getByKey`, {
    params: { textKey },
  });
}

/**
 * 删除文本内容
 */
export async function deleteText(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除文本内容
 */
export async function deleteTextBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 查询文本列表
 */
export async function getTextList(params?: CmsTextApi.QueryParams) {
  return requestClient.post<CmsTextApi.TextVO[]>(
    `${BASE_URL}/list`,
    params ?? {},
  );
}

/**
 * 分页查询文本内容
 */
export async function getTextPage(params?: CmsTextApi.QueryParams) {
  return requestClient.post<PageResult<CmsTextApi.TextVO>>(
    `${BASE_URL}/page`,
    params ?? {},
  );
}

/**
 * 发布文本内容
 */
export async function publishText(id: number) {
  return requestClient.put(`${BASE_URL}/publish`, null, {
    params: { id },
  });
}

/**
 * 下架文本内容
 */
export async function unpublishText(id: number) {
  return requestClient.put(`${BASE_URL}/unpublish`, null, {
    params: { id },
  });
}

/**
 * 设置置顶状态
 */
export async function setTextTop(id: number, top: number) {
  return requestClient.put(`${BASE_URL}/setTop`, null, {
    params: { id, top },
  });
}

/**
 * 设置推荐状态
 */
export async function setTextRecommend(id: number, recommend: number) {
  return requestClient.put(`${BASE_URL}/setRecommend`, null, {
    params: { id, recommend },
  });
}

/**
 * 增加浏览次数
 */
export async function incrementViewCount(id: number) {
  return requestClient.put(`${BASE_URL}/incrementView`, null, {
    params: { id },
  });
}

/**
 * 增加点赞次数
 */
export async function incrementLikeCount(id: number) {
  return requestClient.put(`${BASE_URL}/incrementLike`, null, {
    params: { id },
  });
}

/**
 * 检查文本标识是否存在
 */
export async function checkTextKey(textKey: string, excludeId?: number) {
  return requestClient.get<boolean>(`${BASE_URL}/checkKey`, {
    params: { textKey, excludeId },
  });
}
