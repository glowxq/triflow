-- =====================================================
-- Triflow 文本管理模块数据库设计
-- 版本: V1.0.0
-- 模块前缀: cms_ (内容管理模块)
-- 遵循 CONTRIBUTING.md 2.6 数据库表命名规范
-- =====================================================

-- =====================================================
-- 文本分类表 (cms_text_category)
-- 用于文本内容的分类管理
-- =====================================================
CREATE TABLE `cms_text_category`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `pid`           BIGINT       DEFAULT 0 COMMENT '父分类ID（0表示根分类）',
    `category_name` VARCHAR(50)  NOT NULL COMMENT '分类名称',
    `category_key`  VARCHAR(50)  NOT NULL COMMENT '分类标识（唯一，如: agreement, notice, help）',
    `icon`          VARCHAR(100) DEFAULT NULL COMMENT '分类图标',
    `sort`          INT          DEFAULT 0 COMMENT '显示排序',
    `status`        TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
    `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    -- ========== 必需标准字段 ==========
    `tenant_id`     VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`       BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_key` (`category_key`, `tenant_id`, `deleted`),
    KEY             `idx_pid` (`pid`),
    KEY             `idx_status` (`status`),
    KEY             `idx_sort` (`sort`),
    KEY             `idx_tenant_id` (`tenant_id`),
    KEY             `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文本分类表';

