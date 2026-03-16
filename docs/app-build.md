# Triflow App 打包指南

> 本文档详细说明如何将 Triflow App 打包成 Android APK 和 iOS IPA

## 目录

- [环境准备](#环境准备)
- [Android 打包](#android-打包)
  - [方式一：云打包（推荐）](#方式一云打包推荐)
  - [方式二：离线打包](#方式二离线打包)
- [iOS 打包](#ios-打包)
- [常见问题](#常见问题)

---

## 环境准备

### 基础环境

| 工具 | 版本要求 | 用途 |
|------|----------|------|
| Node.js | 20+ | 运行环境 |
| pnpm | 9+ | 包管理器 |
| HBuilderX | 最新版 | 云打包工具 |

### 下载 HBuilderX

- 官网下载：https://www.dcloud.io/hbuilderx.html
- **选择 App 开发版**（包含原生插件支持）

### 项目配置检查

打包前请确认以下配置：

```
env/.env 文件中：
- VITE_UNI_APPID=__UNI__C60FF82  # DCloud AppID
- VITE_APP_TITLE=Triflow          # 应用名称

manifest.config.ts 文件中：
- versionName: '1.0.0'            # 版本号
- versionCode: '100'              # 版本码（每次发布需递增）
```

### 应用图标

图标文件位于 `src/static/app/icons/` 目录，已包含所有必需尺寸：

| 平台 | 尺寸 | 文件 |
|------|------|------|
| Android hdpi | 72x72 | ✅ 已配置 |
| Android xhdpi | 96x96 | ✅ 已配置 |
| Android xxhdpi | 144x144 | ✅ 已配置 |
| Android xxxhdpi | 192x192 | ✅ 已配置 |
| iOS App Store | 1024x1024 | ✅ 已配置 |
| iOS iPhone | 多尺寸 | ✅ 已配置 |
| iOS iPad | 多尺寸 | ✅ 已配置 |

---

## Android 打包

### 方式一：云打包（推荐）

云打包使用 DCloud 服务器，无需配置 Android 开发环境，适合快速出包。

#### 步骤 1：构建 App 资源

```bash
cd triflow-app

# 生产环境构建
pnpm build:app:prod

# 构建完成后，资源位于 dist/build/app 目录
```

#### 步骤 2：使用 HBuilderX 打包

1. **打开 HBuilderX**

2. **导入项目**
   - 菜单：`文件` → `导入` → `从本地目录导入`
   - 选择目录：`triflow-app/dist/build/app`

3. **发起云打包**
   - 菜单：`发行` → `原生App-云打包`

4. **配置打包选项**

   | 配置项 | 说明 |
   |--------|------|
   | Android 包名 | `com.glowxq.triflow`（建议格式） |
   | 证书 | 选择"使用公共测试证书"（测试用）或自有证书 |
   | 打包类型 | 选择需要的 CPU 架构 |

5. **等待打包完成**
   - 云打包通常需要 5-15 分钟
   - 完成后会提供 APK 下载链接

#### 步骤 3：安装测试

```bash
# 使用 adb 安装到手机（需开启 USB 调试）
adb install -r your-app.apk

# 或直接将 APK 传输到手机安装
```

---

### 方式二：离线打包

离线打包需要配置 Android Studio，适合需要自定义原生功能的场景。

#### 前置要求

- Android Studio（最新版）
- Android SDK（API 30+）
- JDK 11 或 17

#### 步骤 1：下载离线 SDK

下载地址：https://nativesupport.dcloud.net.cn/AppDocs/download/android.html

解压后目录结构：
```
Android-SDK/
├── HBuilder-Integrate-AS/    # Android Studio 工程
├── SDK/                      # SDK 文件
└── Feature-SDK/             # 功能模块
```

#### 步骤 2：构建 App 资源

```bash
cd triflow-app
pnpm build:app:prod
```

#### 步骤 3：复制资源到 SDK

```bash
# 将构建产物复制到 SDK 工程
# 目标路径：HBuilder-Integrate-AS/simpleDemo/src/main/assets/apps/__UNI__C60FF82/www

cp -r dist/build/app/* \
  /path/to/Android-SDK/HBuilder-Integrate-AS/simpleDemo/src/main/assets/apps/__UNI__C60FF82/www/
```

#### 步骤 4：生成签名证书

```bash
# 生成 Android 签名证书（首次需要）
keytool -genkey -alias triflow \
  -keyalg RSA \
  -keysize 2048 \
  -validity 36500 \
  -keystore triflow.keystore \
  -storepass your_store_password \
  -keypass your_key_password

# 按提示填写证书信息：
# - 姓名（CN）
# - 组织单位（OU）
# - 组织（O）
# - 城市（L）
# - 省份（ST）
# - 国家代码（C）：CN
```

#### 步骤 5：配置并打包

1. 用 Android Studio 打开 `HBuilder-Integrate-AS` 工程
2. 修改 `app/build.gradle` 中的包名和签名配置
3. `Build` → `Generate Signed Bundle/APK` → 选择 APK
4. 选择签名证书，完成打包

---

## iOS 打包

### 前置要求

- macOS 系统
- Xcode（最新版）
- Apple Developer 账号（¥688/年）

### 步骤 1：准备证书

1. 登录 https://developer.apple.com
2. 创建 App ID（Bundle ID：`com.glowxq.triflow`）
3. 创建开发/发布证书
4. 创建 Provisioning Profile

### 步骤 2：云打包

```bash
# 构建资源
pnpm build:app:prod
```

在 HBuilderX 中：
1. 导入 `dist/build/app` 目录
2. `发行` → `原生App-云打包`
3. 选择 iOS 平台
4. 上传证书和描述文件
5. 等待打包完成

### 步骤 3：离线打包

下载 iOS SDK：https://nativesupport.dcloud.net.cn/AppDocs/download/ios.html

使用 Xcode 打开工程，配置签名后 Archive 导出 IPA。

---

## 常见问题

### Q1: 云打包失败，提示证书问题

**解决方案**：
- 测试阶段使用"公共测试证书"
- 正式发布需要自己的签名证书

### Q2: 安装后闪退

**可能原因**：
1. API 地址配置错误，检查 `env/.env.production` 中的 `VITE_SERVER_BASEURL`
2. 权限未正确配置

### Q3: 打包后资源加载失败

**解决方案**：
1. 确保 `pnpm build:app:prod` 成功完成
2. 检查 `dist/build/app` 目录结构是否完整

### Q4: 如何更新版本号

修改 `manifest.config.ts`：
```typescript
'versionName': '1.0.1',  // 显示版本号
'versionCode': '101',    // 内部版本码，必须递增
```

### Q5: 如何添加更多 Android 权限

修改 `manifest.config.ts` 中的 `app-plus.distribute.android.permissions` 数组。

---

## 快速命令参考

```bash
# 开发调试
pnpm dev:app              # App 开发模式
pnpm dev:app-android      # 仅 Android
pnpm dev:app-ios          # 仅 iOS

# 生产构建
pnpm build:app            # 默认构建
pnpm build:app:prod       # 生产环境构建
pnpm build:app-android    # 仅 Android
pnpm build:app-ios        # 仅 iOS
```

---

## 相关链接

- [UniApp 官方文档 - App 打包](https://uniapp.dcloud.net.cn/tutorial/app-publish.html)
- [DCloud 开发者中心](https://dev.dcloud.net.cn/)
- [HBuilderX 下载](https://www.dcloud.io/hbuilderx.html)
- [Android 离线 SDK](https://nativesupport.dcloud.net.cn/AppDocs/download/android.html)
- [iOS 离线 SDK](https://nativesupport.dcloud.net.cn/AppDocs/download/ios.html)

---

*最后更新：2026-02-01*
