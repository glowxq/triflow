# 枚举注册器 (EnumRegistry) 原理文档

## 概述

`EnumRegistry` 是一个基于 Spring 的枚举自动扫描与注册组件，它在应用启动时自动发现所有实现 `BaseEnum` 接口的枚举类，并提供统一的查询 API。

## 核心原理

### 1. 扫描机制

```
┌─────────────────────────────────────────────────────────────┐
│                    应用启动 (@PostConstruct)                  │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│           ClassPathScanningCandidateComponentProvider        │
│                                                              │
│  扫描条件：                                                   │
│  • 基础包：com.glowxq                                         │
│  • 过滤器：AssignableTypeFilter(BaseEnum.class)               │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                    发现 BeanDefinition 列表                   │
│                                                              │
│  StatusEnum, BooleanEnum, GrantType, DeleteFlagEnum...       │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                     反射加载枚举常量                          │
│                                                              │
│  Class.forName() → getEnumConstants() → BaseEnum 转换        │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│              存入 ConcurrentHashMap 缓存                      │
│                                                              │
│  enumCache: { "StatusEnum" → [EnumItemDTO...] }              │
│  fullNameMapping: { "StatusEnum" → "com.xxx.StatusEnum" }    │
└─────────────────────────────────────────────────────────────┘
```

### 2. 关键组件说明

#### 2.1 ClassPathScanningCandidateComponentProvider

Spring Framework 提供的类路径扫描器，支持自定义过滤条件：

```java
ClassPathScanningCandidateComponentProvider scanner =
    new ClassPathScanningCandidateComponentProvider(false); // false = 不使用默认过滤器
scanner.addIncludeFilter(new AssignableTypeFilter(BaseEnum.class)); // 只扫描 BaseEnum 子类
Set<BeanDefinition> candidates = scanner.findCandidateComponents("com.glowxq");
```

#### 2.2 AssignableTypeFilter

类型匹配过滤器，用于筛选实现指定接口或继承指定类的组件：

```java
// 匹配所有实现 BaseEnum 接口的类
new AssignableTypeFilter(BaseEnum.class)
```

#### 2.3 反射获取枚举常量

```java
Class<?> clazz = Class.forName(className);
if (clazz.isEnum()) {
    Object[] enumConstants = clazz.getEnumConstants(); // 获取所有枚举值
    for (Object constant : enumConstants) {
        if (constant instanceof BaseEnum baseEnum) {
            // 提取 code 和 name
            items.add(new EnumItemDTO(baseEnum.getCode(), baseEnum.getName()));
        }
    }
}
```

### 3. 数据结构

```java
// 枚举缓存：简称 → 选项列表
private final Map<String, List<EnumItemDTO>> enumCache = new ConcurrentHashMap<>();

// 全名映射：简称 → 全限定类名
private final Map<String, String> fullNameMapping = new ConcurrentHashMap<>();

// 枚举选项 DTO
public record EnumItemDTO(String code, String name) {}
```

### 4. 线程安全

- 使用 `ConcurrentHashMap` 确保并发读写安全
- 初始化在 `@PostConstruct` 阶段完成，后续仅读取操作
- 适合高并发访问场景

## BaseEnum 接口

```java
public interface BaseEnum {
    /**
     * 获取枚举编码（存储值）
     */
    String getCode();

    /**
     * 获取枚举名称（显示值）
     */
    String getName();
}
```

## 使用示例

### 1. 定义枚举

```java
@Getter
@RequiredArgsConstructor
public enum StatusEnum implements BaseEnum {
    DISABLED("0", "禁用"),
    ENABLED("1", "正常");

    private final String code;
    private final String name;
}
```

### 2. 调用 API

```bash
# 获取所有枚举名称
GET /base/enums
→ ["StatusEnum", "BooleanEnum", "GrantType", ...]

# 获取单个枚举选项
GET /base/enums/StatusEnum
→ [{"code": "0", "name": "禁用"}, {"code": "1", "name": "正常"}]

# 批量获取枚举
GET /base/enums/batch/StatusEnum,BooleanEnum
→ {"StatusEnum": [...], "BooleanEnum": [...]}
```

### 3. 前端组件

```vue
<template>
  <EnumSelect v-model="form.status" enum-class="StatusEnum" />
</template>
```

## 优势

| 特性 | 说明 |
|------|------|
| **零配置** | 自动发现，无需手动注册 |
| **启动时缓存** | 一次扫描，后续从内存读取 |
| **统一接口** | 所有枚举通过相同 API 访问 |
| **类型安全** | 基于接口约束，编译期检查 |
| **前后端一致** | 避免硬编码，确保数据同步 |

## 执行时序

```
Spring 启动
    │
    ├── 创建 EnumRegistry Bean
    │
    ├── @PostConstruct init()
    │       │
    │       ├── scanEnums()
    │       │       │
    │       │       ├── 扫描 com.glowxq 包
    │       │       ├── 发现 28 个 BaseEnum 实现类
    │       │       └── 缓存到 enumCache
    │       │
    │       └── log.info("found 28 enum classes")
    │
    └── 应用就绪，可接受请求
```

## 注意事项

1. **包路径**：默认扫描 `com.glowxq` 包，如需修改请调整 `BASE_PACKAGE` 常量
2. **枚举规范**：必须实现 `BaseEnum` 接口才会被扫描
3. **命名唯一**：枚举简称（如 `StatusEnum`）必须全局唯一
4. **热加载**：新增枚举需要重启应用才能生效

## 相关文件

- `EnumRegistry.java` - 枚举注册器核心类
- `EnumController.java` - 枚举 API 控制器
- `EnumItem.java` - 枚举选项 VO
- `BaseEnum.java` - 枚举基础接口