-- =====================================================
-- 文本内容表 (cms_text)
-- 用于管理各类文本内容：协议、公告、帮助文档、FAQ等
-- =====================================================
CREATE TABLE `cms_text`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文本ID',
    `category_id`   BIGINT       DEFAULT NULL COMMENT '分类ID',
    `text_key`      VARCHAR(100) NOT NULL COMMENT '文本标识（唯一，如: user_agreement, privacy_policy）',
    `text_title`    VARCHAR(200) NOT NULL COMMENT '文本标题',
    `text_subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
    `text_summary`  VARCHAR(500) DEFAULT NULL COMMENT '摘要/简介',
    `text_content`  LONGTEXT     DEFAULT NULL COMMENT '文本内容（支持富文本HTML）',
    `text_type`     VARCHAR(30)  DEFAULT 'article' COMMENT '文本类型（article:文章, agreement:协议, notice:公告, help:帮助, faq:FAQ, banner:轮播）',
    `content_type`  VARCHAR(20)  DEFAULT 'html' COMMENT '内容格式（html:富文本, markdown:Markdown, text:纯文本）',
    `cover_image`   VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    `author`        VARCHAR(50)  DEFAULT NULL COMMENT '作者',
    `source`        VARCHAR(100) DEFAULT NULL COMMENT '来源',
    `source_url`    VARCHAR(500) DEFAULT NULL COMMENT '原文链接',
    `link_url`      VARCHAR(500) DEFAULT NULL COMMENT '跳转链接（用于Banner等）',
    `keywords`      VARCHAR(200) DEFAULT NULL COMMENT '关键词（SEO用，逗号分隔）',
    `tags`          VARCHAR(200) DEFAULT NULL COMMENT '标签（逗号分隔）',
    `sort`          INT          DEFAULT 0 COMMENT '显示排序',
    `top`           TINYINT      DEFAULT 0 COMMENT '是否置顶（0:否, 1:是）',
    `recommend`     TINYINT      DEFAULT 0 COMMENT '是否推荐（0:否, 1:是）',
    `status`        TINYINT      DEFAULT 0 COMMENT '状态（0:草稿, 1:已发布, 2:已下架）',
    `view_count`    INT          DEFAULT 0 COMMENT '浏览次数',
    `like_count`    INT          DEFAULT 0 COMMENT '点赞次数',
    `publish_time`  DATETIME     DEFAULT NULL COMMENT '发布时间',
    `effective_time` DATETIME    DEFAULT NULL COMMENT '生效时间（定时发布）',
    `expire_time`   DATETIME     DEFAULT NULL COMMENT '失效时间',
    `version`       INT          DEFAULT 1 COMMENT '版本号（用于协议等需要版本管理的文本）',
    `remark`        VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    -- ========== 必需标准字段 ==========
    `tenant_id`     VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`       BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`     BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`     BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`       TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_text_key` (`text_key`, `version`, `tenant_id`, `deleted`),
    KEY             `idx_category_id` (`category_id`),
    KEY             `idx_text_type` (`text_type`),
    KEY             `idx_status` (`status`),
    KEY             `idx_top` (`top`),
    KEY             `idx_recommend` (`recommend`),
    KEY             `idx_publish_time` (`publish_time`),
    KEY             `idx_sort` (`sort`),
    KEY             `idx_tenant_id` (`tenant_id`),
    KEY             `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文本内容表';

-- =====================================================
-- 文本版本历史表 (cms_text_history)
-- 记录文本内容的修改历史，支持版本回溯
-- =====================================================
CREATE TABLE `cms_text_history`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '历史ID',
    `text_id`       BIGINT       NOT NULL COMMENT '文本ID',
    `text_key`      VARCHAR(100) NOT NULL COMMENT '文本标识',
    `text_title`    VARCHAR(200) NOT NULL COMMENT '文本标题',
    `text_content`  LONGTEXT     DEFAULT NULL COMMENT '文本内容',
    `version`       INT          NOT NULL COMMENT '版本号',
    `change_type`   VARCHAR(20)  DEFAULT 'update' COMMENT '变更类型（create:新建, update:更新, publish:发布, rollback:回滚）',
    `change_reason` VARCHAR(500) DEFAULT NULL COMMENT '变更原因',
    `operator_id`   BIGINT       DEFAULT NULL COMMENT '操作人ID',
    `operator_name` VARCHAR(50)  DEFAULT NULL COMMENT '操作人姓名',
    `operate_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `tenant_id`     VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    KEY             `idx_text_id` (`text_id`),
    KEY             `idx_text_key` (`text_key`),
    KEY             `idx_version` (`version`),
    KEY             `idx_operate_time` (`operate_time`),
    KEY             `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文本版本历史表';

-- =====================================================
-- 初始化文本分类数据
-- =====================================================
INSERT INTO `cms_text_category` (`pid`, `category_name`, `category_key`, `sort`, `status`, `remark`)
VALUES
    (0, '协议文档', 'agreement', 1, 1, '用户协议、隐私政策等法律文档'),
    (0, '系统公告', 'notice', 2, 1, '系统公告、通知消息'),
    (0, '帮助中心', 'help', 3, 1, '帮助文档、使用指南'),
    (0, '常见问题', 'faq', 4, 1, 'FAQ常见问题解答'),
    (0, '关于我们', 'about', 5, 1, '公司介绍、联系方式等'),
    (0, '轮播配置', 'banner', 6, 1, '首页轮播图、广告位配置');

-- =====================================================
-- 初始化文本内容数据
-- =====================================================
INSERT INTO `cms_text` (`category_id`, `text_key`, `text_title`, `text_content`, `text_type`, `status`, `version`, `remark`)
VALUES
    -- 协议类
    (1, 'user_agreement', '用户服务协议', '<h2>用户服务协议</h2><p>欢迎使用Triflow平台服务！</p><p>在使用本平台服务之前，请您仔细阅读并充分理解本协议的全部内容...</p>', 'agreement', 1, 1, '平台用户服务协议'),
    (1, 'privacy_policy', '隐私政策', '<h2>隐私政策</h2><p>我们非常重视您的隐私保护。</p><p>本隐私政策旨在向您说明我们如何收集、使用、存储和保护您的个人信息...</p>', 'agreement', 1, 1, '平台隐私政策'),
    (1, 'cookie_policy', 'Cookie政策', '<h2>Cookie政策</h2><p>本网站使用Cookie和类似技术来提供更好的服务...</p>', 'agreement', 1, 1, 'Cookie使用政策'),

    -- 关于我们
    (5, 'about_us', '关于我们', '<h2>关于Triflow</h2><p>Triflow是一个现代化的企业级开发脚手架...</p>', 'article', 1, 1, '平台介绍'),
    (5, 'contact_us', '联系我们', '<h2>联系我们</h2><p>如有任何问题，请通过以下方式联系我们...</p>', 'article', 1, 1, '联系方式');
