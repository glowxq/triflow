# MyBatis-Flex 查询规范详解

## 1. Lambda 查询（必须）

### QueryWrapper 基础用法

```java
// 条件查询 - 第三个参数控制条件是否生效
QueryWrapper qw = QueryWrapper.create()
    .from(User.class)
    .eq(User::getStatus, query.getStatus(), query.getStatus() != null)
    .like(User::getUsername, query.getUsername(), StringUtils.isNotBlank(query.getUsername()))
    .between(User::getCreateTime, query.getStartTime(), query.getEndTime(),
             query.getStartTime() != null && query.getEndTime() != null)
    .orderBy(User::getCreateTime, false);  // false = DESC
```

### 禁止用法

```java
// ❌ 禁止 - 字符串硬编码
QueryWrapper.create().where("status = ?", 1);

// ❌ 禁止 - 原生 SQL
String sql = "SELECT * FROM user WHERE status = " + status;

// ❌ 禁止 - TableDef 静态字段
import static com.glowxq.business.entity.table.UserTableDef.USER;
QueryWrapper.create().select(USER.ALL_COLUMNS).from(USER).where(USER.STATUS.eq(1));
```

---

## 2. SQL 下放到 Mapper 层

### Service 层禁止构造 QueryWrapper

```java
// ❌ 禁止 - Service 层直接用 QueryWrapper
public User getByUsername(String username) {
    return userMapper.selectOneByQuery(
        QueryWrapper.create().from(User.class).eq(User::getUsername, username)
    );
}

// ✅ 正确 - 调用 Mapper 封装的方法
public User getByUsername(String username) {
    return userMapper.selectByUsername(username);
}
```

### Mapper 层封装查询

```java
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    default User selectByUsername(String username) {
        QueryWrapper qw = QueryWrapper.create()
            .from(User.class)
            .eq(User::getUsername, username);
        return this.selectOneByQuery(qw);
    }

    /**
     * 检查用户名是否重复（排除指定ID）
     */
    default User selectDuplicateByUsername(String username, Long excludeId) {
        QueryWrapper qw = QueryWrapper.create()
            .from(User.class)
            .eq(User::getUsername, username)
            .ne(User::getId, excludeId, excludeId != null);
        return this.selectOneByQuery(qw);
    }

    /**
     * 构建查询条件（用于分页等场景）
     */
    default QueryWrapper buildQueryWrapper(UserQueryDTO query) {
        return QueryWrapper.create()
            .from(User.class)
            .eq(User::getStatus, query.getStatus(), query.getStatus() != null)
            .like(User::getUsername, query.getUsername(), StringUtils.isNotBlank(query.getUsername()))
            .between(User::getCreateTime, query.getStartTime(), query.getEndTime(),
                     query.getStartTime() != null && query.getEndTime() != null)
            .orderBy(User::getCreateTime, false);
    }
}
```

---

## 3. 分页查询

### QueryDTO 必须继承 PageQuery

```java
@Data
@Schema(description = "用户查询请求")
public class UserQueryDTO extends PageQuery implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "状态")
    private Integer status;
}
```

### Service 层分页

```java
@Override
public Page<UserVO> page(UserQueryDTO query) {
    // ✅ 使用 buildPage() 构建分页对象
    Page<UserVO> page = query.buildPage();
    // ✅ 使用 Mapper 层的 buildQueryWrapper
    QueryWrapper qw = userMapper.buildQueryWrapper(query);
    // ✅ 使用 paginateAs 直接返回 VO 类型
    return userMapper.paginateAs(page, qw, UserVO.class);
}
```

### Controller 层返回

```java
@PostMapping("/page")
public ApiResult<List<UserVO>> page(@RequestBody UserQueryDTO query) {
    Page<UserVO> page = userService.page(query);
    return ApiResult.success(page.getRecords(), page);  // 分页信息自动解析
}
```

---

## 4. 关联查询

### 使用 @Relation 注解（禁止手写 JOIN）

```java
@Data
@Table("sys_user")
public class User implements BaseEntity {
    @Id(keyType = KeyType.Auto)
    private Long id;
    private String username;
    private Long deptId;

    /** 关联的部门信息（自动查询） */
    @RelationManyToOne(selfField = "deptId", targetField = "id")
    private Dept dept;

    /** 关联的角色列表（自动查询） */
    @RelationManyToMany(
        joinTable = "sys_user_role",
        selfField = "id", joinSelfColumn = "user_id",
        targetField = "id", joinTargetColumn = "role_id"
    )
    private List<Role> roles;
}

// 查询时自动关联
User user = userMapper.selectOneWithRelationsById(userId);
List<User> users = userMapper.selectListWithRelationsByQuery(queryWrapper);
```

> 参考文档：https://mybatis-flex.com/zh/base/query.html#relations-注解查询
