/**
 * CMS 内容管理模块类型定义
 * @description 文本内容、文本分类相关的 TypeScript 接口
 */

import type { PageParams } from '../system/types';

// ==================== 文本分类相关类型 ====================

export namespace CmsTextCategoryApi {
  /** 文本分类列表项 */
  export interface CategoryVO {
    /** 分类ID */
    id: number;
    /** 父分类ID */
    pid: number;
    /** 分类名称 */
    categoryName: string;
    /** 分类标识 */
    categoryKey: string;
    /** 分类图标 */
    icon?: string;
    /** 排序 */
    sort?: number;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime: string;
    /** 更新时间 */
    updateTime?: string;
    /** 子分类列表 */
    children?: CategoryVO[];
  }

  /** 创建分类参数 */
  export interface CreateParams {
    /** 父分类ID */
    pid?: number;
    /** 分类名称 */
    categoryName: string;
    /** 分类标识 */
    categoryKey: string;
    /** 分类图标 */
    icon?: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新分类参数 */
  export interface UpdateParams {
    /** 分类ID */
    id: number;
    /** 父分类ID */
    pid?: number;
    /** 分类名称 */
    categoryName?: string;
    /** 分类标识 */
    categoryKey?: string;
    /** 分类图标 */
    icon?: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 分类查询参数 */
  export interface QueryParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 父分类ID */
    pid?: number;
    /** 状态筛选 */
    status?: number;
  }
}

// ==================== 文本内容相关类型 ====================

export namespace CmsTextApi {
  /** 文本内容列表项 */
  export interface TextVO {
    /** 文本ID */
    id: number;
    /** 分类ID */
    categoryId?: number;
    /** 分类名称 */
    categoryName?: string;
    /** 文本标识 */
    textKey: string;
    /** 文本标题 */
    textTitle: string;
    /** 副标题 */
    textSubtitle?: string;
    /** 摘要 */
    textSummary?: string;
    /** 文本内容 */
    textContent?: string;
    /** 文本类型 */
    textType?: string;
    /** 内容格式 */
    contentType?: string;
    /** 封面图片URL */
    coverImage?: string;
    /** 作者 */
    author?: string;
    /** 来源 */
    source?: string;
    /** 跳转链接 */
    linkUrl?: string;
    /** 关键词 */
    keywords?: string;
    /** 标签 */
    tags?: string;
    /** 排序 */
    sort?: number;
    /** 是否置顶（0:否, 1:是） */
    top?: number;
    /** 是否推荐（0:否, 1:是） */
    recommend?: number;
    /** 状态（0:草稿, 1:已发布） */
    status: number;
    /** 浏览次数 */
    viewCount?: number;
    /** 点赞次数 */
    likeCount?: number;
    /** 发布时间 */
    publishTime?: string;
    /** 版本号 */
    version?: number;
    /** 创建时间 */
    createTime: string;
    /** 更新时间 */
    updateTime?: string;
  }

  /** 创建文本参数 */
  export interface CreateParams {
    /** 分类ID */
    categoryId?: number;
    /** 文本标识 */
    textKey: string;
    /** 文本标题 */
    textTitle: string;
    /** 副标题 */
    textSubtitle?: string;
    /** 摘要 */
    textSummary?: string;
    /** 文本内容 */
    textContent?: string;
    /** 文本类型 */
    textType?: string;
    /** 内容格式 */
    contentType?: string;
    /** 封面图片URL */
    coverImage?: string;
    /** 作者 */
    author?: string;
    /** 来源 */
    source?: string;
    /** 原文链接 */
    sourceUrl?: string;
    /** 跳转链接 */
    linkUrl?: string;
    /** 关键词 */
    keywords?: string;
    /** 标签 */
    tags?: string;
    /** 排序 */
    sort?: number;
    /** 是否置顶 */
    top?: number;
    /** 是否推荐 */
    recommend?: number;
    /** 状态 */
    status?: number;
    /** 发布时间 */
    publishTime?: string;
    /** 生效时间 */
    effectiveTime?: string;
    /** 失效时间 */
    expireTime?: string;
    /** 备注 */
    remark?: string;
  }

  /** 更新文本参数 */
  export interface UpdateParams {
    /** 文本ID */
    id: number;
    /** 分类ID */
    categoryId?: number;
    /** 文本标识 */
    textKey?: string;
    /** 文本标题 */
    textTitle?: string;
    /** 副标题 */
    textSubtitle?: string;
    /** 摘要 */
    textSummary?: string;
    /** 文本内容 */
    textContent?: string;
    /** 文本类型 */
    textType?: string;
    /** 内容格式 */
    contentType?: string;
    /** 封面图片URL */
    coverImage?: string;
    /** 作者 */
    author?: string;
    /** 来源 */
    source?: string;
    /** 原文链接 */
    sourceUrl?: string;
    /** 跳转链接 */
    linkUrl?: string;
    /** 关键词 */
    keywords?: string;
    /** 标签 */
    tags?: string;
    /** 排序 */
    sort?: number;
    /** 是否置顶 */
    top?: number;
    /** 是否推荐 */
    recommend?: number;
    /** 状态 */
    status?: number;
    /** 发布时间 */
    publishTime?: string;
    /** 生效时间 */
    effectiveTime?: string;
    /** 失效时间 */
    expireTime?: string;
    /** 备注 */
    remark?: string;
  }

  /** 文本查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 分类ID */
    categoryId?: number;
    /** 文本类型 */
    textType?: string;
    /** 状态筛选 */
    status?: number;
    /** 是否置顶 */
    top?: number;
    /** 是否推荐 */
    recommend?: number;
    /** 开始时间 */
    startTime?: string;
    /** 结束时间 */
    endTime?: string;
  }
}
