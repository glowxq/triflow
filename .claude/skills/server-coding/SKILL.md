---
name: server-coding
description: Triflow 后端 Java 开发规范。包括 MyBatis-Flex 查询、实体与 DTO/VO 定义、分层架构、数据库命名、异常处理、数据转换等项目约定。在编写或修改 triflow-server 代码时必须遵循。
---

# Triflow Server 后端开发规范

> 本 Skill 定义了 Triflow 后端 Java 代码的编写规范，所有代码变更必须遵守。

## 1. MyBatis-Flex 查询规范

### 核心规则

- **必须**使用 Lambda 表达式（如 `User::getUsername`）进行类型安全查询
- **禁止**使用 `xxxTableDef` 静态字段（如 `USER.STATUS`）
- **禁止**使用字符串硬编码字段名或原生 SQL 拼接
- **禁止**在 Service 层构造 QueryWrapper，所有查询逻辑下放到 Mapper 层

```java
// ✅ 正确 - Mapper 层使用 Lambda 查询
default User selectByUsername(String username) {
    QueryWrapper qw = QueryWrapper.create()
        .from(User.class)
        .eq(User::getUsername, username);
    return this.selectOneByQuery(qw);
}

// ✅ 正确 - 条件查询带第三个参数控制是否生效
default QueryWrapper buildQueryWrapper(UserQueryDTO query) {
    return QueryWrapper.create()
        .from(User.class)
        .eq(User::getStatus, query.getStatus(), query.getStatus() != null)
        .like(User::getUsername, query.getUsername(), StringUtils.isNotBlank(query.getUsername()))
        .orderBy(User::getCreateTime, false);  // false = DESC
}

// ❌ 禁止 - Service 层直接构造 QueryWrapper
public User getByUsername(String username) {
    return userMapper.selectOneByQuery(
        QueryWrapper.create().from(User.class).eq(User::getUsername, username)
    );  // ❌ 应在 Mapper 层封装
}
```

### 分页查询

- 查询 DTO **必须**继承 `PageQuery` 基类
- 使用 `query.buildPage()` 构建分页对象
- 使用 `paginateAs` 直接返回 VO 类型

```java
// Service 层
public Page<UserVO> page(UserQueryDTO query) {
    Page<UserVO> page = query.buildPage();
    QueryWrapper qw = userMapper.buildQueryWrapper(query);
    return userMapper.paginateAs(page, qw, UserVO.class);
}
```

### 关联查询

- 连表查询**必须**使用 `@RelationOneToMany` / `@RelationManyToOne` 等注解
- **禁止**手写 JOIN SQL

> 详细查询示例和模式见 [QUERY.md](./QUERY.md)

---

## 2. 实体类与 POJO 规范

### Entity 实体类

- `@Table` 使用**模块前缀**命名（如 `sys_user`、`cms_article`），**禁止** `t_` 前缀
- 所有类和字段**必须**添加 JavaDoc 注释
- 状态类字段使用 `@see` 指向枚举类，**禁止**在注释中罗列状态值
- 枚举类**必须**实现 `BaseEnum` 接口

```java
@Data
@Table("sys_user")  // ✅ 模块前缀
public class User implements BaseEntity {
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /**
     * 状态
     * @see com.glowxq.triflow.base.system.enums.UserStatus
     */
    private Integer status;  // ✅ 用 @see 指向枚举

    @Column(isLogicDelete = true)
    private Integer deleted;
}
```

### DTO / VO 规范

- 所有 DTO、VO 类及字段**必须**添加 `@Schema` 注解
- 使用 `@AutoMapper(target = Entity.class)` 定义转换关系
- 使用 `MapStructUtils.convert()` 进行转换，**禁止** BeanUtils / Hutool / 手写 Convert

```java
@Data
@Schema(description = "用户创建请求")
@AutoMapper(target = User.class)
public class UserCreateDTO implements BaseDTO {
    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;
}
```

### POJO 目录结构

```
pojo/
├── vo/    # View Object - 返回给前端的展示对象
├── dto/   # Data Transfer Object - 接收前端参数
├── bo/    # Business Object - 业务层内部传递
└── query/ # Query Object - 分页/条件查询参数
```

> 详细实体规范、枚举定义、Excel VO、审计字段见 [ENTITY.md](./ENTITY.md)

---

## 3. 分层架构规范

### 包结构

```
com.glowxq.triflow.{module}/
├── controller/     # HTTP 请求处理（继承 BaseApi）
├── service/        # 业务逻辑（禁止构造 QueryWrapper）
│   └── impl/
├── mapper/         # 数据访问（封装查询逻辑）
├── entity/         # 数据库实体
└── pojo/           # DTO / VO / BO
```

### 各层职责

| 层级 | 职责 | 返回类型 |
|------|------|----------|
| Controller | 参数校验、调用 Service、返回 ApiResult；**必须继承 BaseApi** | `ApiResult<T>` |
| Service | 业务逻辑、事务管理；**禁止构造 QueryWrapper** | Entity / VO / Page |
| Mapper | CRUD 和查询条件封装 | Entity / List |

### Controller 标准接口

路径结构：类级 `@RequestMapping("/base/{module}/{resource}")`，方法级用**具体路径**。

