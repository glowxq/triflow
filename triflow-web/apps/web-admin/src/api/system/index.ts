/**
 * 系统管理模块 API 导出
 */

// 配置管理 API
export {
  checkConfigKey,
  type ConfigEnumItem,
  type ConfigVersion,
  createConfig,
  deleteConfig,
  deleteConfigBatch,
  downloadConfigTemplate,
  exportConfigList,
  getAllEnabledConfigs,
  getConfigByCategory,
  getConfigById,
  getConfigByKey,
  getConfigCategoryEnums,
  getConfigList,
  getConfigPage,
  getConfigTypeEnums,
  getConfigValueTypeEnums,
  getConfigVersion,
  importConfigData,
  refreshConfigCache,
  updateConfig,
} from './config';

// 操作日志 API
export {
  clearLog,
  deleteLogBatch,
  exportLog,
  getLogById,
  getLogPage,
} from './log';

// 部门管理 API
export {
  createDept,
  deleteDept,
  downloadDeptTemplate,
  exportDept,
  getDeptById,
  getDeptList,
  getDeptTree,
  importDept,
  updateDept,
} from './dept';

// 菜单管理 API
export {
  createMenu,
  deleteMenu,
  downloadMenuTemplate,
  exportMenu,
  getAllPermissions,
  getMenuById,
  getMenuList,
  getMenuTree,
  importMenu,
  updateMenu,
} from './menu';

// 角色管理 API
export {
  assignMenus,
  createRole,
  deleteRole,
  deleteRoleBatch,
  downloadRoleTemplate,
  exportRole,
  getAllRoles,
  getRoleById,
  getRoleList,
  getRoleMenus,
  getRolePage,
  importRole,
  updateRole,
} from './role';

// 开关管理 API
export {
  checkSwitchKey,
  compareSwitchWithEnum,
  createSwitch,
  deleteNotInEnum,
  deleteSwitch,
  deleteSwitchBatch,
  type EnumItem,
  type EnumSwitchItem,
  getAllEnabledSwitches,
  getSwitchByCategory,
  getSwitchById,
  getSwitchByKey,
  getSwitchByType,
  getSwitchCategoryEnums,
  getSwitchKeyEnums,
  getSwitchList,
  getSwitchLogPage,
  getSwitchLogs,
  getSwitchPage,
  getSwitchScopeEnums,
  getSwitchStrategyEnums,
  getSwitchTypeEnums,
  initFromEnum,
  isEnabled,
  refreshSwitchCache,
  type SwitchEnumCompareResult,
  toggleSwitch,
  updateSwitch,
} from './switch';

// 类型导出
export type {
  LogOperationApi,
  PageParams,
  PageResult,
  SysConfigApi,
  SysDeptApi,
  SysMenuApi,
  SysRoleApi,
  SysSwitchApi,
  SysUserApi,
} from './types';

// 用户管理 API
export {
  assignRoles,
  batchUpdateDataScope,
  createUser,
  deleteUser,
  deleteUserBatch,
  downloadUserTemplate,
  exportUser,
  getUserById,
  getUserList,
  getUserPage,
  getUserSocials,
  importUser,
  kickoutUser,
  kickoutUserBatch,
  resetPassword,
  unbindSocial,
  updateUser,
} from './user';
