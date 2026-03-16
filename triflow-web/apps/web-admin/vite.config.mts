import { defineConfig } from '@vben/vite-config';

import ElementPlus from 'unplugin-element-plus/vite';

export default defineConfig(async () => {
  return {
    application: {},
    vite: {
      plugins: [
        ElementPlus({
          format: 'esm',
        }),
      ],
      build: {
        rollupOptions: {
          output: {
            // 将 Element Plus 组件合并到一个 chunk，减少首次加载请求数
            manualChunks: {
              'element-plus': ['element-plus'],
            },
          },
        },
      },
      server: {
        proxy: {
          '/api': {
            changeOrigin: true,
            rewrite: (path) => path.replace(/^\/api/, ''),
            // 后端服务地址（API 路径已显式包含模块前缀如 /base）
            target: 'http://localhost:7100',
            ws: true,
          },
        },
      },
    },
  };
});
