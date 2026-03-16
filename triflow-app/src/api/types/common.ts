/**
 * 通用类型定义
 */

/** 分页结果 */
export interface PageResult<T> {
  /** 记录列表 */
  records: T[]
  /** 总记录数 */
  total?: number
  /** 总记录数（兼容 totalRow） */
  totalRow?: number
  /** 当前页码 */
  pageNumber?: number
  /** 每页条数 */
  pageSize?: number
}

/** 分页查询参数 */
export interface PageQuery {
  /** 页码（从1开始） */
  pageNum?: number
  /** 每页条数 */
  pageSize?: number
}
