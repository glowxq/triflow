-- =====================================================
-- Triflow 系统权限模块数据库设计
-- 版本: V1.0.0
-- 模块前缀: sys_ (系统模块)
-- 遵循 CONTRIBUTING.md 2.6 数据库表命名规范
-- =====================================================

-- =====================================================
-- 1. 部门表 (sys_dept)
-- =====================================================
CREATE TABLE `sys_dept`
(
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `pid`         BIGINT       DEFAULT 0 COMMENT '父部门ID（0表示根部门）',
  `ancestors`   VARCHAR(500) DEFAULT '' COMMENT '祖级列表（逗号分隔，如: 0,1,2）',
  `name`        VARCHAR(50) NOT NULL COMMENT '部门名称',
  `sort`        INT          DEFAULT 0 COMMENT '显示排序',
  `leader`      VARCHAR(50)  DEFAULT NULL COMMENT '负责人',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- ========== 必需标准字段 ==========
  `tenant_id`   VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
  `dept_id`     BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者ID',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者ID',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY           `idx_pid` (`pid`),
  KEY           `idx_status` (`status`),
  KEY           `idx_tenant_id` (`tenant_id`),
  KEY           `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='部门表';

-- =====================================================
-- 2. 用户表 (sys_user)
-- 支持多种登录方式：账号密码、手机号、邮箱、第三方登录
-- =====================================================
CREATE TABLE `sys_user`
(
  `id`              BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  -- ========== 基本信息 ==========
  `username`        VARCHAR(50)  DEFAULT NULL COMMENT '用户名（登录账号，可为空支持纯手机号/邮箱登录）',
  `password`        VARCHAR(100) DEFAULT NULL COMMENT '密码（加密存储，第三方登录可为空）',
  `nickname`        VARCHAR(50)  DEFAULT NULL COMMENT '用户昵称',
  `real_name`       VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
  `avatar`          VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `gender`          TINYINT      DEFAULT 0 COMMENT '性别（0:未知, 1:男, 2:女）',
  -- ========== 联系方式（支持多种登录） ==========
  `phone`           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `phone_verified`  TINYINT      DEFAULT 0 COMMENT '手机号是否验证（0:未验证, 1:已验证）',
  `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `email_verified`  TINYINT      DEFAULT 0 COMMENT '邮箱是否验证（0:未验证, 1:已验证）',
  -- ========== 组织信息 ==========
  `user_dept_id`    BIGINT       DEFAULT NULL COMMENT '所属部门ID',
  `home_path`       VARCHAR(200) DEFAULT NULL COMMENT '登录后首页路径',
  -- ========== 状态信息 ==========
  `status`          TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常, 2:锁定）',
  `user_type`       TINYINT      DEFAULT 0 COMMENT '用户类型（0:普通用户, 1:系统用户）',
  `register_source` VARCHAR(20) DEFAULT 'username' COMMENT '注册来源（username:账号注册, phone:手机号, wechat:微信, apple:苹果, google:谷歌）',
  -- ========== 登录信息 ==========
  `login_ip`        VARCHAR(50)  DEFAULT NULL COMMENT '最后登录IP',
  `login_time`      DATETIME     DEFAULT NULL COMMENT '最后登录时间',
  `login_count`     INT          DEFAULT 0 COMMENT '登录次数',
  `pwd_error_count` INT          DEFAULT 0 COMMENT '密码错误次数（用于账号锁定）',
  `pwd_error_time`  DATETIME     DEFAULT NULL COMMENT '最后密码错误时间',
  `pwd_update_time` DATETIME     DEFAULT NULL COMMENT '密码最后修改时间',
  `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- ========== 必需标准字段 ==========
  `tenant_id`       VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
  `dept_id`         BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by`       BIGINT       DEFAULT NULL COMMENT '创建者ID',
  `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`       BIGINT       DEFAULT NULL COMMENT '更新者ID',
  `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`         TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`, `tenant_id`, `deleted`),
  UNIQUE KEY `uk_phone` (`phone`, `tenant_id`, `deleted`),
  UNIQUE KEY `uk_email` (`email`, `tenant_id`, `deleted`),
  KEY               `idx_user_dept_id` (`user_dept_id`),
  KEY               `idx_status` (`status`),
  KEY               `idx_tenant_id` (`tenant_id`),
  KEY               `idx_dept_id` (`dept_id`),
  KEY               `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- =====================================================
-- 3. 用户第三方账号关联表 (sys_user_social)
-- 支持微信（公众号/小程序/开放平台）、QQ、GitHub、Apple等
-- =====================================================
CREATE TABLE `sys_user_social`
(
  `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
  `social_type`   VARCHAR(30)  NOT NULL COMMENT '第三方平台类型（wechat_mp:公众号, wechat_miniapp:小程序, wechat_open:开放平台, qq, github, apple, google, dingtalk, weibo）',
  `social_id`     VARCHAR(100) NOT NULL COMMENT '第三方平台用户ID（openid）',
  `union_id`      VARCHAR(100) DEFAULT NULL COMMENT '第三方平台统一ID（微信unionid，用于关联同一用户不同应用）',
  `access_token`  VARCHAR(500) DEFAULT NULL COMMENT '访问令牌（加密存储）',
  `refresh_token` VARCHAR(500) DEFAULT NULL COMMENT '刷新令牌（加密存储）',
  `expire_time`   DATETIME     DEFAULT NULL COMMENT '令牌过期时间',
  `nickname`      VARCHAR(100) DEFAULT NULL COMMENT '第三方平台昵称',
  `avatar`        VARCHAR(500) DEFAULT NULL COMMENT '第三方平台头像',
  `raw_data`      JSON         DEFAULT NULL COMMENT '原始用户数据（JSON格式）',
  `bind_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `tenant_id`     VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_social` (`social_type`, `social_id`, `tenant_id`),
  KEY             `idx_user_id` (`user_id`),
  KEY             `idx_union_id` (`union_id`),
  KEY             `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户第三方账号关联表';

-- =====================================================
-- 4. 角色表 (sys_role)
-- =====================================================
CREATE TABLE `sys_role`
(
  `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `code`        VARCHAR(50) NOT NULL COMMENT '角色编码（唯一标识，如: admin, user）',
  `name`        VARCHAR(50) NOT NULL COMMENT '角色名称',
  `sort`        INT          DEFAULT 0 COMMENT '显示排序',
  `status`      TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
  `data_scope`  TINYINT      DEFAULT 1 COMMENT '数据范围（1:全部, 2:自定义, 3:本部门, 4:本部门及以下, 5:仅本人）',
  `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- ========== 必需标准字段 ==========
  `tenant_id`   VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
  `dept_id`     BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者ID',
  `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者ID',
  `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`, `tenant_id`, `deleted`),
  KEY           `idx_status` (`status`),
  KEY           `idx_tenant_id` (`tenant_id`),
  KEY           `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色表';

-- =====================================================
-- 5. 菜单表 (sys_menu)
-- 对应前端 MenuRecordRaw 和 RouteMeta
-- =====================================================
CREATE TABLE `sys_menu`
(
  `id`                    BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `pid`                   BIGINT       DEFAULT 0 COMMENT '父菜单ID（0表示根菜单）',
  `name`                  VARCHAR(100) NOT NULL COMMENT '菜单名称（路由name，唯一）',
  `title`                 VARCHAR(100) DEFAULT NULL COMMENT '菜单标题（显示名称/国际化key）',
  `path`                  VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  `component`             VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
  `redirect`              VARCHAR(200) DEFAULT NULL COMMENT '重定向地址',
  `type`                  VARCHAR(20)  DEFAULT 'menu' COMMENT '菜单类型（catalog:目录, menu:菜单, button:按钮, embedded:内嵌, link:外链）',
  `icon`                  VARCHAR(100) DEFAULT NULL COMMENT '菜单图标',
  `active_icon`           VARCHAR(100) DEFAULT NULL COMMENT '激活时图标',
  `auth_code`             VARCHAR(100) DEFAULT NULL COMMENT '权限标识（如: System:User:List）',
  `sort`                  INT          DEFAULT 0 COMMENT '显示排序',
  `status`                TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
  `visible`   TINYINT DEFAULT 1 COMMENT '是否可见（0:隐藏, 1:显示）',
  `cacheable` TINYINT DEFAULT 1 COMMENT '是否缓存（0:不缓存, 1:缓存）',
  `affix_tab` TINYINT DEFAULT 0 COMMENT '是否固定标签页（0:否, 1:是）',
  `external`  TINYINT DEFAULT 0 COMMENT '是否外链（0:否, 1:是）',
  `link_url`              VARCHAR(500) DEFAULT NULL COMMENT '外链地址',
  -- ========== 徽标相关（对应前端 MenuRecordBadgeRaw）==========
  `badge`                 VARCHAR(50)  DEFAULT NULL COMMENT '徽标内容',
  `badge_type`            VARCHAR(20)  DEFAULT NULL COMMENT '徽标类型（dot:小圆点, normal:普通）',
  `badge_variant`         VARCHAR(20)  DEFAULT NULL COMMENT '徽标颜色变体（primary, destructive等）',
  -- ========== Meta 扩展字段 ==========
  `hide_in_menu`          TINYINT      DEFAULT 0 COMMENT '是否在菜单中隐藏（0:否, 1:是）',
  `hide_in_tab`           TINYINT      DEFAULT 0 COMMENT '是否在标签页隐藏（0:否, 1:是）',
  `hide_in_breadcrumb`    TINYINT      DEFAULT 0 COMMENT '是否在面包屑隐藏（0:否, 1:是）',
  `hide_children_in_menu` TINYINT      DEFAULT 0 COMMENT '是否隐藏子菜单（0:否, 1:是）',
  `ignore_access`         TINYINT      DEFAULT 0 COMMENT '是否忽略权限检查（0:否, 1:是）',
  `remark`                VARCHAR(500) DEFAULT NULL COMMENT '备注',
  -- ========== 必需标准字段 ==========
  `tenant_id`             VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
  `dept_id`               BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by`             BIGINT       DEFAULT NULL COMMENT '创建者ID',
  `create_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by`             BIGINT       DEFAULT NULL COMMENT '更新者ID',
  `update_time`           DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted`               TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`, `tenant_id`, `deleted`),
  KEY                     `idx_pid` (`pid`),
  KEY                     `idx_type` (`type`),
  KEY                     `idx_status` (`status`),
  KEY                     `idx_sort` (`sort`),
  KEY                     `idx_tenant_id` (`tenant_id`),
  KEY                     `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='菜单权限表';

-- =====================================================
-- 6. 用户-角色关联表 (sys_user_role)
-- =====================================================
CREATE TABLE `sys_user_role`
(
  `id`        BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id`   BIGINT NOT NULL COMMENT '用户ID',
  `role_id`   BIGINT NOT NULL COMMENT '角色ID',
  `tenant_id` VARCHAR(50) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY         `idx_user_id` (`user_id`),
  KEY         `idx_role_id` (`role_id`),
  KEY         `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户角色关联表';

-- =====================================================
-- 7. 角色-菜单关联表 (sys_role_menu)
-- =====================================================
CREATE TABLE `sys_role_menu`
(
  `id`        BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`   BIGINT NOT NULL COMMENT '角色ID',
  `menu_id`   BIGINT NOT NULL COMMENT '菜单ID',
  `tenant_id` VARCHAR(50) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`),
  KEY         `idx_role_id` (`role_id`),
  KEY         `idx_menu_id` (`menu_id`),
  KEY         `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色菜单关联表';

-- =====================================================
-- 8. 角色-部门关联表 (sys_role_dept)
-- 用于自定义数据权限范围
-- =====================================================
CREATE TABLE `sys_role_dept`
(
  `id`        BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id`   BIGINT NOT NULL COMMENT '角色ID',
  `dept_id`   BIGINT NOT NULL COMMENT '部门ID',
  `tenant_id` VARCHAR(50) DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_dept` (`role_id`, `dept_id`),
  KEY         `idx_role_id` (`role_id`),
  KEY         `idx_dept_id` (`dept_id`),
  KEY         `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='角色部门关联表';