```java
@Slf4j
@Tag(name = "用户管理")
@RestController
@RequestMapping("/base/system/user")  // ✅ 类级定义完整前缀
@RequiredArgsConstructor
@OperationLog(module = ModuleEnum.System)
public class SysUserController extends BaseApi {  // ✅ 继承 BaseApi

    private final SysUserService userService;

    @PostMapping("/page")
    public ApiResult<List<UserVO>> page(@RequestBody UserQueryDTO query) { ... }

    @GetMapping("/{id}")
    public ApiResult<UserVO> detail(@PathVariable Long id) { ... }

    @PostMapping
    public ApiResult<Void> create(@RequestBody @Valid UserCreateDTO dto) { ... }

    @PutMapping
    public ApiResult<Void> update(@RequestBody @Valid UserUpdateDTO dto) { ... }

    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) { ... }

    @DeleteMapping("/batch")
    public ApiResult<Void> deleteBatch(@RequestBody @Valid Ids ids) { ... }

    @PostMapping("/export")
    public void export(@RequestBody @Valid UserQueryDTO query, HttpServletResponse response) { ... }
}
```

---

## 4. API 响应与异常处理

### 统一响应

```java
return ApiResult.success();                      // 无数据
return ApiResult.success(data);                  // 携带数据
return ApiResult.success(voList, page);          // 分页（自动解析 total/totalPage/current/limit）
return ApiResult.error(ErrorCodeEnum.PARAM_MISSING);  // 错误
```

### 错误码格式：TMMCCC（6位）

| 位 | 含义 | 说明 |
|----|------|------|
| T | 类型 | 1:业务异常 2:告警异常 3:客户端异常 |
| MM | 模块 | 00-99 |
| CCC | 序号 | 000-999 |

### 异常处理

- **优先**使用 `Assert` 断言工具（一行完成校验 + 异常抛出）
- 特殊场景手动 `throw new BusinessException / AlertsException / ClientException`

```java
// ✅ 推荐 - Assert 断言
Assert.notNull(user, ErrorCodeEnum.USER_NOT_FOUND);
Assert.isTrue(age >= 18, ErrorCodeEnum.PARAM_INVALID, "年龄必须大于18");

// ❌ 不推荐 - if-throw
if (user == null) {
    throw new BusinessException(ErrorCodeEnum.USER_NOT_FOUND);
}
```

---

## 5. 代码风格规范

### 工具类规则

- **禁止** Hutool（`cn.hutool.*`）
- **必须**使用 Apache Commons：
  - 字符串：`StringUtils`（commons-lang3）
  - 集合：`CollectionUtils`（commons-collections4）
  - 对象：`ObjectUtils`（commons-lang3）

### 数据转换规则

- **必须**使用 MapStruct Plus + `MapStructUtils`
- **禁止** Spring/Apache/Hutool BeanUtils、手写 Convert 接口、ServiceImpl 中的 convertToEntity 方法
- DTO/VO 类添加 `@AutoMapper(target = Entity.class)`
- 项目已配置 `StrictTypeMapping`，字段类型必须严格匹配

```java
// ✅ 使用 MapStructUtils
User user = MapStructUtils.convert(dto, User.class);
List<UserVO> voList = MapStructUtils.convert(users, UserVO.class);

// ❌ 禁止
BeanUtils.copyProperties(source, target);
```

### 依赖注入

- **必须**使用 `@RequiredArgsConstructor` + `private final` 构造器注入
- **禁止** `@Autowired` 字段注入

### import 排序

1. 第三方库（`cn.*`, `com.*`, `io.*`, `org.*`）
2. Java 标准库（`java.*`, `javax.*`, `jakarta.*`）
3. 静态导入（`import static ...`）

### JavaDoc

- 所有类、公共方法、字段**必须**添加 JavaDoc
- 类需包含 `@author` 和 `@since` 标签

### 认证与权限

- 使用 Sa-Token：`@SaCheckPermission` / `@SaCheckRole`
- 通过 `LoginUtils.getLoginUser()` 获取当前用户

---

## 6. 数据库规范

### 表命名

- 使用**模块前缀**（3位小写字母），**禁止** `t_` 前缀
- 常用前缀：`sys_`(系统)、`cms_`(内容)、`ord_`(订单)、`file_`(文件)、`log_`(日志)

### 必需字段（7个）

所有业务表必须包含：`dept_id`、`tenant_id`、`create_time`、`update_time`、`create_by`、`update_by`、`deleted`

### 其他命名规范

- 字段：`snake_case`
- 主键：`id`（BIGINT AUTO_INCREMENT）
- 外键：`{关联表}_id`
- **禁止** `is_` 前缀（包括布尔字段）
- 唯一索引：`uk_{字段名}`，普通索引：`idx_{字段名}`

> 详细数据库规范、SQL 模板、关联表规范见 [DATABASE.md](./DATABASE.md)

---

## 7. 统一枚举 API

后端所有业务枚举实现 `BaseEnum` 接口，系统启动时 `EnumRegistry` 自动扫描注册。

| 接口 | 说明 |
|------|------|
| `GET /base/enums` | 获取所有枚举名称 |
| `GET /base/enums/{enumClassName}` | 获取枚举选项 |
| `GET /base/enums/batch/{names}` | 批量获取枚举 |

前端通过 `EnumSelect` 组件或手动调用 API 获取枚举选项。
