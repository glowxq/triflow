/**
 * 系统相关类型定义
 * @description 根据 triflow-server 后端接口定义
 */

/** 部门树节点 */
export interface SysDeptTreeVO {
  /** 部门ID */
  id: number
  /** 父部门ID */
  parentId?: number
  /** 部门名称 */
  deptName: string
  /** 排序 */
  sort?: number
  /** 负责人 */
  leader?: string
  /** 状态 */
  status?: number
  /** 子部门 */
  children?: SysDeptTreeVO[]
}

/** 系统开关 */
export interface SysSwitchVO {
  id: number
  switchName: string
  switchKey: string
  switchValue: number
  switchType?: string
  category?: string
  description?: string
  remark?: string
}

/** 系统配置 */
export interface SysConfigVO {
  id: number
  configName: string
  configKey: string
  configValue: string
  valueType?: string
  category?: string
  status?: number
  remark?: string
}

/** 系统开关查询 */
export interface SysSwitchQuery {
  keyword?: string
  switchKey?: string
  switchType?: string
  category?: string
  switchValue?: number
}

/** 系统配置查询 */
export interface SysConfigQuery {
  keyword?: string
  configType?: number
  category?: string
  status?: number
}
