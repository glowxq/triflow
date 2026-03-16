import type { CmsTextCategoryApi } from './types';

/**
 * 文本分类 API
 * @description 文本分类的增删改查接口
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/admin/cms/text-category';

/**
 * 创建文本分类
 */
export async function createCategory(data: CmsTextCategoryApi.CreateParams) {
  return requestClient.post(`${BASE_URL}/create`, data);
}

/**
 * 更新文本分类
 */
export async function updateCategory(data: CmsTextCategoryApi.UpdateParams) {
  return requestClient.put(`${BASE_URL}/update`, data);
}

/**
 * 获取文本分类详情
 */
export async function getCategoryById(id: number) {
  return requestClient.get<CmsTextCategoryApi.CategoryVO>(
    `${BASE_URL}/detail`,
    {
      params: { id },
    },
  );
}

/**
 * 删除文本分类
 */
export async function deleteCategory(id: number) {
  return requestClient.delete(`${BASE_URL}/delete`, {
    params: { id },
  });
}

/**
 * 批量删除文本分类
 */
export async function deleteCategoryBatch(ids: number[]) {
  return requestClient.delete(`${BASE_URL}/deleteBatch`, {
    data: ids,
  });
}

/**
 * 查询文本分类列表
 */
export async function getCategoryList(params?: CmsTextCategoryApi.QueryParams) {
  return requestClient.post<CmsTextCategoryApi.CategoryVO[]>(
    `${BASE_URL}/list`,
    params ?? {},
  );
}

/**
 * 获取文本分类树形结构
 */
export async function getCategoryTree(params?: CmsTextCategoryApi.QueryParams) {
  return requestClient.post<CmsTextCategoryApi.CategoryVO[]>(
    `${BASE_URL}/tree`,
    params ?? {},
  );
}

/**
 * 根据标识获取分类
 */
export async function getCategoryByKey(categoryKey: string) {
  return requestClient.get<CmsTextCategoryApi.CategoryVO>(
    `${BASE_URL}/getByKey`,
    {
      params: { categoryKey },
    },
  );
}

/**
 * 获取所有启用的分类
 */
export async function getAllEnabledCategories() {
  return requestClient.get<CmsTextCategoryApi.CategoryVO[]>(
    `${BASE_URL}/listAll`,
  );
}

/**
 * 检查分类标识是否存在
 */
export async function checkCategoryKey(
  categoryKey: string,
  excludeId?: number,
) {
  return requestClient.get<boolean>(`${BASE_URL}/checkKey`, {
    params: { categoryKey, excludeId },
  });
}
