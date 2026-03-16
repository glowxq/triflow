/**
 * 微信模块类型定义
 */

export namespace WechatTabbarApi {
  /** 底部菜单 VO */
  export interface TabbarVO {
    /** 主键ID */
    id: number;
    /** 菜单名称 */
    text: string;
    /** 页面路径 */
    pagePath: string;
    /** 图标类型 */
    iconType: string;
    /** 图标资源 */
    icon: string;
    /** 选中图标资源 */
    iconActive?: string;
    /** 徽标 */
    badge?: string;
    /** 是否鼓包 */
    isBulge?: number;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status: number;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime?: string;
    /** 更新时间 */
    updateTime?: string;
  }

  /** 查询参数 */
  export interface QueryParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 状态 */
    status?: number;
    /** 页码 */
    pageNum?: number;
    /** 每页数量 */
    pageSize?: number;
  }

  /** 创建参数 */
  export interface CreateParams {
    /** 菜单名称 */
    text: string;
    /** 页面路径 */
    pagePath: string;
    /** 图标类型 */
    iconType: string;
    /** 图标资源 */
    icon: string;
    /** 选中图标资源 */
    iconActive?: string;
    /** 徽标 */
    badge?: string;
    /** 是否鼓包 */
    isBulge?: number;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新参数 */
  export interface UpdateParams extends CreateParams {
    /** 主键ID */
    id: number;
  }
}
