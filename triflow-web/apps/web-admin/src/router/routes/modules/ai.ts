import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:bot',
      order: 18,
      title: 'AI 管理',
    },
    name: 'AI',
    path: '/ai',
    children: [
      {
        name: 'AiConfig',
        path: '/ai/config',
        component: () => import('#/views/ai/config/index.vue'),
        meta: {
          icon: 'lucide:settings-2',
          title: 'AI 配置',
        },
      },
      {
        name: 'AiPrompt',
        path: '/ai/prompt',
        component: () => import('#/views/ai/prompt/index.vue'),
        meta: {
          icon: 'lucide:message-square-text',
          title: 'Prompt 模板',
        },
      },
      {
        name: 'AiCallLog',
        path: '/ai/call-log',
        component: () => import('#/views/ai/call-log/index.vue'),
        meta: {
          icon: 'lucide:scroll-text',
          title: '调用记录',
        },
      },
    ],
  },
];

export default routes;
