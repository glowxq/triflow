-- =====================================================
-- Triflow 系统开关管理模块数据库设计
-- 版本: V1.0.0
-- 模块前缀: sys_ (系统模块)
-- 遵循 CONTRIBUTING.md 2.6 数据库表命名规范
-- =====================================================

-- =====================================================
-- 系统开关表 (sys_switch)
-- 用于管理系统功能的开关状态，支持灰度发布、功能降级
-- =====================================================
CREATE TABLE `sys_switch`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '开关ID',
    `switch_name`  VARCHAR(100) NOT NULL COMMENT '开关名称',
    `switch_key`   VARCHAR(100) NOT NULL COMMENT '开关键（唯一标识，如: feature.newUI.enabled）',
    `switch_value` TINYINT      DEFAULT 1 COMMENT '开关状态（0:关闭, 1:开启）',
    `switch_type`  VARCHAR(30)  DEFAULT 'feature' COMMENT '开关类型（feature:功能开关, gray:灰度开关, degrade:降级开关, experiment:实验开关）',
    `category`     VARCHAR(50)  DEFAULT 'default' COMMENT '开关分类（用于分组，如: user, order, payment）',
    `scope`        VARCHAR(30)  DEFAULT 'global' COMMENT '作用范围（global:全局, tenant:租户级, user:用户级）',
    `strategy`     VARCHAR(30)  DEFAULT 'all' COMMENT '生效策略（all:全部, whitelist:白名单, percentage:百分比, schedule:定时）',
    `whitelist`    JSON         DEFAULT NULL COMMENT '白名单配置（用户ID或租户ID列表）',
    `percentage`   INT          DEFAULT 100 COMMENT '灰度百分比（0-100，strategy=percentage时生效）',
    `start_time`   DATETIME     DEFAULT NULL COMMENT '生效开始时间（strategy=schedule时生效）',
    `end_time`     DATETIME     DEFAULT NULL COMMENT '生效结束时间（strategy=schedule时生效）',
    `sort`         INT          DEFAULT 0 COMMENT '显示排序',
    `description`  VARCHAR(500) DEFAULT NULL COMMENT '开关描述',
    `remark`       VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    -- ========== 必需标准字段 ==========
    `tenant_id`    VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`      BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`    BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_switch_key` (`switch_key`, `tenant_id`, `deleted`),
    KEY            `idx_switch_type` (`switch_type`),
    KEY            `idx_category` (`category`),
    KEY            `idx_switch_value` (`switch_value`),
    KEY            `idx_tenant_id` (`tenant_id`),
    KEY            `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统开关表';

-- =====================================================
-- 开关操作日志表 (sys_switch_log)
-- 记录开关状态变更历史，便于审计和回溯
-- =====================================================
CREATE TABLE `sys_switch_log`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `switch_id`    BIGINT       NOT NULL COMMENT '开关ID',
    `switch_key`   VARCHAR(100) NOT NULL COMMENT '开关键',
    `old_value`    TINYINT      DEFAULT NULL COMMENT '变更前状态',
    `new_value`    TINYINT      NOT NULL COMMENT '变更后状态',
    `old_config`   JSON         DEFAULT NULL COMMENT '变更前配置（JSON格式）',
    `new_config`   JSON         DEFAULT NULL COMMENT '变更后配置（JSON格式）',
    `change_type`  VARCHAR(20)  DEFAULT 'manual' COMMENT '变更类型（manual:手动, schedule:定时, auto:自动）',
    `change_reason` VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    `operator_id`  BIGINT       DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50) DEFAULT NULL COMMENT '操作人姓名',
    `operate_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `tenant_id`    VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    KEY            `idx_switch_id` (`switch_id`),
    KEY            `idx_switch_key` (`switch_key`),
    KEY            `idx_operate_time` (`operate_time`),
    KEY            `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='开关操作日志表';

-- =====================================================
-- 初始化系统开关数据
-- =====================================================
INSERT INTO `sys_switch` (`switch_name`, `switch_key`, `switch_value`, `switch_type`, `category`, `scope`, `strategy`, `description`)
VALUES
    -- 用户模块开关
    ('用户注册开关', 'user.register.enabled', 1, 'feature', 'user', 'global', 'all', '是否允许新用户注册'),
    ('手机号登录开关', 'user.login.phone.enabled', 1, 'feature', 'user', 'global', 'all', '是否允许手机号验证码登录'),
    ('第三方登录开关', 'user.login.social.enabled', 1, 'feature', 'user', 'global', 'all', '是否允许第三方账号登录'),
    ('密码找回开关', 'user.password.reset.enabled', 1, 'feature', 'user', 'global', 'all', '是否允许找回密码功能'),

    -- 安全模块开关
    ('验证码开关', 'security.captcha.enabled', 0, 'feature', 'security', 'global', 'all', '是否开启登录验证码'),
    ('接口防重放开关', 'security.replay.enabled', 1, 'feature', 'security', 'global', 'all', '是否开启接口防重放校验'),
    ('接口限流开关', 'security.rateLimit.enabled', 1, 'feature', 'security', 'global', 'all', '是否开启接口限流'),
    ('敏感词过滤开关', 'security.sensitiveWord.enabled', 1, 'feature', 'security', 'global', 'all', '是否开启敏感词过滤'),

    -- 系统功能开关
    ('系统维护模式', 'system.maintenance.enabled', 0, 'degrade', 'system', 'global', 'all', '系统维护模式，开启后仅管理员可访问'),
    ('API文档开关', 'system.apidoc.enabled', 1, 'feature', 'system', 'global', 'all', '是否开启API文档（生产环境建议关闭）'),
    ('操作日志开关', 'system.operateLog.enabled', 1, 'feature', 'system', 'global', 'all', '是否记录操作日志'),
    ('登录日志开关', 'system.loginLog.enabled', 1, 'feature', 'system', 'global', 'all', '是否记录登录日志'),

    -- 通知模块开关
    ('短信通知开关', 'notify.sms.enabled', 1, 'feature', 'notify', 'global', 'all', '是否开启短信通知'),
    ('邮件通知开关', 'notify.email.enabled', 1, 'feature', 'notify', 'global', 'all', '是否开启邮件通知'),
    ('微信通知开关', 'notify.wechat.enabled', 1, 'feature', 'notify', 'global', 'all', '是否开启微信模板消息通知');
