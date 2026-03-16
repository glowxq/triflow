/* eslint-disable import/no-mutable-exports */
// 获取屏幕边界到安全区域距离
let systemInfo: UniApp.GetSystemInfoResult | UniApp.GetWindowInfoResult
let safeAreaInsets: UniApp.SafeAreaInsets | null
let statusBarHeight: number

// #ifdef MP-WEIXIN
// 微信小程序使用新的 API（getSystemInfoSync 已废弃）
systemInfo = uni.getWindowInfo()
statusBarHeight = systemInfo.statusBarHeight || 0
safeAreaInsets = systemInfo.safeArea
  ? {
      top: systemInfo.safeArea.top,
      right: systemInfo.windowWidth - systemInfo.safeArea.right,
      bottom: systemInfo.windowHeight - systemInfo.safeArea.bottom,
      left: systemInfo.safeArea.left,
    }
  : null
// #endif

// #ifndef MP-WEIXIN
// 其他平台继续使用 uni API
systemInfo = uni.getSystemInfoSync()
statusBarHeight = systemInfo.statusBarHeight || 0
safeAreaInsets = systemInfo.safeAreaInsets || null
// #endif

export { safeAreaInsets, statusBarHeight, systemInfo }
