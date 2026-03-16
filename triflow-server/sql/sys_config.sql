-- =====================================================
-- Triflow 系统配置管理模块数据库设计
-- 版本: V1.0.0
-- 模块前缀: sys_ (系统模块)
-- 遵循 CONTRIBUTING.md 2.6 数据库表命名规范
-- =====================================================

-- =====================================================
-- 系统配置表 (sys_config)
-- 用于存储系统级和业务级的配置项，支持动态配置
-- =====================================================
CREATE TABLE `sys_config`
(
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_name`  VARCHAR(100) NOT NULL COMMENT '配置名称',
    `config_key`   VARCHAR(100) NOT NULL COMMENT '配置键（唯一标识，如: sys.user.initPassword）',
    `config_value` TEXT         DEFAULT NULL COMMENT '配置值（支持JSON格式）',
    `value_type`   VARCHAR(20)  DEFAULT 'string' COMMENT '值类型（string:字符串, number:数字, boolean:布尔, json:JSON对象, array:数组）',
    `config_type`  TINYINT      DEFAULT 1 COMMENT '配置类型（0:系统内置-不可删除, 1:业务配置-可删除）',
    `category`     VARCHAR(50)  DEFAULT 'default' COMMENT '配置分类（用于分组显示，如: security, upload, email）',
    `sort`         INT          DEFAULT 0 COMMENT '显示排序',
    `status`       TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
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
    UNIQUE KEY `uk_config_key` (`config_key`, `tenant_id`, `deleted`),
    KEY            `idx_config_type` (`config_type`),
    KEY            `idx_category` (`category`),
    KEY            `idx_status` (`status`),
    KEY            `idx_tenant_id` (`tenant_id`),
    KEY            `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='系统配置表';

-- =====================================================
-- 初始化系统配置数据
-- =====================================================
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `value_type`, `config_type`, `category`, `sort`, `remark`)
VALUES
    -- 用户相关配置
    ('用户初始密码', 'sys.user.initPassword', '123456', 'string', 0, 'user', 1, '新用户默认密码'),
    ('密码最小长度', 'sys.user.password.minLength', '6', 'number', 0, 'user', 2, '密码最小长度限制'),
    ('密码错误锁定次数', 'sys.user.password.lockCount', '5', 'number', 0, 'user', 3, '密码错误超过此次数将锁定账户'),
    ('账户锁定时长(分钟)', 'sys.user.lockDuration', '30', 'number', 0, 'user', 4, '账户锁定后的自动解锁时长'),

    -- 安全相关配置
    ('验证码开关', 'sys.captcha.enabled', 'true', 'boolean', 0, 'security', 10, '是否开启登录验证码'),
    ('验证码类型', 'sys.captcha.type', 'math', 'string', 0, 'security', 11, '验证码类型（math:数学运算, char:字符）'),
    ('Token有效期(分钟)', 'sys.token.expireTime', '1440', 'number', 0, 'security', 12, 'JWT Token有效期，默认24小时'),
    ('允许同时登录', 'sys.user.multiLogin', 'true', 'boolean', 0, 'security', 13, '是否允许同一账号多设备同时登录'),

    -- 上传相关配置
    ('文件上传大小限制(MB)', 'sys.upload.maxSize', '50', 'number', 0, 'upload', 20, '单个文件上传大小限制'),
    ('允许上传的文件类型', 'sys.upload.allowedTypes', '["jpg","jpeg","png","gif","bmp","doc","docx","xls","xlsx","ppt","pptx","pdf","txt","zip","rar"]', 'array', 0, 'upload', 21, '允许上传的文件扩展名列表'),
    ('图片上传大小限制(MB)', 'sys.upload.image.maxSize', '10', 'number', 0, 'upload', 22, '图片文件上传大小限制'),

    -- 系统相关配置
    ('系统名称', 'sys.app.name', 'Triflow', 'string', 0, 'system', 30, '系统名称'),
    ('系统版本', 'sys.app.version', '1.0.0', 'string', 0, 'system', 31, '系统版本号'),
    ('版权信息', 'sys.app.copyright', 'Copyright © 2025 Triflow', 'string', 0, 'system', 32, '系统版权信息');
