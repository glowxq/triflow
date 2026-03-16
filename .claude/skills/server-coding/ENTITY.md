# 实体类与 POJO 规范详解

## 1. Entity 实体类

### 标准实体模板

```java
/**
 * 用户实体类
 * <p>
 * 对应数据库表 sys_user，存储系统用户基本信息。
 * </p>
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Table("sys_user")
public class User implements BaseEntity {

    /** 用户ID（主键，自增） */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 用户名（唯一） */
    private String username;

    /** 邮箱地址 */
    private String email;

    /**
     * 状态
     * @see com.glowxq.triflow.base.system.enums.UserStatus
     */
    private Integer status;

    /** 逻辑删除标识（0:正常, 1:已删除） */
    @Column(isLogicDelete = true)
    private Integer deleted;

    // ========== 审计字段（自动填充，无需手动设置）==========

    /** 租户ID */
    private String tenantId;

    /** 部门ID（用于数据权限） */
    private Long deptId;

    /** 创建者ID */
    private Long createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新者ID */
    private Long updateBy;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
```

### 实体规则

- `@Table` 使用模块前缀命名（`sys_user`、`cms_article`），**禁止** `t_` 前缀
- 所有类和字段**必须**添加 JavaDoc 注释
- 状态类字段使用 `@see` 注解指向枚举类
- 审计字段由 `EntityChangeListener` 自动填充，业务代码无需手动设置

---

## 2. 枚举规范

### 所有业务枚举必须实现 BaseEnum 接口

```java
/**
 * 文章状态枚举
 *
 * @author glowxq
 * @since 2025-01-23
 */
@Getter
@AllArgsConstructor
public enum ArticleStatus implements BaseEnum {

    /** 草稿 - 文章尚未发布，仅作者可见 */
    DRAFT("0", "草稿"),

    /** 已发布 - 文章已公开发布 */
    PUBLISHED("1", "已发布"),

    /** 已下架 - 文章已从公开列表移除 */
    OFFLINE("2", "已下架"),
    ;

    /** 状态编码（数据库存储值） */
    private final String code;

    /** 状态名称（显示值） */
    private final String name;

    public Integer getValue() {
        return Integer.valueOf(code);
    }

    public static ArticleStatus of(Integer value) {
        if (value == null) return null;
        String code = String.valueOf(value);
        for (ArticleStatus status : values()) {
            if (status.getCode().equals(code)) return status;
        }
        return null;
    }
}
```

### 枚举放置位置

枚举类放在对应模块的 `enums` 包下：`com.glowxq.triflow.base.{module}.enums.XxxStatus`

---

## 3. DTO / VO 规范

### @Schema 注解必填

```java
/**
 * 用户创建请求 DTO
 *
 * @author glowxq
 * @since 2025-01-22
 */
@Data
@Schema(description = "用户创建请求")
@AutoMapper(target = User.class)
public class UserCreateDTO implements BaseDTO {

    @Schema(description = "用户名", example = "zhangsan", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度必须在2-20之间")
    private String username;

    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED, minLength = 6)
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "邮箱地址", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号", example = "13800138000", pattern = "^1[3-9]\\d{9}$")
    private String phone;

    @Schema(description = "角色ID列表", example = "[1, 2, 3]")
    private List<Long> roleIds;
}
```

### @Schema 常用属性

| 属性 | 说明 | 示例 |
|------|------|------|
| `description` | 字段描述（必填） | `"用户名"` |
| `example` | 示例值（推荐） | `"zhangsan"` |
| `requiredMode` | 是否必填 | `Schema.RequiredMode.REQUIRED` |
| `allowableValues` | 允许的值 | `{"0", "1"}` |
| `defaultValue` | 默认值 | `"1"` |
| `pattern` | 正则表达式 | `"^1[3-9]\\d{9}$"` |
| `hidden` | 是否隐藏 | `true` |

### VO 响应对象

