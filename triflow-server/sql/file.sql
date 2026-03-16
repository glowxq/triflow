-- =====================================================
-- Triflow 文件管理模块数据库设计
-- 版本: V1.0.0
-- 模块前缀: file_ (文件管理模块)
-- 遵循 CONTRIBUTING.md 2.6 数据库表命名规范
-- =====================================================

-- =====================================================
-- 文件存储配置表 (file_config)
-- 支持多种存储方式：本地、阿里云OSS、腾讯云COS、MinIO等
-- =====================================================
CREATE TABLE `file_config`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_name`     VARCHAR(100) NOT NULL COMMENT '配置名称',
    `config_key`      VARCHAR(50)  NOT NULL COMMENT '配置标识（唯一，如: aliyun-oss, tencent-cos）',
    `storage_type`    VARCHAR(30)  NOT NULL COMMENT '存储类型（local:本地, aliyun:阿里云OSS, tencent:腾讯云COS, minio:MinIO, qiniu:七牛云）',
    `endpoint`        VARCHAR(200) DEFAULT NULL COMMENT '服务端点（如: oss-cn-hangzhou.aliyuncs.com）',
    `access_key`      VARCHAR(200) DEFAULT NULL COMMENT 'AccessKey（加密存储）',
    `secret_key`      VARCHAR(200) DEFAULT NULL COMMENT 'SecretKey（加密存储）',
    `bucket_name`     VARCHAR(100) DEFAULT NULL COMMENT '存储桶名称',
    `region`          VARCHAR(50)  DEFAULT NULL COMMENT '存储区域',
    `domain`          VARCHAR(200) DEFAULT NULL COMMENT '自定义域名（CDN加速域名）',
    `base_path`       VARCHAR(200) DEFAULT '' COMMENT '基础路径（文件存储的根目录）',
    `use_https`       TINYINT      DEFAULT 1 COMMENT '是否使用HTTPS（0:否, 1:是）',
    `defaulted`       TINYINT      DEFAULT 0 COMMENT '是否默认配置（0:否, 1:是，仅一个）',
    `status`          TINYINT      DEFAULT 1 COMMENT '状态（0:禁用, 1:正常）',
    `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    -- ========== 必需标准字段 ==========
    `tenant_id`       VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`         BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`       BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`, `tenant_id`, `deleted`),
    KEY               `idx_storage_type` (`storage_type`),
    KEY               `idx_defaulted` (`defaulted`),
    KEY               `idx_status` (`status`),
    KEY               `idx_tenant_id` (`tenant_id`),
    KEY               `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件存储配置表';

