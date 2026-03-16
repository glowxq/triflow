/**
 * 系统管理模块类型定义
 * @description 用户、角色、菜单、部门相关的 TypeScript 接口
 */

// ==================== 通用类型 ====================

/** 分页参数 */
export interface PageParams {
  /** 页码 */
  pageNum?: number;
  /** 每页数量 */
  pageSize?: number;
}

/** 分页结果 */
export interface PageResult<T> {
  /** 数据列表 */
  records: T[];
  /** 当前页码 */
  pageNumber: number;
  /** 每页数量 */
  pageSize: number;
  /** 总条数 */
  totalRow: number;
  /** 总页数 */
  totalPage: number;
}

// ==================== 用户相关类型 ====================

export namespace SysUserApi {
  /** 用户列表项 */
  export interface UserVO {
    /** 用户ID */
    id: number;
    /** 用户名 */
    username: string;
    /** 昵称 */
    nickname?: string;
    /** 真实姓名 */
    realName?: string;
    /** 头像URL */
    avatar?: string;
    /** 性别（0:未知, 1:男, 2:女） */
    gender?: number;
    /** 手机号 */
    phone?: string;
    /** 邮箱 */
    email?: string;
    /** 所属部门ID */
    deptId?: number;
    /** 数据权限范围 */
    dataScope?: string;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 可用积分 */
    points?: number;
    /** 可用余额 */
    balance?: number;
    /** 最后登录时间 */
    loginTime?: string;
    /** 创建时间 */
    createTime: string;
  }

  /** 用户详情 */
  export interface UserDetailVO extends UserVO {
    /** 首页路径 */
    homePath?: string;
    /** 用户类型 */
    userType?: number;
    /** 注册来源 */
    registerSource?: string;
    /** 最后登录IP */
    loginIp?: string;
    /** 登录次数 */
    loginCount?: number;
    /** 备注 */
    remark?: string;
    /** 更新时间 */
    updateTime?: string;
    /** 角色ID列表 */
    roleIds?: number[];
    /** 角色名称列表 */
    roleNames?: string[];
    /** 冻结积分（详情专有） */
    frozenPoints?: number;
    /** 冻结余额（详情专有） */
    frozenBalance?: number;
  }

  /** 创建用户参数 */
  export interface CreateParams {
    /** 用户名 */
    username: string;
    /** 密码 */
    password: string;
    /** 昵称 */
    nickname?: string;
    /** 真实姓名 */
    realName?: string;
    /** 头像URL */
    avatar?: string;
    /** 性别 */
    gender?: number;
    /** 手机号 */
    phone?: string;
    /** 邮箱 */
    email?: string;
    /** 所属部门ID */
    deptId?: number;
    /** 数据权限范围 */
    dataScope?: string;
    /** 状态 */
    status?: number;
    /** 角色ID列表 */
    roleIds?: number[];
    /** 备注 */
    remark?: string;
  }

  /** 更新用户参数 */
  export interface UpdateParams {
    /** 用户ID */
    id: number;
    /** 用户名 */
    username?: string;
    /** 昵称 */
    nickname?: string;
    /** 真实姓名 */
    realName?: string;
    /** 头像URL */
    avatar?: string;
    /** 性别 */
    gender?: number;
    /** 手机号 */
    phone?: string;
    /** 邮箱 */
    email?: string;
    /** 所属部门ID */
    deptId?: number;
    /** 数据权限范围 */
    dataScope?: string;
    /** 状态 */
    status?: number;
    /** 角色ID列表 */
    roleIds?: number[];
    /** 可用积分 */
    points?: number;
    /** 冻结积分 */
    frozenPoints?: number;
    /** 可用余额 */
    balance?: number;
    /** 冻结余额 */
    frozenBalance?: number;
    /** 备注 */
    remark?: string;
  }

