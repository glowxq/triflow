/**
 * CMS 文本管理类型定义
 */

/**
 * 文本类型
 */
export type TextType = 'article' | 'notice' | 'help' | 'agreement' | 'about' | 'other' | 'faq' | 'banner'

/**
 * 内容格式
 */
export type ContentType = 'html' | 'markdown' | 'text'

/**
 * 文本状态
 */
export type TextStatus = 0 | 1 | 2 // 0:草稿, 1:已发布, 2:已下架

/**
 * CMS 文本内容 VO
 */
export interface CmsTextVO {
  /** 文本ID */
  id: number
  /** 分类ID */
  categoryId?: number
  /** 分类名称 */
  categoryName?: string
  /** 文本标识 */
  textKey: string
  /** 文本标题 */
  textTitle: string
  /** 副标题 */
  textSubtitle?: string
  /** 摘要 */
  textSummary?: string
  /** 文本内容 */
  textContent?: string
  /** 文本类型 */
  textType?: TextType
  /** 内容格式 */
  contentType?: ContentType
  /** 封面图片URL */
  coverImage?: string
  /** 作者 */
  author?: string
  /** 来源 */
  source?: string
  /** 跳转链接 */
  linkUrl?: string
  /** 关键词 */
  keywords?: string
  /** 标签 */
  tags?: string
  /** 显示排序 */
  sort?: number
  /** 是否置顶 */
  top?: number
  /** 是否推荐 */
  recommend?: number
  /** 状态 */
  status?: TextStatus
  /** 浏览次数 */
  viewCount?: number
  /** 点赞次数 */
  likeCount?: number
  /** 发布时间 */
  publishTime?: string
  /** 版本号 */
  version?: number
  /** 创建时间 */
  createTime?: string
  /** 更新时间 */
  updateTime?: string
}

/**
 * 常用文本标识枚举
 */
export enum TextKey {
  /** 用户协议 */
  USER_AGREEMENT = 'user_agreement',
  /** 隐私政策 */
  PRIVACY_POLICY = 'privacy_policy',
  /** Cookie政策 */
  COOKIE_POLICY = 'cookie_policy',
  /** 关于我们 */
  ABOUT_US = 'about_us',
  /** 联系我们 */
  CONTACT_US = 'contact_us',
  /** 帮助反馈 */
  HELP_FEEDBACK = 'help_feedback',
}
