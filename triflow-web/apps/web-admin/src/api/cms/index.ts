/**
 * CMS 内容管理模块 API 导出
 */

// 文本内容 API
export {
  checkTextKey,
  createText,
  deleteText,
  deleteTextBatch,
  getTextById,
  getTextByKey,
  getTextList,
  getTextPage,
  incrementLikeCount,
  incrementViewCount,
  publishText,
  setTextRecommend,
  setTextTop,
  unpublishText,
  updateText,
} from './text';

// 文本分类 API
export {
  checkCategoryKey,
  createCategory,
  deleteCategory,
  deleteCategoryBatch,
  getAllEnabledCategories,
  getCategoryById,
  getCategoryByKey,
  getCategoryList,
  getCategoryTree,
  updateCategory,
} from './text-category';

// 类型导出
export type { CmsTextApi, CmsTextCategoryApi } from './types';
