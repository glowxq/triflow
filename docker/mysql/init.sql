-- =====================================================
-- Triflow 数据库初始化脚本
-- 用途: Docker 首次启动时自动初始化数据库
-- 说明: 包含所有表结构和基础数据 (菜单/角色/用户等)
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for `ai_call_log`
-- ----------------------------
DROP TABLE IF EXISTS `ai_call_log`;
CREATE TABLE `ai_call_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
  `provider` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'AI 提供商',
  `model` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '使用的模型',
  `system_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '系统提示',
  `user_message` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户消息',
  `ai_response` mediumtext COLLATE utf8mb4_unicode_ci COMMENT 'AI 响应内容',
  `prompt_tokens` int DEFAULT '0' COMMENT '提示 token 数',
  `completion_tokens` int DEFAULT '0' COMMENT '完成 token 数',
  `total_tokens` int DEFAULT '0' COMMENT '总 token 数',
  `duration` bigint DEFAULT '0' COMMENT '耗时 (毫秒)',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 1-成功, 0-失败',
  `error_message` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求IP',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_provider` (`provider`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 调用记录表';


-- ----------------------------
-- Records of `ai_call_log`
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_text`
-- ----------------------------
DROP TABLE IF EXISTS `cms_text`;
CREATE TABLE `cms_text` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文本ID',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `text_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文本标识（唯一，如: user_agreement, privacy_policy）',
  `text_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文本标题',
  `text_subtitle` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '副标题',
  `text_summary` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '摘要/简介',
  `text_content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文本内容（支持富文本HTML）',
  `text_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'article' COMMENT '文本类型（article:文章, agreement:协议, notice:公告, help:帮助, faq:FAQ, banner:轮播）',
  `content_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'html' COMMENT '内容格式（html:富文本, markdown:Markdown, text:纯文本）',
  `cover_image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片URL',
  `author` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '作者',
  `source` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源',
  `source_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原文链接',
  `link_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转链接（用于Banner等）',
  `keywords` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关键词（SEO用，逗号分隔）',
  `tags` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签（逗号分隔）',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `top` tinyint DEFAULT '0' COMMENT '是否置顶（0:否, 1:是）',
  `recommend` tinyint DEFAULT '0' COMMENT '是否推荐（0:否, 1:是）',
  `status` tinyint DEFAULT '0' COMMENT '状态（0:草稿, 1:已发布, 2:已下架）',
  `view_count` int DEFAULT '0' COMMENT '浏览次数',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `effective_time` datetime DEFAULT NULL COMMENT '生效时间（定时发布）',
  `expire_time` datetime DEFAULT NULL COMMENT '失效时间',
  `version` int DEFAULT '1' COMMENT '版本号（用于协议等需要版本管理的文本）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_text_key` (`text_key`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_text_type` (`text_type`),
  KEY `idx_status` (`status`),
  KEY `idx_top` (`top`),
  KEY `idx_recommend` (`recommend`),
  KEY `idx_publish_time` (`publish_time`),
  KEY `idx_sort` (`sort`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文本内容表';


-- ----------------------------
-- Records of `cms_text`
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_text_category`
-- ----------------------------
DROP TABLE IF EXISTS `cms_text_category`;
CREATE TABLE `cms_text_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `pid` bigint DEFAULT '0' COMMENT '父分类ID（0表示根分类）',
  `category_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `category_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类标识（唯一，如: agreement, notice, help）',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分类图标',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_key` (`category_key`),
  KEY `idx_pid` (`pid`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文本分类表';


-- ----------------------------
-- Records of `cms_text_category`
-- ----------------------------

-- ----------------------------
-- Table structure for `cms_text_history`
-- ----------------------------
DROP TABLE IF EXISTS `cms_text_history`;
CREATE TABLE `cms_text_history` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '历史ID',
  `text_id` bigint NOT NULL COMMENT '文本ID',
  `text_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文本标识',
  `text_title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文本标题',
  `text_content` longtext COLLATE utf8mb4_unicode_ci COMMENT '文本内容',
  `version` int NOT NULL COMMENT '版本号',
  `change_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'update' COMMENT '变更类型（create:新建, update:更新, publish:发布, rollback:回滚）',
  `change_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更原因',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_text_id` (`text_id`),
  KEY `idx_text_key` (`text_key`),
  KEY `idx_version` (`version`),
  KEY `idx_operate_time` (`operate_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文本版本历史表';


-- ----------------------------
-- Table structure for `file_chunk`
-- ----------------------------
DROP TABLE IF EXISTS `file_chunk`;
CREATE TABLE `file_chunk` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分片ID',
  `upload_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上传任务ID（唯一标识一次分片上传）',
  `file_md5` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '完整文件MD5',
  `file_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名',
  `file_size` bigint NOT NULL COMMENT '文件总大小（字节）',
  `chunk_size` int NOT NULL COMMENT '分片大小（字节）',
  `chunk_count` int NOT NULL COMMENT '分片总数',
  `chunk_index` int NOT NULL COMMENT '当前分片索引（从0开始）',
  `chunk_md5` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分片MD5',
  `chunk_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分片存储路径',
  `storage_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'local' COMMENT '存储类型',
  `bucket_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储桶名称',
  `status` tinyint DEFAULT '0' COMMENT '状态（0:上传中, 1:已完成, 2:已合并, 3:失败）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时分片的清理时间）',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_upload_chunk` (`upload_id`,`chunk_index`),
  KEY `idx_file_md5` (`file_md5`),
  KEY `idx_upload_id` (`upload_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件分片上传记录表';


-- ----------------------------
-- Table structure for `file_info`
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `config_id` bigint DEFAULT NULL COMMENT '存储配置ID',
  `file_name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名（存储名，如: 20250124/abc123.jpg）',
  `original_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名（上传时的文件名）',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件路径（完整的存储路径）',
  `file_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件访问URL',
  `file_size` bigint DEFAULT '0' COMMENT '文件大小（字节）',
  `file_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件MIME类型（如: image/jpeg, application/pdf）',
  `file_ext` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件扩展名（如: jpg, pdf）',
  `storage_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'local' COMMENT '存储类型（local:本地, aliyun:阿里云, tencent:腾讯云, minio:MinIO）',
  `bucket_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储桶名称',
  `object_key` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象键（云存储中的唯一标识）',
  `md5` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件MD5值（用于秒传和去重）',
  `sha256` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件SHA256值（安全校验）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'default' COMMENT '文件分类（avatar:头像, document:文档, image:图片, video:视频, audio:音频, other:其他）',
  `biz_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型（关联的业务模块，如: user, article）',
  `biz_id` bigint DEFAULT NULL COMMENT '业务ID（关联的业务记录ID）',
  `uploader_id` bigint DEFAULT NULL COMMENT '上传者ID',
  `uploader_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传者名称',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `public_access` tinyint DEFAULT '1' COMMENT '是否公开访问（0:私有, 1:公开）',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间（临时文件使用）',
  `download_count` int DEFAULT '0' COMMENT '下载次数',
  `last_access_time` datetime DEFAULT NULL COMMENT '最后访问时间',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:待处理, 1:正常, 2:处理中, 3:处理失败）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_object_key` (`storage_type`,`bucket_name`,`object_key`),
  KEY `idx_md5` (`md5`),
  KEY `idx_category` (`category`),
  KEY `idx_biz` (`biz_type`,`biz_id`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_upload_time` (`upload_time`),
  KEY `idx_file_ext` (`file_ext`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件信息表';


-- ----------------------------
-- Records of `file_info`
-- ----------------------------

-- ----------------------------
-- Table structure for `log_operation`
-- ----------------------------
DROP TABLE IF EXISTS `log_operation`;
CREATE TABLE `log_operation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作模块（如：system, user, role）',
  `operation` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作类型（如：create, update, delete, login）',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `method` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求方法（GET/POST/PUT/DELETE）',
  `request_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '请求URL',
  `request_params` text COLLATE utf8mb4_unicode_ci COMMENT '请求参数（JSON）',
  `response_data` text COLLATE utf8mb4_unicode_ci COMMENT '响应数据（JSON）',
  `ip` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户代理',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `duration` bigint DEFAULT NULL COMMENT '执行耗时（毫秒）',
  `status` tinyint DEFAULT '1' COMMENT '操作状态（0:失败, 1:成功）',
  `error_msg` text COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  KEY `idx_module` (`module`),
  KEY `idx_operation` (`operation`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operate_time` (`operate_time`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';


-- ----------------------------
-- Records of `log_operation`
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_ai_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_ai_config`;
CREATE TABLE `sys_ai_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `provider` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提供商代码 (glm, deepseek, qianwen, claude)',
  `api_key` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'API Key (加密存储)',
  `endpoint` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'API 端点',
  `default_model` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '默认模型',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认提供商',
  `timeout` int DEFAULT '120' COMMENT '超时时间 (秒)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider` (`provider`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 配置表';


-- ----------------------------
-- Records of `sys_ai_config`
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_notify_subscribe`
-- ----------------------------
DROP TABLE IF EXISTS `sys_notify_subscribe`;
CREATE TABLE `sys_notify_subscribe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板ID',
  `template_key` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板标识',
  `channel` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知渠道',
  `subscribe_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订阅状态',
  `subscribe_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '订阅时间',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_template_channel` (`user_id`,`template_id`,`channel`),
  KEY `idx_template_id` (`template_id`),
  KEY `idx_channel` (`channel`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息订阅表';


-- ----------------------------
-- Table structure for `sys_notify_template`
-- ----------------------------
DROP TABLE IF EXISTS `sys_notify_template`;
CREATE TABLE `sys_notify_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板标识',
  `template_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_id` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板ID',
  `channel` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知渠道',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_key` (`template_key`),
  UNIQUE KEY `uk_template_id_channel` (`template_id`,`channel`),
  KEY `idx_channel` (`channel`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知模板表';


-- ----------------------------
-- Table structure for `sys_prompt_template`
-- ----------------------------
DROP TABLE IF EXISTS `sys_prompt_template`;
CREATE TABLE `sys_prompt_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板代码',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板分类',
  `system_prompt` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '系统提示',
  `user_prompt_template` text COLLATE utf8mb4_unicode_ci COMMENT '用户提示模板',
  `variables` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板变量 (JSON 数组)',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_category` (`category`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Prompt 模板表';


-- ----------------------------
-- Records of `sys_prompt_template`
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_switch_log`
-- ----------------------------
DROP TABLE IF EXISTS `sys_switch_log`;
CREATE TABLE `sys_switch_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `switch_id` bigint NOT NULL COMMENT '开关ID',
  `switch_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '开关键',
  `old_value` tinyint DEFAULT NULL COMMENT '变更前状态',
  `new_value` tinyint NOT NULL COMMENT '变更后状态',
  `old_config` json DEFAULT NULL COMMENT '变更前配置（JSON格式）',
  `new_config` json DEFAULT NULL COMMENT '变更后配置（JSON格式）',
  `change_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'manual' COMMENT '变更类型（manual:手动, schedule:定时, auto:自动）',
  `change_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变更原因',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人姓名',
  `operate_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  KEY `idx_switch_id` (`switch_id`),
  KEY `idx_switch_key` (`switch_key`),
  KEY `idx_operate_time` (`operate_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='开关操作日志表';


-- ----------------------------
-- Records of `sys_switch_log`
-- ----------------------------

-- ----------------------------
-- Table structure for `sys_user_social`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_social`;
CREATE TABLE `sys_user_social` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `social_type` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台类型（wechat_mp, wechat_miniapp, wechat_open, qq, github, apple, google, dingtalk, weibo, feishu）',
  `social_id` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '第三方平台用户ID（openid）',
  `union_id` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第三方平台统一ID（微信unionid）',
  `access_token` text COLLATE utf8mb4_general_ci COMMENT '访问令牌（加密存储）',
  `refresh_token` text COLLATE utf8mb4_general_ci COMMENT '刷新令牌（加密存储）',
  `expire_time` datetime DEFAULT NULL COMMENT '令牌过期时间',
  `nickname` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第三方平台昵称',
  `avatar` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '第三方平台头像',
  `raw_data` text COLLATE utf8mb4_general_ci COMMENT '原始用户数据（JSON格式）',
  `bind_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
  `tenant_id` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_social_type_id` (`social_type`,`social_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_union_id` (`union_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户第三方账号关联表';


-- ----------------------------
-- Records of `sys_user_social`
-- ----------------------------

-- ----------------------------
-- Table structure for `wallet_recharge_order`
-- ----------------------------
DROP TABLE IF EXISTS `wallet_recharge_order`;
CREATE TABLE `wallet_recharge_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `config_id` bigint DEFAULT NULL COMMENT '充值配置ID',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '充值类型: points-积分, balance-余额',
  `pay_amount` decimal(12,2) NOT NULL COMMENT '支付金额',
  `reward_amount` decimal(12,2) NOT NULL COMMENT '到账金额/积分',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单状态: pending-待支付, paid-已支付, refunded-已退款, closed-已关闭, failed-失败',
  `wechat_transaction_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信支付订单号',
  `refund_no` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款单号',
  `refund_amount` decimal(12,2) DEFAULT NULL COMMENT '退款金额',
  `refund_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '退款原因',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `refund_by` bigint DEFAULT NULL COMMENT '退款人ID',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`,`tenant_id`,`deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包充值订单表';


-- ----------------------------
-- Records of `wallet_recharge_order`
-- ----------------------------

-- ----------------------------
-- Table structure for `wallet_signin`
-- ----------------------------
DROP TABLE IF EXISTS `wallet_signin`;
CREATE TABLE `wallet_signin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `sign_date` date NOT NULL COMMENT '签到日期',
  `points` bigint NOT NULL COMMENT '获得积分',
  `consecutive_days` int NOT NULL COMMENT '连续签到天数',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`,`sign_date`,`tenant_id`,`deleted`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_sign_date` (`sign_date`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包签到记录表';


-- ----------------------------
-- Records of `wallet_signin`
-- ----------------------------

-- ----------------------------
-- Table structure for `wallet_transaction`
-- ----------------------------
DROP TABLE IF EXISTS `wallet_transaction`;
CREATE TABLE `wallet_transaction` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '交易类型: points-积分, balance-余额',
  `action` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型: income-收入, expense-支出, freeze-冻结, unfreeze-解冻',
  `amount` decimal(12,2) NOT NULL COMMENT '变动金额（正数为增加，负数为减少）',
  `before_amount` decimal(12,2) NOT NULL COMMENT '变动前金额',
  `after_amount` decimal(12,2) NOT NULL COMMENT '变动后金额',
  `biz_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务类型（如：order-订单, recharge-充值, activity-活动等）',
  `biz_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '业务ID（关联的订单号等）',
  `title` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标题',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID（后台操作时记录）',
  `operator_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作人名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_action` (`action`),
  KEY `idx_biz_type` (`biz_type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包变动记录表';


-- ----------------------------
-- Records of `wallet_transaction`
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;


-- ----------------------------
-- Table structure for `file_config`
-- ----------------------------
DROP TABLE IF EXISTS `file_config`;
CREATE TABLE `file_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `config_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置标识（唯一，如: aliyun-oss, tencent-cos）',
  `storage_type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储类型（local:本地, aliyun:阿里云OSS, tencent:腾讯云COS, minio:MinIO, qiniu:七牛云）',
  `endpoint` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '服务端点（如: oss-cn-hangzhou.aliyuncs.com）',
  `access_key` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'AccessKey（加密存储）',
  `secret_key` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'SecretKey（加密存储）',
  `bucket_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储桶名称',
  `region` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '存储区域',
  `domain` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义域名（CDN加速域名）',
  `base_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '基础路径（文件存储的根目录）',
  `use_https` tinyint DEFAULT '1' COMMENT '是否使用HTTPS（0:否, 1:是）',
  `defaulted` tinyint DEFAULT '0' COMMENT '是否默认配置（0:否, 1:是，仅一个）',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`,`tenant_id`,`deleted`),
  KEY `idx_storage_type` (`storage_type`),
  KEY `idx_defaulted` (`defaulted`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件存储配置表';


-- ----------------------------
-- Records of `file_config`
-- ----------------------------
INSERT INTO `file_config` (`id`, `config_name`, `config_key`, `storage_type`, `endpoint`, `access_key`, `secret_key`, `bucket_name`, `region`, `domain`, `base_path`, `use_https`, `defaulted`, `status`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, '本地存储', 'local', 'local', NULL, NULL, NULL, 'triflow', NULL, NULL, '/upload', 1, 0, 1, '本地文件存储，适用于开发环境', NULL, NULL, NULL, '2026-01-24 02:46:20', NULL, '2026-01-25 01:39:16', 1),
(2, 'MinIO存储', 'minio', 'minio', 'http://minio:9000', 'minioadmin', 'minioadmin', 'triflow', NULL, NULL, '/upload', 0, 1, 1, 'MinIO对象存储，适用于Docker部署环境', NULL, NULL, NULL, NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for `sys_config`
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键（唯一标识，如: sys.user.initPassword）',
  `config_value` text COLLATE utf8mb4_unicode_ci COMMENT '配置值（支持JSON格式）',
  `value_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'string' COMMENT '值类型（string:字符串, number:数字, boolean:布尔, json:JSON对象, array:数组）',
  `config_type` tinyint DEFAULT '1' COMMENT '配置类型（0:系统内置-不可删除, 1:业务配置-可删除）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'default' COMMENT '配置分类（用于分组显示，如: security, upload, email）',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';


-- ----------------------------
-- Records of `sys_config`
-- ----------------------------
INSERT INTO `sys_config` (`id`, `config_name`, `config_key`, `config_value`, `value_type`, `config_type`, `category`, `sort`, `status`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(6, '验证码类型', 'sys.captcha.type', 'math', 'string', 0, 'security', 11, 1, '验证码类型（math:数学运算, char:字符）', NULL, NULL, NULL, '2026-01-24 02:46:28', NULL, '2026-01-24 02:46:28', 0),
(7, 'Token有效期(分钟)', 'sys.token.expireTime', '1440', 'number', 0, 'security', 12, 1, 'JWT Token有效期，默认24小时', NULL, NULL, NULL, '2026-01-24 02:46:28', NULL, '2026-01-24 02:46:28', 0),
(16, '签到积分', 'wallet.signin.points', '0', 'number', 0, 'business', 11, 1, '每日签到发放积分', NULL, NULL, NULL, '2026-01-29 00:16:22', NULL, '2026-01-29 08:26:49', 0),
(19, '默认数据权限', 'sys.user.defaultDataScope', 'UserCreate', 'STRING', 1, 'security', 100, 1, '新用户默认数据权限范围 (All, DeptAndChildren, Dept, UserCreate, JoinGroup)', NULL, NULL, NULL, '2026-02-01 17:19:25', NULL, '2026-02-01 17:19:25', 0);

-- ----------------------------
-- Table structure for `sys_dept`
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID（0表示顶级部门）',
  `ancestors` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `sort` int DEFAULT '0' COMMENT '排序',
  `leader` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统部门表';


-- ----------------------------
-- Records of `sys_dept`
-- ----------------------------
INSERT INTO `sys_dept` (`id`, `parent_id`, `ancestors`, `dept_name`, `sort`, `leader`, `phone`, `email`, `status`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 0, '0', 'Triflow科技', 0, 'Admin', NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(2, 1, '0,1', '研发部', 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(3, 1, '0,1', '产品部', 2, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(4, 1, '0,1', '运营部', 3, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(5, 2, '0,1,2', '前端组', 1, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(6, 2, '0,1,2', '后端组', 2, NULL, NULL, NULL, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0);

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID（0表示顶级菜单）',
  `menu_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `menu_type` char(1) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单类型（M:目录, C:菜单, F:按钮）',
  `path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '路由地址',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '路由名称（用于前端路由）',
  `component` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '组件路径',
  `redirect` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '重定向地址',
  `permission` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '菜单图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `visible` tinyint DEFAULT '1' COMMENT '是否可见（0:隐藏, 1:显示）',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `is_frame` tinyint DEFAULT '0' COMMENT '是否外链（0:否, 1:是）',
  `is_cache` tinyint DEFAULT '1' COMMENT '是否缓存（0:否, 1:是）',
  `is_affix` tinyint DEFAULT '0' COMMENT '是否固定标签（0:否, 1:是）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单表';


-- ----------------------------
-- Records of `sys_menu`
-- ----------------------------
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `name`, `component`, `redirect`, `permission`, `icon`, `sort`, `visible`, `status`, `is_frame`, `is_cache`, `is_affix`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 0, 'Dashboard', 'M', '/dashboard', 'Dashboard', NULL, '/analytics', NULL, 'lucide:layout-dashboard', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(2, 1, '数据分析', 'C', '/analytics', 'Analytics', '/dashboard/analytics/index', NULL, NULL, 'lucide:area-chart', 1, 1, 1, 0, 1, 1, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(3, 1, '工作台', 'C', '/workspace', 'Workspace', '/dashboard/workspace/index', NULL, NULL, 'lucide:briefcase', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(21, 2, '分析查询', 'F', '', NULL, NULL, NULL, 'analytics:dashboard:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:52', NULL, '2026-02-10 17:37:52', 0),
(100, 0, '系统管理', 'M', '/system', 'System', NULL, '/system/user', NULL, 'lucide:settings', 100, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(101, 100, '用户管理', 'C', '/system/user', 'SystemUser', '/system/user/index', NULL, 'system:user:list', 'lucide:users', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(102, 100, '角色管理', 'C', '/system/role', 'SystemRole', '/system/role/index', NULL, 'system:role:list', 'lucide:shield', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(103, 100, '菜单管理', 'C', '/system/menu', 'SystemMenu', '/system/menu/index', NULL, 'system:menu:list', 'lucide:menu', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(104, 100, '部门管理', 'C', '/system/dept', 'SystemDept', '/system/dept/index', NULL, 'system:dept:list', 'lucide:building-2', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(105, 100, '配置管理', 'C', '/system/config', 'SystemConfig', '/system/config/index', NULL, 'system:config:list', 'lucide:sliders-horizontal', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:36', NULL, '2026-01-24 18:01:36', 0),
(106, 100, '开关管理', 'C', '/system/switch', 'SystemSwitch', '/system/switch/index', NULL, 'system:switch:list', 'lucide:toggle-left', 6, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:38', NULL, '2026-01-24 18:01:38', 0),
(107, 100, '操作日志', 'C', '/system/log', 'SystemLog', '/system/log/index', NULL, 'system:log:list', 'lucide:file-clock', 7, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 16:32:08', NULL, '2026-01-29 16:32:08', 0),
(300, 0, '文件管理', 'M', '/file', 'File', NULL, '/file/info', NULL, 'lucide:folder', 300, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:40', NULL, '2026-01-24 18:01:40', 0),
(301, 300, '存储配置', 'C', '/file/config', 'FileConfig', '/file/config/index', NULL, 'file:config:list', 'lucide:hard-drive', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:40', NULL, '2026-01-24 18:01:40', 0),
(302, 300, '文件列表', 'C', '/file/info', 'FileInfo', '/file/info/index', NULL, 'file:info:list', 'lucide:files', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:42', NULL, '2026-01-24 18:01:42', 0),
(400, 0, '内容管理', 'M', '/cms', 'Cms', NULL, '/cms/text', NULL, 'lucide:newspaper', 400, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:43', NULL, '2026-01-24 18:01:43', 0),
(401, 400, '文本分类', 'C', '/cms/text-category', 'CmsTextCategory', '/cms/text-category/index', NULL, 'cms:textCategory:list', 'lucide:folder-tree', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:44', NULL, '2026-01-24 18:01:44', 0),
(402, 400, '文本管理', 'C', '/cms/text', 'CmsText', '/cms/text/index', NULL, 'cms:text:list', 'lucide:file-text', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(500, 0, '微信管理', 'M', '/wechat', 'Wechat', NULL, '/wechat/subscribe', NULL, 'lucide:message-circle', 500, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(501, 500, '订阅管理', 'C', '/wechat/subscribe', 'WechatSubscribe', '/wechat/subscribe/index', NULL, 'wechat:subscribe:list', 'lucide:bell', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(502, 500, '底部菜单', 'C', '/wechat/tabbar', 'WechatTabbar', '/wechat/tabbar/index', NULL, 'wechat:tabbar:list', 'lucide:layout-template', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(503, 500, '通知模板', 'C', '/wechat/notify-template', 'NotifyTemplate', '/wechat/notify-template/index', NULL, 'notify:template:list', 'lucide:mail', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:52', NULL, '2026-02-10 17:37:52', 0),
(600, 0, '文章管理', 'M', '/template', 'Template', NULL, '/template/article', NULL, 'lucide:book-open', 600, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(601, 600, '文章列表', 'C', '/template/article', 'Article', '/template/article/index', NULL, 'template:article:list', 'lucide:file-text', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(1011, 101, '用户查询', 'F', '', NULL, NULL, NULL, 'system:user:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1012, 101, '用户新增', 'F', '', NULL, NULL, NULL, 'system:user:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1013, 101, '用户修改', 'F', '', NULL, NULL, NULL, 'system:user:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1014, 101, '用户删除', 'F', '', NULL, NULL, NULL, 'system:user:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1015, 101, '用户导出', 'F', '', NULL, NULL, NULL, 'system:user:export', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1016, 101, '用户导入', 'F', '', NULL, NULL, NULL, 'system:user:import', '', 6, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1017, 101, '重置密码', 'F', '', NULL, NULL, NULL, 'system:user:resetPwd', '', 7, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1018, 101, '踢用户下线', 'F', '', NULL, NULL, NULL, 'system:user:kickout', '', 8, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 00:49:12', NULL, '2026-01-24 00:49:12', 0),
(1021, 102, '角色查询', 'F', '', NULL, NULL, NULL, 'system:role:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1022, 102, '角色新增', 'F', '', NULL, NULL, NULL, 'system:role:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1023, 102, '角色修改', 'F', '', NULL, NULL, NULL, 'system:role:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1024, 102, '角色删除', 'F', '', NULL, NULL, NULL, 'system:role:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1025, 102, '角色导出', 'F', '', NULL, NULL, NULL, 'system:role:export', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:16', NULL, '2026-01-23 14:46:16', 0),
(1031, 103, '菜单查询', 'F', '', NULL, NULL, NULL, 'system:menu:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1032, 103, '菜单新增', 'F', '', NULL, NULL, NULL, 'system:menu:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1033, 103, '菜单修改', 'F', '', NULL, NULL, NULL, 'system:menu:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1034, 103, '菜单删除', 'F', '', NULL, NULL, NULL, 'system:menu:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1041, 104, '部门查询', 'F', '', NULL, NULL, NULL, 'system:dept:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1042, 104, '部门新增', 'F', '', NULL, NULL, NULL, 'system:dept:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1043, 104, '部门修改', 'F', '', NULL, NULL, NULL, 'system:dept:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1044, 104, '部门删除', 'F', '', NULL, NULL, NULL, 'system:dept:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-23 14:46:17', NULL, '2026-01-23 14:46:17', 0),
(1051, 105, '配置查询', 'F', '', NULL, NULL, NULL, 'system:config:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:37', NULL, '2026-01-24 18:01:37', 0),
(1052, 105, '配置新增', 'F', '', NULL, NULL, NULL, 'system:config:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:37', NULL, '2026-01-24 18:01:37', 0),
(1053, 105, '配置修改', 'F', '', NULL, NULL, NULL, 'system:config:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:37', NULL, '2026-01-24 18:01:37', 0),
(1054, 105, '配置删除', 'F', '', NULL, NULL, NULL, 'system:config:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:37', NULL, '2026-01-24 18:01:37', 0),
(1055, 105, '刷新缓存', 'F', '', NULL, NULL, NULL, 'system:config:refresh', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:37', NULL, '2026-01-24 18:01:37', 0),
(1061, 106, '开关查询', 'F', '', NULL, NULL, NULL, 'system:switch:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1062, 106, '开关新增', 'F', '', NULL, NULL, NULL, 'system:switch:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1063, 106, '开关修改', 'F', '', NULL, NULL, NULL, 'system:switch:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1064, 106, '开关删除', 'F', '', NULL, NULL, NULL, 'system:switch:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1065, 106, '开关切换', 'F', '', NULL, NULL, NULL, 'system:switch:toggle', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1066, 106, '刷新缓存', 'F', '', NULL, NULL, NULL, 'system:switch:refresh', '', 6, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:39', NULL, '2026-01-24 18:01:39', 0),
(1071, 107, '日志查询', 'F', '', NULL, NULL, NULL, 'system:log:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 16:32:08', NULL, '2026-01-29 16:32:08', 0),
(1072, 107, '日志删除', 'F', '', NULL, NULL, NULL, 'system:log:delete', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 16:32:08', NULL, '2026-01-29 16:32:08', 0),
(1073, 107, '日志导出', 'F', '', NULL, NULL, NULL, 'system:log:export', '', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 16:32:08', NULL, '2026-01-29 16:32:08', 0),
(1074, 107, '日志清空', 'F', '', NULL, NULL, NULL, 'system:log:clear', '', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 16:32:08', NULL, '2026-01-29 16:32:08', 0),
(3011, 301, '配置查询', 'F', '', NULL, NULL, NULL, 'file:config:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3012, 301, '配置新增', 'F', '', NULL, NULL, NULL, 'file:config:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3013, 301, '配置修改', 'F', '', NULL, NULL, NULL, 'file:config:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3014, 301, '配置删除', 'F', '', NULL, NULL, NULL, 'file:config:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3015, 301, '设为默认', 'F', '', NULL, NULL, NULL, 'file:config:setDefault', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3016, 301, '测试连接', 'F', '', NULL, NULL, NULL, 'file:config:test', '', 6, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:41', NULL, '2026-01-24 18:01:41', 0),
(3021, 302, '文件查询', 'F', '', NULL, NULL, NULL, 'file:info:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:43', NULL, '2026-01-24 18:01:43', 0),
(3022, 302, '文件上传', 'F', '', NULL, NULL, NULL, 'file:info:upload', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:43', NULL, '2026-01-24 18:01:43', 0),
(3023, 302, '文件删除', 'F', '', NULL, NULL, NULL, 'file:info:delete', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:43', NULL, '2026-01-24 18:01:43', 0),
(3024, 302, '文件下载', 'F', '', NULL, NULL, NULL, 'file:info:download', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:43', NULL, '2026-01-24 18:01:43', 0),
(4011, 401, '分类查询', 'F', '', NULL, NULL, NULL, 'cms:textCategory:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:44', NULL, '2026-01-24 18:01:44', 0),
(4012, 401, '分类新增', 'F', '', NULL, NULL, NULL, 'cms:textCategory:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:44', NULL, '2026-01-24 18:01:44', 0),
(4013, 401, '分类修改', 'F', '', NULL, NULL, NULL, 'cms:textCategory:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:44', NULL, '2026-01-24 18:01:44', 0),
(4014, 401, '分类删除', 'F', '', NULL, NULL, NULL, 'cms:textCategory:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:44', NULL, '2026-01-24 18:01:44', 0),
(4021, 402, '文本查询', 'F', '', NULL, NULL, NULL, 'cms:text:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(4022, 402, '文本新增', 'F', '', NULL, NULL, NULL, 'cms:text:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(4023, 402, '文本修改', 'F', '', NULL, NULL, NULL, 'cms:text:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(4024, 402, '文本删除', 'F', '', NULL, NULL, NULL, 'cms:text:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(4025, 402, '文本发布', 'F', '', NULL, NULL, NULL, 'cms:text:publish', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(4026, 402, '文本下架', 'F', '', NULL, NULL, NULL, 'cms:text:unpublish', '', 6, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-24 18:01:45', NULL, '2026-01-24 18:01:45', 0),
(5011, 501, '订阅查询', 'F', '', NULL, NULL, NULL, 'wechat:subscribe:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5012, 501, '订阅更新', 'F', '', NULL, NULL, NULL, 'wechat:subscribe:update', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5013, 501, '订阅删除', 'F', '', NULL, NULL, NULL, 'wechat:subscribe:delete', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5014, 501, '订阅批量删除', 'F', '', NULL, NULL, NULL, 'wechat:subscribe:deleteBatch', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5021, 502, '菜单查询', 'F', '', NULL, NULL, NULL, 'wechat:tabbar:query', '', 1, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5022, 502, '菜单新增', 'F', '', NULL, NULL, NULL, 'wechat:tabbar:create', '', 2, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5023, 502, '菜单修改', 'F', '', NULL, NULL, NULL, 'wechat:tabbar:update', '', 3, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5024, 502, '菜单删除', 'F', '', NULL, NULL, NULL, 'wechat:tabbar:delete', '', 4, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5025, 502, '菜单批量删除', 'F', '', NULL, NULL, NULL, 'wechat:tabbar:deleteBatch', '', 5, 1, 1, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-01-27 21:50:15', NULL, '2026-01-27 21:50:15', 0),
(5026, 101, '调整余额积分', 'F', '', '', '', '', 'system:user:adjustBalance', '', 8, 1, 1, 0, 0, 0, '', NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-29 00:40:57', 0),
(5031, 503, '模板查询', 'F', '', NULL, NULL, NULL, 'notify:template:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(5032, 503, '模板新增', 'F', '', NULL, NULL, NULL, 'notify:template:create', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(5033, 503, '模板修改', 'F', '', NULL, NULL, NULL, 'notify:template:update', '', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(5034, 503, '模板删除', 'F', '', NULL, NULL, NULL, 'notify:template:delete', '', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(5100, 0, 'AI 管理', 'M', '/ai', 'AI', NULL, '/ai/config', NULL, 'lucide:bot', 18, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5101, 5100, 'AI 配置', 'C', '/ai/config', 'AiConfig', '/ai/config/index', NULL, 'ai:config:list', 'lucide:settings', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5102, 5101, 'AI配置查询', 'F', NULL, NULL, NULL, NULL, 'ai:config:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5103, 5101, 'AI配置修改', 'F', NULL, NULL, NULL, NULL, 'ai:config:update', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5104, 5101, 'AI配置测试', 'F', NULL, NULL, NULL, NULL, 'ai:config:test', NULL, 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5110, 5100, 'Prompt 模板', 'C', '/ai/prompt', 'AiPrompt', '/ai/prompt/index', NULL, 'ai:prompt:list', 'lucide:message-square-text', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5111, 5110, 'Prompt查询', 'F', NULL, NULL, NULL, NULL, 'ai:prompt:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5112, 5110, 'Prompt新增', 'F', NULL, NULL, NULL, NULL, 'ai:prompt:create', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5113, 5110, 'Prompt修改', 'F', NULL, NULL, NULL, NULL, 'ai:prompt:update', NULL, 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:39', NULL, '2026-01-28 16:42:39', 0),
(5114, 5110, 'Prompt删除', 'F', NULL, NULL, NULL, NULL, 'ai:prompt:delete', NULL, 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-28 16:42:40', 0),
(5120, 5100, 'AI调用记录', 'C', '/ai/call-log', 'AiCallLog', '/ai/call-log/index', NULL, 'ai:call-log:list', 'lucide:scroll-text', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 21:49:29', NULL, '2026-01-29 21:49:29', 0),
(5121, 5120, '调用记录查询', 'F', '', NULL, NULL, NULL, 'ai:call-log:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 21:49:29', NULL, '2026-01-29 21:49:29', 0),
(5122, 5120, '调用记录删除', 'F', '', NULL, NULL, NULL, 'ai:call-log:delete', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 21:49:29', NULL, '2026-01-29 21:49:29', 0),
(5200, 0, '钱包管理', 'M', '/wallet', 'Wallet', NULL, '/wallet/transaction', NULL, 'lucide:wallet', 15, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-28 16:42:40', 0),
(5201, 5200, '账户变动', 'C', '/wallet/transaction', 'WalletTransaction', '/wallet/transaction/index', NULL, 'wallet:transaction:list', 'lucide:arrow-left-right', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-28 16:42:40', 0),
(5202, 5201, '流水查询', 'F', NULL, NULL, NULL, NULL, 'wallet:transaction:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-28 16:42:40', 0),
(5203, 5201, '账户调整', 'F', NULL, NULL, NULL, NULL, 'wallet:transaction:adjust', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-28 16:42:40', NULL, '2026-01-28 16:42:40', 0),
(5230, 5200, '充值配置', 'C', '/wallet/recharge-config', 'WalletRechargeConfig', '/wallet/recharge-config/index', NULL, 'wallet:recharge:list', 'lucide:badge-dollar-sign', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5231, 5230, '充值配置查询', 'F', '', NULL, NULL, NULL, 'wallet:recharge:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5232, 5230, '充值配置新增', 'F', '', NULL, NULL, NULL, 'wallet:recharge:create', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5233, 5230, '充值配置修改', 'F', '', NULL, NULL, NULL, 'wallet:recharge:update', '', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5234, 5230, '充值配置删除', 'F', '', NULL, NULL, NULL, 'wallet:recharge:delete', '', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5240, 5200, '签到记录', 'C', '/wallet/signin', 'WalletSignIn', '/wallet/signin/index', NULL, 'wallet:signin:list', 'lucide:calendar-check', 5, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5241, 5240, '签到记录查询', 'F', '', NULL, NULL, NULL, 'wallet:signin:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 00:16:21', NULL, '2026-01-29 00:16:21', 0),
(5242, 5240, '签到配置修改', 'F', '', NULL, NULL, NULL, 'wallet:signin:update', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(5250, 5200, '充值订单', 'C', '/wallet/recharge-order', 'WalletRechargeOrder', '/wallet/recharge-order/index', NULL, 'wallet:recharge-order:list', 'lucide:wallet-cards', 6, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 11:43:16', NULL, '2026-01-29 11:43:16', 0),
(5251, 5250, '充值订单查询', 'F', '', NULL, NULL, NULL, 'wallet:recharge-order:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 11:43:16', NULL, '2026-01-29 11:43:16', 0),
(5252, 5250, '充值订单退款', 'F', '', NULL, NULL, NULL, 'wallet:recharge-order:refund', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-01-29 11:43:16', NULL, '2026-01-29 11:43:16', 0),
(5253, 5250, '充值订单编辑', 'F', '', '', '', '', 'wallet:recharge-order:update', '', 0, 1, 1, 0, 1, 0, '', NULL, NULL, NULL, NULL, NULL, '2026-01-29 12:54:31', NULL),
(6011, 601, '文章查询', 'F', '', NULL, NULL, NULL, 'template:article:query', '', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(6012, 601, '文章新增', 'F', '', NULL, NULL, NULL, 'template:article:create', '', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(6013, 601, '文章修改', 'F', '', NULL, NULL, NULL, 'template:article:update', '', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(6014, 601, '文章删除', 'F', '', NULL, NULL, NULL, 'template:article:delete', '', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-10 17:37:53', NULL, '2026-02-10 17:37:53', 0),
(7100, 0, '星星领航员', 'M', '/starnav', 'StarNav', NULL, '/starnav/goal', NULL, 'lucide:star', 25, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7101, 7100, '目标管理', 'C', '/starnav/goal', 'StarNavGoal', '/starnav/goal/index', NULL, 'starnav:goal:list', 'lucide:target', 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7102, 7101, '目标查询', 'F', NULL, NULL, NULL, NULL, 'starnav:goal:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7103, 7101, '目标删除', 'F', NULL, NULL, NULL, NULL, 'starnav:goal:delete', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7110, 7100, '模板管理', 'C', '/starnav/template', 'StarNavTemplate', '/starnav/template/index', NULL, 'starnav:template:list', 'lucide:layout-template', 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7111, 7110, '模板查询', 'F', NULL, NULL, NULL, NULL, 'starnav:template:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7112, 7110, '模板新增', 'F', NULL, NULL, NULL, NULL, 'starnav:template:create', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7113, 7110, '模板修改', 'F', NULL, NULL, NULL, NULL, 'starnav:template:update', NULL, 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7114, 7110, '模板删除', 'F', NULL, NULL, NULL, NULL, 'starnav:template:delete', NULL, 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:38:15', NULL, '2026-02-28 11:38:15', 0),
(7115, 7101, '目标导出', 'F', NULL, NULL, NULL, NULL, 'starnav:goal:export', NULL, 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7120, 7100, '数据概览', 'C', '/starnav/dashboard', 'StarNavDashboard', '/starnav/dashboard/index', NULL, 'starnav:stats:list', 'lucide:bar-chart-3', 0, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7121, 7120, '统计查询', 'F', NULL, NULL, NULL, NULL, 'starnav:stats:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7130, 7100, '成就管理', 'C', '/starnav/achievement', 'StarNavAchievement', '/starnav/achievement/index', NULL, 'starnav:achievement:list', 'lucide:trophy', 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7131, 7130, '成就查询', 'F', NULL, NULL, NULL, NULL, 'starnav:achievement:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7132, 7130, '成就新增', 'F', NULL, NULL, NULL, NULL, 'starnav:achievement:create', NULL, 2, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7133, 7130, '成就修改', 'F', NULL, NULL, NULL, NULL, 'starnav:achievement:update', NULL, 3, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7134, 7130, '成就删除', 'F', NULL, NULL, NULL, NULL, 'starnav:achievement:delete', NULL, 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7140, 7100, 'AI 会话', 'C', '/starnav/session', 'StarNavSession', '/starnav/session/index', NULL, 'starnav:session:list', 'lucide:messages-square', 4, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0),
(7141, 7140, '会话查询', 'F', NULL, NULL, NULL, NULL, 'starnav:session:query', NULL, 1, 1, 1, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-02-28 11:39:55', NULL, '2026-02-28 11:39:55', 0);

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色标识',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色表';


-- ----------------------------
-- Records of `sys_role`
-- ----------------------------
INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, 'Root', 'root', 1, 1, '超级管理员，拥有所有权限', NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 21:30:57', 0),
(2, '系统管理员', 'admin', 2, 1, '系统管理员', NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-01-23 14:46:15', 0),
(3, '普通用户', 'user', 3, 1, '普通用户，仅可查看自己的数据', NULL, NULL, NULL, '2026-01-23 14:46:15', NULL, '2026-02-01 15:07:11', 0);

-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';


-- ----------------------------
-- Records of `sys_role_menu`
-- ----------------------------
INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`, `tenant_id`) VALUES
(32, 2, 1, NULL),
(33, 2, 2, NULL),
(34, 2, 3, NULL),
(35, 2, 100, NULL),
(36, 2, 101, NULL),
(37, 2, 1011, NULL),
(38, 2, 1012, NULL),
(39, 2, 1013, NULL),
(40, 2, 1014, NULL),
(41, 2, 1015, NULL),
(42, 2, 1016, NULL),
(43, 2, 1017, NULL),
(44, 2, 102, NULL),
(45, 2, 1021, NULL),
(46, 2, 1022, NULL),
(47, 2, 1023, NULL),
(48, 2, 1024, NULL),
(49, 2, 1025, NULL),
(50, 2, 103, NULL),
(51, 2, 1031, NULL),
(52, 2, 1032, NULL),
(53, 2, 1033, NULL),
(54, 2, 1034, NULL),
(55, 2, 104, NULL),
(56, 2, 1041, NULL),
(57, 2, 1042, NULL),
(58, 2, 1043, NULL),
(59, 2, 1044, NULL),
(67, 2, 1018, NULL),
(118, 2, 105, NULL),
(119, 2, 1051, NULL),
(120, 2, 1052, NULL),
(121, 2, 1053, NULL),
(122, 2, 1054, NULL),
(123, 2, 1055, NULL),
(124, 2, 106, NULL),
(125, 2, 1061, NULL),
(126, 2, 1062, NULL),
(127, 2, 1063, NULL),
(128, 2, 1064, NULL),
(129, 2, 1065, NULL),
(130, 2, 1066, NULL),
(143, 2, 500, NULL),
(144, 2, 501, NULL),
(145, 2, 5011, NULL),
(146, 2, 5012, NULL),
(147, 2, 5013, NULL),
(148, 2, 5014, NULL),
(149, 2, 502, NULL),
(150, 2, 5021, NULL),
(151, 2, 5022, NULL),
(152, 2, 5023, NULL),
(153, 2, 5024, NULL),
(154, 2, 5025, NULL),
(180, 2, 400, NULL),
(181, 2, 401, NULL),
(182, 2, 4011, NULL),
(183, 2, 4012, NULL),
(184, 2, 4013, NULL),
(185, 2, 402, NULL),
(186, 2, 4021, NULL),
(187, 2, 4022, NULL),
(188, 2, 4023, NULL),
(189, 2, 4024, NULL),
(213, 2, 5100, NULL),
(214, 2, 5101, NULL),
(215, 2, 5102, NULL),
(216, 2, 5103, NULL),
(217, 2, 5104, NULL),
(218, 2, 5110, NULL),
(219, 2, 5111, NULL),
(220, 2, 5112, NULL),
(221, 2, 5113, NULL),
(222, 2, 5114, NULL),
(223, 2, 5200, NULL),
(224, 2, 5201, NULL),
(225, 2, 5202, NULL),
(226, 2, 5203, NULL),
(229, 2, 5026, NULL),
(344, 2, 5230, NULL),
(345, 2, 5231, NULL),
(346, 2, 5232, NULL),
(347, 2, 5233, NULL),
(348, 2, 5234, NULL),
(349, 2, 5240, NULL),
(350, 2, 5241, NULL),
(567, 2, 5250, NULL),
(568, 2, 5251, NULL),
(569, 2, 5252, NULL),
(570, 1, 1, NULL),
(571, 1, 2, NULL),
(572, 1, 3, NULL),
(573, 1, 5200, NULL),
(574, 1, 5201, NULL),
(575, 1, 5202, NULL),
(576, 1, 5203, NULL),
(577, 1, 5230, NULL),
(578, 1, 5231, NULL),
(579, 1, 5232, NULL),
(580, 1, 5233, NULL),
(581, 1, 5234, NULL),
(582, 1, 5240, NULL),
(583, 1, 5241, NULL),
(584, 1, 5250, NULL),
(585, 1, 5253, NULL),
(586, 1, 5251, NULL),
(587, 1, 5252, NULL),
(588, 1, 5100, NULL),
(589, 1, 5101, NULL),
(590, 1, 5102, NULL),
(591, 1, 5103, NULL),
(592, 1, 5104, NULL),
(593, 1, 5110, NULL),
(594, 1, 5111, NULL),
(595, 1, 5112, NULL),
(596, 1, 5113, NULL),
(597, 1, 5114, NULL),
(598, 1, 100, NULL),
(599, 1, 101, NULL),
(600, 1, 1011, NULL),
(601, 1, 1012, NULL),
(602, 1, 1013, NULL),
(603, 1, 1014, NULL),
(604, 1, 1015, NULL),
(605, 1, 1016, NULL),
(606, 1, 1017, NULL),
(607, 1, 1018, NULL),
(608, 1, 5026, NULL),
(609, 1, 102, NULL),
(610, 1, 1021, NULL),
(611, 1, 1022, NULL),
(612, 1, 1023, NULL),
(613, 1, 1024, NULL),
(614, 1, 1025, NULL),
(615, 1, 103, NULL),
(616, 1, 1031, NULL),
(617, 1, 1032, NULL),
(618, 1, 1033, NULL),
(619, 1, 1034, NULL),
(620, 1, 104, NULL),
(621, 1, 1041, NULL),
(622, 1, 1042, NULL),
(623, 1, 1043, NULL),
(624, 1, 1044, NULL),
(625, 1, 105, NULL),
(626, 1, 1051, NULL),
(627, 1, 1052, NULL),
(628, 1, 1053, NULL),
(629, 1, 1054, NULL),
(630, 1, 1055, NULL),
(631, 1, 106, NULL),
(632, 1, 1061, NULL),
(633, 1, 1062, NULL),
(634, 1, 1063, NULL),
(635, 1, 1064, NULL),
(636, 1, 1065, NULL),
(637, 1, 1066, NULL),
(641, 1, 300, NULL),
(642, 1, 301, NULL),
(643, 1, 3011, NULL),
(644, 1, 3012, NULL),
(645, 1, 3013, NULL),
(646, 1, 3014, NULL),
(647, 1, 3015, NULL),
(648, 1, 3016, NULL),
(649, 1, 302, NULL),
(650, 1, 3021, NULL),
(651, 1, 3022, NULL),
(652, 1, 3023, NULL),
(653, 1, 3024, NULL),
(654, 1, 400, NULL),
(655, 1, 401, NULL),
(656, 1, 4011, NULL),
(657, 1, 4012, NULL),
(658, 1, 4013, NULL),
(659, 1, 4014, NULL),
(660, 1, 402, NULL),
(661, 1, 4021, NULL),
(662, 1, 4022, NULL),
(663, 1, 4023, NULL),
(664, 1, 4024, NULL),
(665, 1, 4025, NULL),
(666, 1, 4026, NULL),
(667, 1, 500, NULL),
(668, 1, 501, NULL),
(669, 1, 5011, NULL),
(670, 1, 5012, NULL),
(671, 1, 5013, NULL),
(672, 1, 5014, NULL),
(673, 1, 502, NULL),
(674, 1, 5021, NULL),
(675, 1, 5022, NULL),
(676, 1, 5023, NULL),
(677, 1, 5024, NULL),
(678, 1, 5025, NULL),
(679, 1, 107, NULL),
(680, 1, 1071, NULL),
(681, 1, 1072, NULL),
(682, 1, 1073, NULL),
(683, 1, 1074, NULL),
(686, 2, 107, NULL),
(687, 2, 1071, NULL),
(688, 2, 1072, NULL),
(689, 2, 1073, NULL),
(690, 2, 1074, NULL),
(693, 1, 5120, NULL),
(694, 1, 5121, NULL),
(695, 1, 5122, NULL),
(696, 2, 5120, NULL),
(697, 2, 5121, NULL),
(698, 2, 5122, NULL),
(713, 3, 1, NULL),
(714, 3, 2, NULL),
(715, 3, 3, NULL),
(716, 3, 100, NULL),
(717, 3, 101, NULL),
(718, 3, 1011, NULL),
(719, 3, 1012, NULL),
(720, 3, 1013, NULL),
(721, 3, 1014, NULL),
(722, 3, 1015, NULL),
(723, 3, 1016, NULL),
(724, 3, 1017, NULL),
(725, 3, 1018, NULL),
(726, 3, 5026, NULL),
(727, 3, 102, NULL),
(728, 3, 1021, NULL),
(729, 3, 1022, NULL),
(730, 3, 1023, NULL),
(731, 3, 1024, NULL),
(732, 3, 1025, NULL),
(733, 3, 103, NULL),
(734, 3, 1031, NULL),
(735, 3, 1032, NULL),
(736, 3, 1033, NULL),
(737, 3, 1034, NULL),
(738, 3, 104, NULL),
(739, 3, 1041, NULL),
(740, 3, 1042, NULL),
(741, 3, 1043, NULL),
(742, 3, 1044, NULL),
(743, 3, 105, NULL),
(744, 3, 1051, NULL),
(745, 3, 1052, NULL),
(746, 3, 1053, NULL),
(747, 3, 1054, NULL),
(748, 3, 1055, NULL),
(749, 3, 106, NULL),
(750, 3, 1061, NULL),
(751, 3, 1062, NULL),
(752, 3, 1063, NULL),
(753, 3, 1064, NULL),
(754, 3, 1065, NULL),
(755, 3, 1066, NULL),
(756, 3, 107, NULL),
(757, 3, 1071, NULL),
(758, 3, 1072, NULL),
(759, 3, 1073, NULL),
(760, 3, 1074, NULL),
(761, 1, 21, NULL),
(762, 1, 503, NULL),
(763, 1, 600, NULL),
(764, 1, 601, NULL),
(765, 1, 5031, NULL),
(766, 1, 5032, NULL),
(767, 1, 5033, NULL),
(768, 1, 5034, NULL),
(769, 1, 5242, NULL),
(770, 1, 6011, NULL),
(771, 1, 6012, NULL),
(772, 1, 6013, NULL),
(773, 1, 6014, NULL),
(776, 2, 21, NULL),
(777, 2, 503, NULL),
(778, 2, 600, NULL),
(779, 2, 601, NULL),
(780, 2, 5031, NULL),
(781, 2, 5032, NULL),
(782, 2, 5033, NULL),
(783, 2, 5034, NULL),
(784, 2, 5242, NULL),
(785, 2, 6011, NULL),
(786, 2, 6012, NULL),
(787, 2, 6013, NULL),
(788, 2, 6014, NULL),
(791, 2, 7100, NULL),
(792, 2, 7101, NULL),
(793, 2, 7102, NULL),
(794, 2, 7103, NULL),
(795, 2, 7110, NULL),
(796, 2, 7111, NULL),
(797, 2, 7112, NULL),
(798, 2, 7113, NULL),
(799, 2, 7114, NULL),
(800, 1, 7100, NULL),
(801, 1, 7101, NULL),
(802, 1, 7102, NULL),
(803, 1, 7103, NULL),
(804, 1, 7110, NULL),
(805, 1, 7111, NULL),
(806, 1, 7112, NULL),
(807, 1, 7113, NULL),
(808, 1, 7114, NULL),
(809, 2, 7115, NULL),
(810, 2, 7120, NULL),
(811, 2, 7121, NULL),
(812, 2, 7130, NULL),
(813, 2, 7131, NULL),
(814, 2, 7132, NULL),
(815, 2, 7133, NULL),
(816, 2, 7134, NULL),
(817, 2, 7140, NULL),
(818, 2, 7141, NULL),
(819, 1, 7115, NULL),
(820, 1, 7120, NULL),
(821, 1, 7121, NULL),
(822, 1, 7130, NULL),
(823, 1, 7131, NULL),
(824, 1, 7132, NULL),
(825, 1, 7133, NULL),
(826, 1, 7134, NULL),
(827, 1, 7140, NULL),
(828, 1, 7141, NULL);

-- ----------------------------
-- Table structure for `sys_switch`
-- ----------------------------
DROP TABLE IF EXISTS `sys_switch`;
CREATE TABLE `sys_switch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '开关ID',
  `switch_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '开关名称',
  `switch_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '开关键（唯一标识，如: feature.newUI.enabled）',
  `switch_value` tinyint DEFAULT '1' COMMENT '开关状态（0:关闭, 1:开启）',
  `switch_type` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'feature' COMMENT '开关类型（feature:功能开关, gray:灰度开关, degrade:降级开关, experiment:实验开关）',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'default' COMMENT '开关分类（用于分组，如: user, order, payment）',
  `scope` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'global' COMMENT '作用范围（global:全局, tenant:租户级, user:用户级）',
  `strategy` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'all' COMMENT '生效策略（all:全部, whitelist:白名单, percentage:百分比, schedule:定时）',
  `whitelist` json DEFAULT NULL COMMENT '白名单配置（用户ID或租户ID列表）',
  `percentage` int DEFAULT '100' COMMENT '灰度百分比（0-100，strategy=percentage时生效）',
  `start_time` datetime DEFAULT NULL COMMENT '生效开始时间（strategy=schedule时生效）',
  `end_time` datetime DEFAULT NULL COMMENT '生效结束时间（strategy=schedule时生效）',
  `sort` int DEFAULT '0' COMMENT '显示排序',
  `description` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '开关描述',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注说明',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_switch_key` (`switch_key`),
  KEY `idx_switch_type` (`switch_type`),
  KEY `idx_category` (`category`),
  KEY `idx_switch_value` (`switch_value`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统开关表';


-- ----------------------------
-- Records of `sys_switch`
-- ----------------------------
INSERT INTO `sys_switch` (`id`, `switch_name`, `switch_key`, `switch_value`, `switch_type`, `category`, `scope`, `strategy`, `whitelist`, `percentage`, `start_time`, `end_time`, `sort`, `description`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(16, '用户注册开关', 'user.register.enabled', 1, 'feature', 'user', 'global', 'all', NULL, 100, NULL, NULL, 0, '是否允许新用户注册', NULL, NULL, NULL, NULL, '2026-01-25 22:23:36', NULL, '2026-01-25 22:23:36', 0),
(17, '手机号登录开关', 'user.login.phone.enabled', 1, 'feature', 'user', 'global', 'all', NULL, 100, NULL, NULL, 0, '是否允许手机号验证码登录', NULL, NULL, NULL, NULL, '2026-01-25 22:23:36', NULL, '2026-01-25 22:23:36', 0),
(18, '第三方登录开关', 'user.login.social.enabled', 1, 'feature', 'user', 'global', 'all', NULL, 100, NULL, NULL, 0, '是否允许第三方账号登录', NULL, NULL, NULL, NULL, '2026-01-25 22:23:36', NULL, '2026-01-25 22:23:36', 0),
(20, '验证码开关', 'security.captcha.enabled', 0, 'feature', 'security', 'global', 'all', NULL, 100, NULL, NULL, 0, '是否开启登录验证码', NULL, NULL, NULL, NULL, '2026-01-25 22:23:36', NULL, '2026-01-29 14:18:15', 0),
(24, '系统维护模式', 'system.maintenance.enabled', 0, 'degrade', 'system', 'global', 'all', NULL, 100, NULL, NULL, 0, '系统维护模式，开启后仅管理员可访问', NULL, NULL, NULL, NULL, '2026-01-25 22:23:36', NULL, '2026-01-25 22:23:36', 0),
(32, '微信登录收集手机号开关', 'wechat.collect.phone.enabled', 1, 'feature', 'wechat', 'global', 'all', NULL, 100, NULL, NULL, 0, '是否在微信登录时提示用户授权手机号', '', NULL, NULL, NULL, '2026-01-26 23:27:28', NULL, '2026-01-27 15:30:31', 0),
(34, '用户信息完善开关', 'user.profile.complete.enabled', 1, 'feature', 'user', 'global', 'all', NULL, 100, NULL, NULL, 0, '从枚举自动创建', NULL, NULL, NULL, NULL, NULL, NULL, '2026-01-27 15:45:04', 0),
(36, '签到开关', 'wallet.signin.enabled', 1, 'feature', 'system', 'global', 'all', NULL, 100, NULL, NULL, 0, NULL, '', NULL, NULL, NULL, NULL, NULL, '2026-01-29 14:18:15', NULL);

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt加密）',
  `password_set` tinyint DEFAULT '1' COMMENT '是否设置密码（0:否,1:是）',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '昵称',
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT '0' COMMENT '性别（0:未知, 1:男, 2:女）',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `home_path` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户登录后默认首页',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `introduction` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个人简介',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `data_scope` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'UserCreate' COMMENT '数据权限范围 (All, DeptAndChildren, Dept, UserCreate, JoinGroup)',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  `points` bigint NOT NULL DEFAULT '0' COMMENT '可用积分',
  `frozen_points` bigint NOT NULL DEFAULT '0' COMMENT '冻结积分',
  `reward_points` bigint DEFAULT '0' COMMENT '奖励积分（非充值获得的积分统计，如签到、赠送、活动奖励等）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_phone` (`phone`),
  KEY `idx_email` (`email`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_user_points` (`points`),
  KEY `idx_data_scope` (`data_scope`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';


-- ----------------------------
-- Records of `sys_user`
-- ----------------------------
INSERT INTO `sys_user` (`id`, `username`, `password`, `password_set`, `nickname`, `real_name`, `avatar`, `email`, `phone`, `gender`, `status`, `home_path`, `remark`, `introduction`, `tenant_id`, `dept_id`, `data_scope`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`, `points`, `frozen_points`, `reward_points`) VALUES
(1, 'root', '$2a$10$ADwBAjEg4peTpiSdft9NxOwQr.jhOeADsAl6pnEtnao20xJ9qUmUW', 1, 'Root', 'Root Admin', 'https://api.dicebear.com/9.x/bottts-neutral/svg?seed=root', 'root@triflow.com', '13800000001', 1, 1, NULL, '超级管理员账号', NULL, NULL, 1, 'All', NULL, '2026-01-23 14:46:15', NULL, '2026-03-01 18:27:54', 0, 20, 0, 20),
(2, 'admin', '$2a$10$ADwBAjEg4peTpiSdft9NxOwQr.jhOeADsAl6pnEtnao20xJ9qUmUW', 1, 'Admin', '系统管理员', 'https://api.dicebear.com/9.x/bottts-neutral/svg?seed=admin', 'admin@triflow.com', '13800000002', 1, 1, '/workspace', '系统管理员账号', '这是我的个人简介测试', NULL, 1, 'All', NULL, '2026-01-23 14:46:15', NULL, '2026-02-01 17:19:25', 0, 1243, 0, 0),
(3, 'jack', '$2a$10$ADwBAjEg4peTpiSdft9NxOwQr.jhOeADsAl6pnEtnao20xJ9qUmUW', 1, 'Jack', 'Jack Chen', 'https://api.dicebear.com/9.x/bottts-neutral/svg?seed=jack', 'jack@triflow.com', '13800000003', 1, 1, '/analytics', '普通用户账号', NULL, NULL, 3, 'DeptAndChildren', NULL, '2026-01-23 14:46:15', NULL, '2026-02-01 19:01:52', 0, 0, 0, 0);

-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';


-- ----------------------------
-- Records of `sys_user_role`
-- ----------------------------
INSERT INTO `sys_user_role` (`id`, `user_id`, `role_id`, `tenant_id`) VALUES
(1, 1, 1, NULL),
(2, 2, 2, NULL),
(16, 3, 3, NULL);

-- ----------------------------
-- Table structure for `sys_wechat_tabbar`
-- ----------------------------
DROP TABLE IF EXISTS `sys_wechat_tabbar`;
CREATE TABLE `sys_wechat_tabbar` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `text` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '菜单名称',
  `page_path` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '页面路径',
  `icon_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标类型（uiLib/unocss/iconfont/image）',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '图标资源',
  `icon_active` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选中图标资源',
  `badge` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '徽标（数字或dot）',
  `is_bulge` tinyint DEFAULT '0' COMMENT '是否鼓包（0:否, 1:是）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:启用）',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标识（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信小程序底部菜单配置表';


-- ----------------------------
-- Records of `sys_wechat_tabbar`
-- ----------------------------
INSERT INTO `sys_wechat_tabbar` (`id`, `text`, `page_path`, `icon_type`, `icon`, `icon_active`, `badge`, `is_bulge`, `sort`, `status`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, '首页', 'pages/index/index', 'unocss', 'i-carbon-home', '', '', 0, 1, 1, '默认首页菜单', NULL, NULL, NULL, '2026-01-27 22:33:26', NULL, '2026-01-28 02:06:48', 0),
(2, '我的', 'pages/me/me', 'unocss', 'i-carbon-user', '', '', 0, 3, 1, '默认个人菜单', NULL, NULL, NULL, '2026-01-27 22:33:26', NULL, '2026-01-28 01:51:46', 0),
(3, 'Demo', 'pages/demo/index', 'unocss', 'i-carbon-code', '', '', 0, 2, 1, 'Demo 演示页面', NULL, NULL, NULL, '2026-01-28 01:51:46', NULL, '2026-01-28 22:45:23', 0);

-- ----------------------------
-- Table structure for `wallet_recharge_config`
-- ----------------------------
DROP TABLE IF EXISTS `wallet_recharge_config`;
CREATE TABLE `wallet_recharge_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置名称',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '充值类型: points-积分, balance-余额',
  `pay_amount` decimal(12,2) NOT NULL COMMENT '支付金额（元）',
  `reward_amount` decimal(12,2) NOT NULL COMMENT '到账金额/积分',
  `bonus_amount` decimal(12,2) DEFAULT '0.00' COMMENT '赠送金额/积分',
  `status` tinyint DEFAULT '1' COMMENT '状态（0:禁用, 1:正常）',
  `sort` int DEFAULT '0' COMMENT '排序',
  `remark` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `tenant_id` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '数据权限部门ID',
  `create_by` bigint DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除（0:正常, 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包充值配置表';


-- ----------------------------
-- Records of `wallet_recharge_config`
-- ----------------------------
INSERT INTO `wallet_recharge_config` (`id`, `config_name`, `type`, `pay_amount`, `reward_amount`, `bonus_amount`, `status`, `sort`, `remark`, `tenant_id`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`, `deleted`) VALUES
(1, '10', 'points', '0.01', '2.00', '0.00', 1, 0, '', NULL, 1, 1, '2026-01-29 08:23:45', 1, '2026-01-29 08:23:45', NULL),
(2, '111', 'balance', '0.01', '0.01', '0.00', 1, 0, '', NULL, 1, 1, '2026-01-29 08:23:58', 1, '2026-01-29 08:23:58', NULL);

SET FOREIGN_KEY_CHECKS = 1;

SET FOREIGN_KEY_CHECKS = 1;