  /** 用户查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 状态筛选 */
    status?: number;
    /** 部门ID筛选 */
    deptId?: number;
    /** 数据权限筛选 */
    dataScope?: string;
    /** 创建时间-开始 */
    startTime?: string;
    /** 创建时间-结束 */
    endTime?: string;
  }

  /** 用户第三方绑定信息 */
  export interface UserSocialVO {
    /** ID */
    id: number;
    /** 用户ID */
    userId: number;
    /** 第三方平台类型 */
    socialType: string;
    /** 第三方用户ID */
    socialId: string;
    /** 统一ID */
    unionId?: string;
    /** 第三方昵称 */
    nickname?: string;
    /** 第三方头像 */
    avatar?: string;
    /** 绑定时间 */
    bindTime?: string;
  }
}

// ==================== 角色相关类型 ====================

export namespace SysRoleApi {
  /** 角色列表项 */
  export interface RoleVO {
    /** 角色ID */
    id: number;
    /** 角色编码 */
    roleKey: string;
    /** 角色名称 */
    roleName: string;
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
  }

  /** 创建角色参数 */
  export interface CreateParams {
    /** 角色编码 */
    roleKey: string;
    /** 角色名称 */
    roleName: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 菜单ID列表 */
    menuIds?: number[];
    /** 备注 */
    remark?: string;
  }

  /** 更新角色参数 */
  export interface UpdateParams {
    /** 角色ID */
    id: number;
    /** 角色编码 */
    roleKey?: string;
    /** 角色名称 */
    roleName?: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 菜单ID列表 */
    menuIds?: number[];
    /** 备注 */
    remark?: string;
  }

  /** 角色查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 状态筛选 */
    status?: number;
  }
}

// ==================== 菜单相关类型 ====================

export namespace SysMenuApi {
  /** 菜单列表项 */
  export interface MenuVO {
    /** 菜单ID */
    id: number;
    /** 父菜单ID */
    parentId: number;
    /** 菜单名称 */
    menuName: string;
    /** 菜单类型 (M:目录, C:菜单, F:按钮) */
    menuType: string;
    /** 路由路径 */
    path?: string;
    /** 路由名称 */
    name?: string;
    /** 组件路径 */
    component?: string;
    /** 重定向地址 */
    redirect?: string;
    /** 权限标识 */
    permission?: string;
    /** 图标 */
    icon?: string;
    /** 排序 */
    sort?: number;
    /** 是否可见（0:隐藏, 1:显示） */
    visible?: number;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 是否外链（0:否, 1:是） */
    isFrame?: number;
    /** 是否缓存（0:否, 1:是） */
    isCache?: number;
    /** 是否固定标签页（0:否, 1:是） */
    isAffix?: number;
    /** 创建时间 */
    createTime: string;
  }

  /** 菜单树元信息 */
  export interface MenuMeta {
    /** 菜单标题 */
    title?: string;
    /** 菜单图标 */
    icon?: string;
    /** 权限标识 */
    authority?: string;
    /** 排序 */
    order?: number;
    /** 是否固定标签页 */
    affixTab?: boolean;
    /** 是否缓存 */
    keepAlive?: boolean;
    /** 是否在菜单中隐藏 */
    hideInMenu?: boolean;
  }

  /** 菜单树节点 */
  export interface MenuTreeVO {
    /** 菜单ID */
    id: number;
    /** 父菜单ID */
    parentId: number;
    /** 菜单名称 */
    name?: string;
    /** 路由路径 */
    path?: string;
    /** 组件路径 */
    component?: string;
    /** 重定向地址 */
    redirect?: string;
    /** 菜单类型 */
    menuType?: string;
    /** 元信息 */
    meta?: MenuMeta;
    /** 子菜单 */
    children?: MenuTreeVO[];
  }

  /** 创建菜单参数 */
  export interface CreateParams {
    /** 父菜单ID */
    parentId?: number;
    /** 菜单名称 */
    menuName: string;
    /** 菜单类型 */
    menuType?: string;
    /** 路由路径 */
    path?: string;
    /** 路由名称 */
    name?: string;
    /** 组件路径 */
    component?: string;
    /** 重定向地址 */
    redirect?: string;
    /** 权限标识 */
    permission?: string;
    /** 图标 */
    icon?: string;
    /** 排序 */
    sort?: number;
    /** 是否可见 */
    visible?: number;
    /** 状态 */
    status?: number;
    /** 是否外链 */
    isFrame?: number;
    /** 是否缓存 */
    isCache?: number;
    /** 是否固定标签页 */
    isAffix?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新菜单参数 */
  export interface UpdateParams extends CreateParams {
    /** 菜单ID */
    id: number;
  }

