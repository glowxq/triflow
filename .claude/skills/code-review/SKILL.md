---
name: code-review
description: Triflow 代码审查规范。对当前代码变更或指定文件/模块进行全面合规性检查，覆盖后端 Java、Web 前端、App 移动端的命名、架构、类型安全、数据库、API 等规范。使用此 skill 审查代码是否符合项目约定。
---

# Triflow 代码审查规范

> 使用此 Skill 对代码进行合规性审查。按以下清单逐项检查，报告不符合项并给出修复建议。

## 审查流程

1. **确定审查范围**：指定文件、目录、或 `git diff` 变更
2. **按清单逐项检查**：根据代码所属平台（Server/Web/App）选择对应章节
3. **输出审查报告**：按严重度分类（Error / Warning / Info）

---

## 1. 通用规范（全平台）

### 1.1 命名规范

- [ ] 文件名使用 `kebab-case`（Vue 组件、TS 文件）或 `PascalCase`（Java 类）
- [ ] 变量/函数使用 `camelCase`，常量使用 `UPPER_SNAKE_CASE`
- [ ] 类型/接口使用 `PascalCase`（如 `UserVO`、`UserQueryDTO`）
- [ ] 无拼写错误、无拼音命名、无无意义缩写

### 1.2 注释规范

- [ ] 公共 API、类、复杂逻辑有注释说明
- [ ] 无注释掉的代码（应删除而非注释）
- [ ] 无 TODO/FIXME 遗留（或已关联 Issue）

### 1.3 安全规范

- [ ] 无硬编码密码、密钥、Token
- [ ] 无 `console.log` 遗留（生产代码）
- [ ] 敏感操作有权限校验
- [ ] 用户输入有验证和清洗

### 1.4 Git 规范

- [ ] 提交信息符合 Conventional Commits（`feat:` / `fix:` / `refactor:` 等）
- [ ] 单次提交聚焦单一功能/修复
- [ ] 无无关文件（`.env`、`.idea/`、`node_modules/`）提交

---

## 2. Server 后端检查清单（Java）

### 2.1 分层架构

- [ ] Controller **继承 `BaseApi`**
- [ ] Controller 只做参数校验 + 调用 Service + 返回 `ApiResult`
- [ ] Service 层**不构造 `QueryWrapper`**（查询逻辑下放到 Mapper）
- [ ] Mapper 层封装所有查询条件（`default` 方法 + Lambda 表达式）

### 2.2 MyBatis-Flex 查询

- [ ] 使用 Lambda 表达式（`User::getUsername`），**无** `xxxTableDef` 静态字段
- [ ] **无**字符串硬编码字段名，**无**原生 SQL 拼接
- [ ] 条件查询使用三参数形式（`eq(field, value, condition)`）
- [ ] 分页查询：QueryDTO 继承 `PageQuery`，使用 `buildPage()` + `paginateAs`

### 2.3 实体与 POJO

- [ ] `@Table` 使用**模块前缀**（如 `sys_user`），**无** `t_` 前缀
- [ ] 所有类和字段有 **JavaDoc** 注释
- [ ] Java 类有 `@author` 和 `@since` 标签
- [ ] 状态字段使用 `@see` 指向枚举类，枚举实现 `BaseEnum` 接口
- [ ] DTO/VO 所有字段有 `@Schema` 注解（description 必填，推荐 example）
- [ ] DTO/VO 使用 `@AutoMapper(target = Entity.class)` 定义转换

### 2.4 数据转换

- [ ] 使用 `MapStructUtils.convert()`，**无** BeanUtils / Hutool / 手写 Convert
- [ ] DTO 与 Entity 字段类型严格匹配（`StrictTypeMapping`）

### 2.5 异常处理

- [ ] 使用 `Assert` 断言工具，**无** `if (x == null) throw new ...` 模式
- [ ] 错误码符合 TMMCCC 格式（6位）
- [ ] 异常类型正确：`BusinessException` / `AlertsException` / `ClientException`

### 2.6 代码风格

- [ ] 使用 `@RequiredArgsConstructor` + `private final` 构造器注入，**无** `@Autowired`
- [ ] **无** Hutool 依赖（`cn.hutool.*`），使用 Apache Commons
- [ ] Swagger 注解完整：`@Tag`、`@Operation`、`@Schema`
- [ ] `@OperationLog` 注解标注操作日志模块
- [ ] import 顺序：三方库 → Java 标准库 → 静态导入

### 2.7 数据库（Flyway 迁移）

- [ ] 表名使用**模块前缀**（`sys_`、`cms_`、`ord_` 等），**无** `t_` 前缀
- [ ] 业务表包含 **7 个必需字段**：`tenant_id`、`dept_id`、`create_by`、`create_time`、`update_by`、`update_time`、`deleted`
- [ ] 布尔字段**无** `is_` 前缀
- [ ] 字段使用 `snake_case`，主键为 `id`（BIGINT AUTO_INCREMENT）
- [ ] 索引命名：唯一索引 `uk_xxx`，普通索引 `idx_xxx`
- [ ] 字符集 `utf8mb4`，排序规则 `utf8mb4_unicode_ci`

### 2.8 认证与权限

- [ ] 需权限控制的接口有 `@SaCheckPermission` 或 `@SaCheckRole`
- [ ] 获取当前用户使用 `LoginUtils.getLoginUser()`
- [ ] 公开接口路径包含 `/public/` 标识

---

## 3. Web 前端检查清单（Vue + TypeScript）

