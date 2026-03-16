import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:wallet',
      order: 15,
      title: '钱包管理',
    },
    name: 'Wallet',
    path: '/wallet',
    children: [
      {
        name: 'WalletTransaction',
        path: '/wallet/transaction',
        component: () => import('#/views/wallet/transaction/index.vue'),
        meta: {
          icon: 'lucide:receipt',
          title: '账户变动',
        },
      },
      {
        name: 'WalletRechargeConfig',
        path: '/wallet/recharge-config',
        component: () => import('#/views/wallet/recharge-config/index.vue'),
        meta: {
          icon: 'lucide:badge-dollar-sign',
          title: '充值配置',
        },
      },
      {
        name: 'WalletSignIn',
        path: '/wallet/signin',
        component: () => import('#/views/wallet/signin/index.vue'),
        meta: {
          icon: 'lucide:calendar-check',
          title: '签到记录',
        },
      },
      {
        name: 'WalletRechargeOrder',
        path: '/wallet/recharge-order',
        component: () => import('#/views/wallet/recharge-order/index.vue'),
        meta: {
          icon: 'lucide:wallet-cards',
          title: '充值订单',
        },
      },
    ],
  },
];

export default routes;
