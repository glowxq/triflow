import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:message-circle',
      order: 25,
      title: '微信管理',
    },
    name: 'Wechat',
    path: '/wechat',
    children: [
      {
        name: 'WechatSubscribe',
        path: '/wechat/subscribe',
        component: () => import('#/views/wechat/subscribe/index.vue'),
        meta: {
          icon: 'lucide:bell',
          title: '订阅管理',
        },
      },
      {
        name: 'WechatTabbar',
        path: '/wechat/tabbar',
        component: () => import('#/views/wechat/tabbar/index.vue'),
        meta: {
          icon: 'lucide:layout-template',
          title: '底部菜单',
        },
      },
    ],
  },
];

export default routes;
