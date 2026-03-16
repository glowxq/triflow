import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:newspaper',
      order: 30,
      title: '内容管理',
    },
    name: 'Cms',
    path: '/cms',
    children: [
      {
        name: 'CmsTextCategory',
        path: '/cms/text-category',
        component: () => import('#/views/cms/text-category/index.vue'),
        meta: {
          icon: 'lucide:folder-tree',
          title: '文本分类',
        },
      },
      {
        name: 'CmsText',
        path: '/cms/text',
        component: () => import('#/views/cms/text/index.vue'),
        meta: {
          icon: 'lucide:file-text',
          title: '文本管理',
        },
      },
    ],
  },
];

export default routes;