### 3.1 组件规范

- [ ] 使用 `<script setup lang="ts">`
- [ ] 代码区域顺序：Props → Emits → State → Computed → Methods → Lifecycle → Expose
- [ ] Props 使用 TypeScript interface + `withDefaults` 定义
- [ ] 组件文件名 `kebab-case.vue`

### 3.2 TypeScript

- [ ] **无** `any` 类型（包括 `catch(error)` 也需正确类型化）
- [ ] 所有 API 函数有请求参数和响应类型定义
- [ ] 类型导入使用 `import type { ... }`
- [ ] 接口文件放在 `api/modules/` 下，类型与函数在同一文件

### 3.3 路径与导入

- [ ] 应用内导入使用 `#/` 别名（= `web-admin/src/`）
- [ ] 共享包使用 `@vben/xxx` 导入
- [ ] **无**相对路径穿越超过两层（`../../..`）

### 3.4 状态管理

- [ ] Pinia Store 使用 Setup Store 写法（`defineStore` + 箭头函数）
- [ ] 响应式数据解构使用 `storeToRefs()`，**无**直接解构
- [ ] Store 提供 `$reset` 方法

### 3.5 样式

- [ ] 自定义样式使用 SCSS + BEM 命名（`.block__element--modifier`）
- [ ] 优先使用 Tailwind CSS 工具类
- [ ] 样式书写顺序：定位 → 盒模型 → 排版 → 视觉 → 其他
- [ ] **无**内联样式（除动态计算值外）

### 3.6 国际化

- [ ] 用户可见文案使用 `t()` 函数
- [ ] **无**硬编码中文字符串（开发/调试除外）

### 3.7 复用性

- [ ] 已有封装组件优先使用（StatusTag、EnumSelect、ImageUpload 等）
- [ ] Hooks 遵循 `useXxx` 命名，定义 Options 和 Return 接口
- [ ] **无**重复造轮子（检查 `#/components/` 和 CHARTER.md）

---

## 4. App 移动端检查清单（UniApp）

### 4.1 API 路径

- [ ] 所有 API 路径包含 `/base/` 模块前缀
- [ ] 环境变量使用 `VITE_SERVER_BASEURL`（**无** `/base` 后缀）
- [ ] **无**硬编码 IP/域名

### 4.2 组件规范

- [ ] 使用 `<script setup lang="ts">`
- [ ] UI 优先使用 Wot Design Uni（`wd-xxx`）组件
- [ ] 页面中使用 Toast 前确保有 `<wd-toast />` 组件
- [ ] 组件文件名 `kebab-case.vue`

### 4.3 跨端兼容

- [ ] 条件显示优先使用 `v-if` 而非 `v-show`
- [ ] 平台差异代码使用 `#ifdef` / `#endif` 条件编译
- [ ] **无**小程序不支持的 CSS：`backdrop-filter`、`gap`、`aspect-ratio`、`@font-face`
- [ ] 渐变使用简单单层渐变或纯色
- [ ] 新功能已在 H5 和微信小程序两端测试

### 4.4 样式

- [ ] 优先使用 UnoCSS 原子类
- [ ] 尺寸优先使用 `rpx`（自适应），边框/小图标用 `px`
- [ ] 自定义样式使用 SCSS + BEM 命名
- [ ] 底部安全区域适配 `env(safe-area-inset-bottom)`

### 4.5 状态管理

- [ ] Pinia Store 使用 Setup Store + `persist` 配置
- [ ] 持久化 storage 使用 `uni.getStorageSync` / `uni.setStorageSync`
- [ ] 响应式数据解构使用 `storeToRefs()`

### 4.6 分页与数据

- [ ] 分页列表使用 `z-paging` 组件
- [ ] 系统开关使用 `SwitchKey` 枚举 + `getSwitchStatus()`，**无**硬编码开关键
- [ ] 网络图片有 `@error` 处理和 `lazy-load`

### 4.7 小程序专项

- [ ] 微信登录使用 `wx.login()` + code 换 token，**无**废弃的 `getUserInfo` open-type
- [ ] 小程序组件使用 `custom-class` 而非 `class`（外部样式传递）
- [ ] `scroll-view` 有明确高度设置
- [ ] 原生 `<button>` 已重置默认样式

---

## 5. 审查报告模板

```markdown
# 代码审查报告

**审查范围**: [文件/目录/PR]
**审查时间**: [日期]

## 概要

| 严重度 | 数量 |
|--------|------|
| Error  | X    |
| Warning| X    |
| Info   | X    |

## Error（必须修复）

### E1: [问题标题]
- **文件**: `path/to/file.java:42`
- **规则**: [违反的规则编号，如 2.2]
- **问题**: [具体描述]
- **修复**: [建议的修复方式]

## Warning（建议修复）

### W1: [问题标题]
...

## Info（改进建议）

### I1: [问题标题]
...

## 通过项

- [x] 分层架构正确
- [x] 命名规范符合
- ...
```

---

## 6. 自动化检查命令

```bash
# 后端
cd triflow-server
mvn compile                         # 编译检查（含 MapStruct 类型校验）
mvn checkstyle:check                # 代码风格检查（如已配置）

# Web 前端
cd triflow-web
pnpm lint                           # ESLint + Prettier
pnpm type-check                     # TypeScript 类型检查

# App 移动端
cd triflow-app
pnpm lint                           # ESLint
pnpm type-check                     # TypeScript 类型检查
```