  /** 菜单查询参数 */
  export interface QueryParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 菜单类型筛选 */
    menuType?: string;
    /** 状态筛选 */
    status?: number;
  }
}

// ==================== 配置管理相关类型 ====================

export namespace SysConfigApi {
  /** 配置列表项 */
  export interface ConfigVO {
    /** 配置ID */
    id: number;
    /** 配置名称 */
    configName: string;
    /** 配置键 */
    configKey: string;
    /** 配置值 */
    configValue: string;
    /** 值类型 */
    valueType?: string;
    /** 配置类型（0:系统内置,1:业务配置） */
    configType?: number;
    /** 配置分类 */
    category?: string;
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
  }

  /** 创建配置参数 */
  export interface CreateParams {
    /** 配置名称 */
    configName: string;
    /** 配置键 */
    configKey: string;
    /** 配置值 */
    configValue: string;
    /** 值类型 */
    valueType?: string;
    /** 配置类型（0:系统内置,1:业务配置） */
    configType?: number;
    /** 配置分类 */
    category?: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新配置参数 */
  export interface UpdateParams {
    /** 配置ID */
    id: number;
    /** 配置名称 */
    configName?: string;
    /** 配置键 */
    configKey?: string;
    /** 配置值 */
    configValue?: string;
    /** 值类型 */
    valueType?: string;
    /** 配置类型（0:系统内置,1:业务配置） */
    configType?: number;
    /** 配置分类 */
    category?: string;
    /** 排序 */
    sort?: number;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 配置查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 配置类型 */
    configType?: number;
    /** 配置分类 */
    category?: string;
    /** 状态筛选 */
    status?: number;
  }
}

// ==================== 开关管理相关类型 ====================

export namespace SysSwitchApi {
  /** 开关列表项 */
  export interface SwitchVO {
    /** 开关ID */
    id: number;
    /** 开关名称 */
    switchName: string;
    /** 开关键 */
    switchKey: string;
    /** 开关值 */
    switchValue: number;
    /** 开关类型 */
    switchType?: string;
    /** 开关分类 */
    category?: string;
    /** 作用范围 */
    scope?: string;
    /** 生效策略 */
    strategy?: string;
    /** 白名单配置 */
    whitelist?: string;
    /** 灰度百分比 */
    percentage?: number;
    /** 生效开始时间 */
    startTime?: string;
    /** 生效结束时间 */
    endTime?: string;
    /** 开关描述 */
    description?: string;
    /** 开关描述 */
    switchDesc?: string;
    /** 开关分组 */
    switchGroup?: string;
    /** 灰度策略 */
    grayStrategy?: string;
    /** 灰度白名单 */
    grayWhitelist?: string;
    /** 灰度比例 */
    grayPercent?: number;
    /** 生效时间 */
    effectiveTime?: string;
    /** 失效时间 */
    expireTime?: string;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 备注 */
    remark?: string;
    /** 创建时间 */
    createTime: string;
    /** 更新时间 */
    updateTime?: string;
  }

  /** 开关日志 */
  export interface SwitchLogVO {
    /** 日志ID */
    id: number;
    /** 开关ID */
    switchId: number;
    /** 开关名称 */
    switchName?: string;
    /** 旧值 */
    oldValue: number;
    /** 新值 */
    newValue: number;
    /** 变更原因 */
    changeReason?: string;
    /** 操作人ID */
    operatorId?: number;
    /** 操作人姓名 */
    operatorName?: string;
    /** 操作时间 */
    operateTime: string;
  }

