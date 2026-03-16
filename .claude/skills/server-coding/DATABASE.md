# 数据库规范详解

## 1. 表命名规范

### 核心原则：使用模块前缀，禁止 `t_` 前缀

| 模块类型 | 前缀 | 示例 |
|---------|------|------|
| 系统模块 | `sys_` | `sys_user`, `sys_role`, `sys_menu`, `sys_dept` |
| 内容模块 | `cms_` | `cms_article`, `cms_category`, `cms_tag` |
| 订单模块 | `ord_` | `ord_order`, `ord_order_item`, `ord_payment` |
| 商品模块 | `pms_` | `pms_product`, `pms_category`, `pms_brand` |
| 会员模块 | `ums_` | `ums_member`, `ums_member_level`, `ums_address` |
| 营销模块 | `sms_` | `sms_coupon`, `sms_promotion` |
| 库存模块 | `wms_` | `wms_warehouse`, `wms_stock` |
| 财务模块 | `fin_` | `fin_account`, `fin_transaction` |
| 日志模块 | `log_` | `log_login`, `log_operation` |
| 消息模块 | `msg_` | `msg_notice`, `msg_template` |
| 文件模块 | `file_` | `file_info`, `file_config` |

新增模块命名规则：使用 **3-4 位小写字母** 前缀 + `_`，表意清晰。

```java
@Table("cms_article")    // ✅ 内容模块文章表
@Table("sys_user")       // ✅ 系统模块用户表
@Table("t_article")      // ❌ 禁止通用 t_ 前缀
```

---

## 2. 标准业务表模板（7 个必需字段）

```sql
CREATE TABLE `{module}_{table_name}`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    -- ========== 业务字段 ==========
    -- ... 根据业务需求添加 ...

    -- ========== 必需标准字段 ==========
    `tenant_id`   VARCHAR(50)  DEFAULT NULL COMMENT '租户ID',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者ID',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者ID',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`     TINYINT      DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',

    PRIMARY KEY (`id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '{表注释}';
```

### 示例 - 文章表

```sql
CREATE TABLE `cms_article`
(
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title`        VARCHAR(200)  NOT NULL COMMENT '文章标题',
    `content`      LONGTEXT      DEFAULT NULL COMMENT '文章内容',
    `category_id`  BIGINT        DEFAULT NULL COMMENT '分类ID',
    `cover_image`  VARCHAR(500)  DEFAULT NULL COMMENT '封面图片',
    `status`       TINYINT       DEFAULT 0 COMMENT '状态（0:草稿, 1:已发布, 2:已下架）',
    `view_count`   INT           DEFAULT 0 COMMENT '阅读量',
    `publish_time` DATETIME      DEFAULT NULL COMMENT '发布时间',
    `remark`       VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    -- ========== 必需标准字段 ==========
    `tenant_id`    VARCHAR(50)   DEFAULT NULL COMMENT '租户ID',
    `dept_id`      BIGINT        DEFAULT NULL COMMENT '数据权限部门ID',
    `create_by`    BIGINT        DEFAULT NULL COMMENT '创建者ID',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    BIGINT        DEFAULT NULL COMMENT '更新者ID',
    `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`      TINYINT       DEFAULT 0 COMMENT '逻辑删除（0:正常, 1:已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_publish_time` (`publish_time`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '文章表';
```

---

## 3. 关联表规范

关联表（多对多中间表）仅需关联字段和 `tenant_id`，不需要审计字段：

```sql
CREATE TABLE `sys_user_role`
(
    `id`        BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id`   BIGINT      NOT NULL COMMENT '用户ID',
    `role_id`   BIGINT      NOT NULL COMMENT '角色ID',
    `tenant_id` VARCHAR(50) DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表';
```

---

## 4. 字段命名规范

| 规则 | 说明 | 示例 |
|------|------|------|
| 字段命名 | `snake_case` | `create_time`, `user_name` |
| 主键字段 | 统一 `id`，BIGINT AUTO_INCREMENT | `id` |
| 外键字段 | `{关联表}_id` | `user_id`, `dept_id` |
| 时间字段 | `{xxx}_time` | `create_time`, `publish_time` |
| 布尔字段 | **禁止** `is_` 前缀 | `enabled`, `visible`, `deleted` |
| 唯一索引 | `uk_{字段名}` | `uk_username`, `uk_user_role` |
| 普通索引 | `idx_{字段名}` | `idx_status`, `idx_create_time` |

### 布尔字段禁止 `is_` 前缀

遵循《阿里巴巴 Java 开发手册》规约：

```sql
-- ❌ 禁止
`is_enabled` TINYINT COMMENT '是否启用'
`is_deleted` TINYINT COMMENT '是否删除'

-- ✅ 正确
`enabled`  TINYINT COMMENT '是否启用'
`deleted`  TINYINT COMMENT '逻辑删除（0:正常, 1:已删除）'
`visible`  TINYINT COMMENT '是否可见'
`cacheable` TINYINT COMMENT '是否可缓存'
```
