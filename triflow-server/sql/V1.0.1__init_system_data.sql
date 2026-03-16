-- =====================================================
-- Triflow 系统权限模块初始化数据
-- 版本: V1.0.1
-- =====================================================

-- =====================================================
-- 1. 部门初始数据
-- =====================================================
INSERT INTO `sys_dept` (`id`, `pid`, `ancestors`, `name`, `sort`, `status`)
VALUES (1, 0, '0', 'Triflow科技', 0, 1),
       (2, 1, '0,1', '研发部门', 1, 1),
       (3, 1, '0,1', '市场部门', 2, 1),
       (4, 2, '0,1,2', '前端组', 1, 1),
       (5, 2, '0,1,2', '后端组', 2, 1);

-- =====================================================
-- 2. 角色初始数据
-- =====================================================
INSERT INTO `sys_role` (`id`, `code`, `name`, `sort`, `status`, `data_scope`, `remark`)
VALUES (1, 'super', '超级管理员', 1, 1, 1, '拥有所有权限'),
       (2, 'admin', '管理员', 2, 1, 2, '拥有大部分权限'),
       (3, 'user', '普通用户', 3, 1, 5, '基础权限');

-- =====================================================
-- 3. 用户初始数据
-- 密码: 123456 (BCrypt加密)
-- =====================================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `real_name`, `phone`, `phone_verified`, `email`,
                        `email_verified`, `status`, `user_type`, `register_source`, `user_dept_id`)
VALUES (1, 'vben', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Vben', 'Vben Admin', '13800000001',
        1, 'vben@triflow.com', 1, 1, 1, 'username', 1),
       (2, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Admin', '管理员', '13800000002', 1,
        'admin@triflow.com', 1, 1, 1, 'username', 2),
       (3, 'jack', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'Jack', '测试用户', '13800000003', 1,
        'jack@triflow.com', 0, 1, 0, 'username', 4);

-- =====================================================
-- 4. 用户角色关联
-- =====================================================
INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES (1, 1), -- vben -> super
       (2, 2), -- admin -> admin
       (3, 3);
-- jack -> user

-- =====================================================
-- 5. 菜单初始数据
-- type: catalog(目录), menu(菜单), button(按钮), embedded(内嵌), link(外链)
-- =====================================================

-- 工作台
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`, `affix_tab`)
VALUES (1, 0, 'Workspace', 'page.dashboard.workspace', '/workspace', '/dashboard/workspace/index', 'menu',
        'carbon:workspace', NULL, 0, 1, 1);

-- ========== 系统管理 ==========
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`, `badge`, `badge_type`, `badge_variant`)
VALUES (100, 0, 'System', 'system.title', '/system', NULL, 'catalog', 'carbon:settings', NULL, 9997, 1, NULL, NULL,
        NULL);

-- 用户管理
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`)
VALUES (101, 100, 'SystemUser', 'system.user.title', '/system/user', '/system/user/list', 'menu', 'carbon:user',
        'System:User:List', 1, 1),
       (10101, 101, 'SystemUserCreate', 'common.create', NULL, NULL, 'button', NULL, 'System:User:Create', 1, 1),
       (10102, 101, 'SystemUserUpdate', 'common.update', NULL, NULL, 'button', NULL, 'System:User:Update', 2, 1),
       (10103, 101, 'SystemUserDelete', 'common.delete', NULL, NULL, 'button', NULL, 'System:User:Delete', 3, 1),
       (10104, 101, 'SystemUserExport', 'common.export', NULL, NULL, 'button', NULL, 'System:User:Export', 4, 1),
       (10105, 101, 'SystemUserImport', 'common.import', NULL, NULL, 'button', NULL, 'System:User:Import', 5, 1),
       (10106, 101, 'SystemUserResetPwd', 'system.user.resetPwd', NULL, NULL, 'button', NULL, 'System:User:ResetPwd', 6,
        1);

-- 角色管理
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`)
VALUES (102, 100, 'SystemRole', 'system.role.title', '/system/role', '/system/role/list', 'menu', 'carbon:user-role',
        'System:Role:List', 2, 1),
       (10201, 102, 'SystemRoleCreate', 'common.create', NULL, NULL, 'button', NULL, 'System:Role:Create', 1, 1),
       (10202, 102, 'SystemRoleUpdate', 'common.update', NULL, NULL, 'button', NULL, 'System:Role:Update', 2, 1),
       (10203, 102, 'SystemRoleDelete', 'common.delete', NULL, NULL, 'button', NULL, 'System:Role:Delete', 3, 1),
       (10204, 102, 'SystemRoleAssign', 'system.role.assign', NULL, NULL, 'button', NULL, 'System:Role:Assign', 4, 1);

-- 菜单管理
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`)
VALUES (103, 100, 'SystemMenu', 'system.menu.title', '/system/menu', '/system/menu/list', 'menu', 'carbon:menu',
        'System:Menu:List', 3, 1),
       (10301, 103, 'SystemMenuCreate', 'common.create', NULL, NULL, 'button', NULL, 'System:Menu:Create', 1, 1),
       (10302, 103, 'SystemMenuUpdate', 'common.update', NULL, NULL, 'button', NULL, 'System:Menu:Update', 2, 1),
       (10303, 103, 'SystemMenuDelete', 'common.delete', NULL, NULL, 'button', NULL, 'System:Menu:Delete', 3, 1);

-- 部门管理
INSERT INTO `sys_menu` (`id`, `pid`, `name`, `title`, `path`, `component`, `type`, `icon`, `auth_code`, `sort`,
                        `status`)
VALUES (104, 100, 'SystemDept', 'system.dept.title', '/system/dept', '/system/dept/list', 'menu', 'carbon:tree-view',
        'System:Dept:List', 4, 1),
       (10401, 104, 'SystemDeptCreate', 'common.create', NULL, NULL, 'button', NULL, 'System:Dept:Create', 1, 1),
       (10402, 104, 'SystemDeptUpdate', 'common.update', NULL, NULL, 'button', NULL, 'System:Dept:Update', 2, 1),
       (10403, 104, 'SystemDeptDelete', 'common.delete', NULL, NULL, 'button', NULL, 'System:Dept:Delete', 3, 1);

-- =====================================================
-- 6. 角色菜单关联
-- =====================================================

-- 超级管理员拥有所有权限
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id
FROM `sys_menu`;

-- 管理员权限（不含删除权限）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (2, 1),     -- 工作台
       (2, 100),   -- 系统管理
       (2, 101),   -- 用户管理
       (2, 10101), -- 用户-新增
       (2, 10102), -- 用户-修改
       (2, 10104), -- 用户-导出
       (2, 10105), -- 用户-导入
       (2, 102),   -- 角色管理
       (2, 10201), -- 角色-新增
       (2, 10202), -- 角色-修改
       (2, 10204), -- 角色-分配
       (2, 103),   -- 菜单管理
       (2, 10301), -- 菜单-新增
       (2, 10302), -- 菜单-修改
       (2, 104),   -- 部门管理
       (2, 10401), -- 部门-新增
       (2, 10402);
-- 部门-修改

-- 普通用户权限（仅查看）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
VALUES (3, 1),   -- 工作台
       (3, 100), -- 系统管理
       (3, 101), -- 用户管理（仅查看）
       (3, 102), -- 角色管理（仅查看）
       (3, 103), -- 菜单管理（仅查看）
       (3, 104); -- 部门管理（仅查看）