  /** 创建开关参数 */
  export interface CreateParams {
    /** 开关名称 */
    switchName: string;
    /** 开关键 */
    switchKey: string;
    /** 开关值 */
    switchValue?: number;
    /** 开关描述 */
    switchDesc?: string;
    /** 开关分组 */
    switchGroup?: string;
    /** 灰度策略 */
    grayStrategy?: string;
    /** 灰度白名单 */
    grayWhitelist?: string;
    /** 灰度比例 */
    grayPercent?: number;
    /** 生效时间 */
    effectiveTime?: string;
    /** 失效时间 */
    expireTime?: string;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新开关参数 */
  export interface UpdateParams {
    /** 开关ID */
    id: number;
    /** 开关名称 */
    switchName?: string;
    /** 开关键 */
    switchKey?: string;
    /** 开关值 */
    switchValue?: number;
    /** 开关描述 */
    switchDesc?: string;
    /** 开关分组 */
    switchGroup?: string;
    /** 灰度策略 */
    grayStrategy?: string;
    /** 灰度白名单 */
    grayWhitelist?: string;
    /** 灰度比例 */
    grayPercent?: number;
    /** 生效时间 */
    effectiveTime?: string;
    /** 失效时间 */
    expireTime?: string;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 开关查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 开关标识 */
    switchKey?: string;
    /** 开关类型 */
    switchType?: string;
    /** 开关分类 */
    category?: string;
    /** 开关状态 */
    switchValue?: number;
  }

  /** 开关日志查询参数 */
  export interface LogQueryParams extends PageParams {
    /** 开关ID */
    switchId?: number;
    /** 开始时间 */
    startTime?: string;
    /** 结束时间 */
    endTime?: string;
  }
}

// ==================== 操作日志相关类型 ====================

export namespace LogOperationApi {
  /** 操作日志列表项 */
  export interface LogVO {
    /** 日志ID */
    id: number;
    /** 操作模块 */
    module?: string;
    /** 操作类型 */
    operation?: string;
    /** 操作描述 */
    description?: string;
    /** 请求方法 */
    method?: string;
    /** 请求URL */
    requestUrl?: string;
    /** 请求参数 */
    requestParams?: string;
    /** 响应数据 */
    responseData?: string;
    /** 操作IP地址 */
    ip?: string;
    /** 用户代理 */
    userAgent?: string;
    /** 操作人ID */
    operatorId?: number;
    /** 操作人姓名 */
    operatorName?: string;
    /** 操作时间 */
    operateTime: string;
    /** 执行耗时（毫秒） */
    duration?: number;
    /** 操作状态（0:失败, 1:成功） */
    status: number;
    /** 错误信息 */
    errorMsg?: string;
  }

  /** 操作日志查询参数 */
  export interface QueryParams extends PageParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 操作模块 */
    module?: string;
    /** 操作类型 */
    operation?: string;
    /** 操作状态 */
    status?: number;
    /** 操作时间-开始 */
    startTime?: string;
    /** 操作时间-结束 */
    endTime?: string;
  }
}

// ==================== 部门相关类型 ====================

export namespace SysDeptApi {
  /** 部门列表项 */
  export interface DeptVO {
    /** 部门ID */
    id: number;
    /** 父部门ID */
    parentId: number;
    /** 部门名称 */
    deptName: string;
    /** 排序 */
    sort?: number;
    /** 负责人 */
    leader?: string;
    /** 联系电话 */
    phone?: string;
    /** 邮箱 */
    email?: string;
    /** 状态（0:禁用, 1:正常） */
    status: number;
    /** 创建时间 */
    createTime: string;
  }

  /** 部门树节点 */
  export interface DeptTreeVO {
    /** 部门ID */
    id: number;
    /** 父部门ID */
    parentId: number;
    /** 部门名称 */
    deptName: string;
    /** 排序 */
    sort?: number;
    /** 负责人 */
    leader?: string;
    /** 状态 */
    status: number;
    /** 子部门 */
    children?: DeptTreeVO[];
  }

  /** 创建部门参数 */
  export interface CreateParams {
    /** 父部门ID */
    parentId?: number;
    /** 部门名称 */
    deptName: string;
    /** 排序 */
    sort?: number;
    /** 负责人 */
    leader?: string;
    /** 联系电话 */
    phone?: string;
    /** 邮箱 */
    email?: string;
    /** 状态 */
    status?: number;
    /** 备注 */
    remark?: string;
  }

  /** 更新部门参数 */
  export interface UpdateParams extends CreateParams {
    /** 部门ID */
    id: number;
  }

  /** 部门查询参数 */
  export interface QueryParams {
    /** 搜索关键词 */
    keyword?: string;
    /** 状态筛选 */
    status?: number;
  }
}