-- =====================================================
-- 文件信息表 (file_info)
-- 记录所有上传文件的元数据信息
-- =====================================================
CREATE TABLE `file_info`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    `config_id`       BIGINT       DEFAULT NULL COMMENT '存储配置ID',
    `file_name`       VARCHAR(200) NOT NULL COMMENT '文件名（存储名，如: 20250124/abc123.jpg）',
    `original_name`   VARCHAR(200) DEFAULT NULL COMMENT '原始文件名（上传时的文件名）',
    `file_path`       VARCHAR(500) NOT NULL COMMENT '文件路径（完整的存储路径）',
    `file_url`        VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
    `file_size`       BIGINT       DEFAULT 0 COMMENT '文件大小（字节）',
    `file_type`       VARCHAR(100) DEFAULT NULL COMMENT '文件MIME类型（如: image/jpeg, application/pdf）',
    `file_ext`        VARCHAR(20)  DEFAULT NULL COMMENT '文件扩展名（如: jpg, pdf）',
    `storage_type`    VARCHAR(30)  DEFAULT 'local' COMMENT '存储类型（local:本地, aliyun:阿里云, tencent:腾讯云, minio:MinIO）',
    `bucket_name`     VARCHAR(100) DEFAULT NULL COMMENT '存储桶名称',
    `object_key`      VARCHAR(500) DEFAULT NULL COMMENT '对象键（云存储中的唯一标识）',
    `md5`             VARCHAR(32)  DEFAULT NULL COMMENT '文件MD5值（用于秒传和去重）',
    `sha256`          VARCHAR(64)  DEFAULT NULL COMMENT '文件SHA256值（安全校验）',
    `category`        VARCHAR(50)  DEFAULT 'default' COMMENT '文件分类（avatar:头像, document:文档, image:图片, video:视频, audio:音频, other:其他）',
    `biz_type`        VARCHAR(50)  DEFAULT NULL COMMENT '业务类型（关联的业务模块，如: user, article）',
    `biz_id`          BIGINT       DEFAULT NULL COMMENT '业务ID（关联的业务记录ID）',
    `uploader_id`     BIGINT       DEFAULT NULL COMMENT '上传者ID',
    `uploader_name`   VARCHAR(50)  DEFAULT NULL COMMENT '上传者名称',
    `upload_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    `public_access`   TINYINT      DEFAULT 1 COMMENT '是否公开访问（0:私有, 1:公开）',
    `expire_time`     DATETIME     DEFAULT NULL COMMENT '过期时间（临时文件使用）',
    `download_count`  INT          DEFAULT 0 COMMENT '下载次数',
    `last_access_time` DATETIME    DEFAULT NULL COMMENT '最后访问时间',
    `status`          TINYINT      DEFAULT 1 COMMENT '状态（0:待处理, 1:正常, 2:处理中, 3:处理失败）',
    `remark`          VARCHAR(500) DEFAULT NULL COMMENT '备注说明',
    -- ========== 必需标准字段 ==========
    `tenant_id`       VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`         BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`       BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`       BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_object_key` (`storage_type`, `bucket_name`, `object_key`),
    KEY               `idx_md5` (`md5`),
    KEY               `idx_category` (`category`),
    KEY               `idx_biz` (`biz_type`, `biz_id`),
    KEY               `idx_uploader_id` (`uploader_id`),
    KEY               `idx_upload_time` (`upload_time`),
    KEY               `idx_file_ext` (`file_ext`),
    KEY               `idx_status` (`status`),
    KEY               `idx_tenant_id` (`tenant_id`),
    KEY               `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件信息表';

-- =====================================================
-- 文件分片上传记录表 (file_chunk)
-- 支持大文件分片上传、断点续传
-- =====================================================
CREATE TABLE `file_chunk`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '分片ID',
    `upload_id`       VARCHAR(100) NOT NULL COMMENT '上传任务ID（唯一标识一次分片上传）',
    `file_md5`        VARCHAR(32)  NOT NULL COMMENT '完整文件MD5',
    `file_name`       VARCHAR(200) DEFAULT NULL COMMENT '文件名',
    `file_size`       BIGINT       NOT NULL COMMENT '文件总大小（字节）',
    `chunk_size`      INT          NOT NULL COMMENT '分片大小（字节）',
    `chunk_count`     INT          NOT NULL COMMENT '分片总数',
    `chunk_index`     INT          NOT NULL COMMENT '当前分片索引（从0开始）',
    `chunk_md5`       VARCHAR(32)  DEFAULT NULL COMMENT '分片MD5',
    `chunk_path`      VARCHAR(500) DEFAULT NULL COMMENT '分片存储路径',
    `storage_type`    VARCHAR(30)  DEFAULT 'local' COMMENT '存储类型',
    `bucket_name`     VARCHAR(100) DEFAULT NULL COMMENT '存储桶名称',
    `status`          TINYINT      DEFAULT 0 COMMENT '状态（0:上传中, 1:已完成, 2:已合并, 3:失败）',
    `expire_time`     DATETIME     DEFAULT NULL COMMENT '过期时间（临时分片的清理时间）',
    `tenant_id`       VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `create_by`       BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_upload_chunk` (`upload_id`, `chunk_index`),
    KEY               `idx_file_md5` (`file_md5`),
    KEY               `idx_upload_id` (`upload_id`),
    KEY               `idx_status` (`status`),
    KEY               `idx_expire_time` (`expire_time`),
    KEY               `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='文件分片上传记录表';

-- =====================================================
-- 初始化存储配置数据
-- =====================================================
INSERT INTO `file_config` (`config_name`, `config_key`, `storage_type`, `bucket_name`, `base_path`, `defaulted`, `status`, `remark`)
VALUES
    ('本地存储', 'local', 'local', 'triflow', '/upload', 1, 1, '本地文件存储，适用于开发环境');