```java
@Data
@Schema(description = "用户信息响应")
@AutoMapper(target = User.class)
public class UserVO implements BaseVO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "创建时间", example = "2025-01-22 10:30:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
```

---

## 4. 数据转换（MapStruct Plus）

### 核心规则

- DTO/VO 类添加 `@AutoMapper(target = Entity.class)`
- 使用 `MapStructUtils.convert()` 进行转换
- **禁止** Spring/Apache/Hutool BeanUtils、手写 Convert 接口

### 基本用法

```java
// 单对象转换
User user = MapStructUtils.convert(dto, User.class);
UserVO vo = MapStructUtils.convert(user, UserVO.class);

// 列表转换
List<UserVO> voList = MapStructUtils.convert(users, UserVO.class);

// 更新已有对象
MapStructUtils.convert(dto, existingUser);
```

### @AutoMapper 高级用法

```java
// 字段名不同时的映射
@Data
@AutoMapper(target = User.class)
public class UserDTO {
    @AutoMapping(target = "username")
    private String name;

    @AutoMapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;

    @AutoMapping(ignore = true)
    private String password;
}

// 一个类转换为多个目标类
@AutoMappers({
    @AutoMapper(target = UserVO.class),
    @AutoMapper(target = UserSimpleVO.class)
})
public class User { ... }
```

### StrictTypeMapping

项目已配置严格类型检查，**禁止隐式类型转换**。DTO 和 Entity 字段类型不一致时编译报错：

```java
// ❌ 编译报错：User.age 是 Integer，这里是 String
@AutoMapper(target = User.class)
public class UserDTO {
    private String age;  // ❌ 类型不匹配
}
```

---

## 5. Excel VO 规范

导入导出使用独立的 Excel VO 类，与普通 VO 分离：

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserExcelVO {

    @ExcelProperty("用户ID")
    @ColumnWidth(15)
    private Long id;

    @ExcelProperty("用户名")
    @ColumnWidth(20)
    private String username;

    @ExcelProperty("性别")
    @ColumnWidth(10)
    @DictFormat(readConverterExp = "0=男,1=女,2=未知", isSelected = true)
    private String gender;

    @ExcelProperty("状态")
    @ColumnWidth(12)
    @DictFormat(sourceClass = StatusDynamicSelect.class, isSelected = true)
    private String status;

    @ExcelProperty("部门")
    @ColumnWidth(15)
    @CellMerge
    private String department;
}
```

### Excel 注解

| 注解 | 说明 |
|------|------|
| `@ExcelProperty` | Excel 列标题映射 |
| `@ColumnWidth` | 列宽（字符数） |
| `@DictFormat` | 字典转换与下拉选项 |
| `@CellMerge` | 相同值单元格合并 |

### 导入导出接口标准

```java
@PostMapping("/export")
public void export(@RequestBody @Valid UserQueryDTO query, HttpServletResponse response);

@PostMapping("/import")
public ApiResult<ExcelResult<UserExcelVO>> importData(@RequestParam("file") MultipartFile file);

@GetMapping("/importTemplate")
public void importTemplate(HttpServletResponse response);
```

---

## 6. 审计字段规范

所有业务表必须包含以下 7 个标准字段（关联表除外）：

| 字段名 | Java 属性 | 类型 | 说明 |
|--------|-----------|------|------|
| `tenant_id` | `tenantId` | VARCHAR(50) | 租户ID |
| `dept_id` | `deptId` | BIGINT | 数据权限部门ID |
| `create_by` | `createBy` | BIGINT | 创建者ID |
| `create_time` | `createTime` | DATETIME | 创建时间 |
| `update_by` | `updateBy` | BIGINT | 更新者ID |
| `update_time` | `updateTime` | DATETIME | 更新时间 |
| `deleted` | `deleted` | TINYINT | 逻辑删除（0:正常, 1:已删除） |

> 审计字段由 `EntityChangeListener` 自动填充，业务代码无需手动设置。
