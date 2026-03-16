import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:folder',
      order: 20,
      title: '文件管理',
    },
    name: 'File',
    path: '/file',
    children: [
      {
        name: 'FileConfig',
        path: '/file/config',
        component: () => import('#/views/file/config/index.vue'),
        meta: {
          icon: 'lucide:hard-drive',
          title: '存储配置',
        },
      },
      {
        name: 'FileInfo',
        path: '/file/info',
        component: () => import('#/views/file/info/index.vue'),
        meta: {
          icon: 'lucide:files',
          title: '文件列表',
        },
      },
    ],
  },
];

export default routes;
