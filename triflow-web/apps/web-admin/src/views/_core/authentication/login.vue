<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';

import { computed, markRaw, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { AuthenticationLogin, SliderCaptcha, z } from '@vben/common-ui';
import { $t } from '@vben/locales';
import { preferences } from '@vben/preferences';
import { useAccessStore, useUserStore } from '@vben/stores';

import { ElMessage } from 'element-plus';

import { getSwitchStatus, SwitchKey } from '#/api/base';
import {
  feishuLoginApi,
  getAccessCodesApi,
  getFeishuAuthUrl,
} from '#/api/core/auth';
import { getUserInfoApi } from '#/api/core/user';
import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
const accessStore = useAccessStore();
const userStore = useUserStore();
const router = useRouter();
const route = useRoute();

/** 飞书登录加载状态 */
const feishuLoading = ref(false);

/** 验证码开关状态 */
const captchaEnabled = ref(false);
/** 第三方登录开关状态 */
const socialLoginEnabled = ref(false);

/** 获取登录相关开关状态 */
async function fetchLoginSwitches() {
  const [captcha, social] = await Promise.all([
    getSwitchStatus(SwitchKey.SECURITY_CAPTCHA),
    getSwitchStatus(SwitchKey.USER_LOGIN_SOCIAL),
  ]);
  captchaEnabled.value = captcha;
  socialLoginEnabled.value = social;
}

onMounted(() => {
  fetchLoginSwitches();
  // 检查是否是飞书登录回调
  handleFeishuCallback();
});

// ==================== 飞书登录 ====================

/**
 * 发起飞书登录
 */
async function handleFeishuLogin() {
  try {
    feishuLoading.value = true;
    // 生成随机 state 用于防 CSRF
    const state = Math.random().toString(36).slice(2);
    sessionStorage.setItem('feishu_oauth_state', state);

    // 回调地址：当前登录页
    const redirectUri = `${window.location.origin}${window.location.pathname}`;

    // 获取飞书授权 URL
    const authUrl = await getFeishuAuthUrl(redirectUri, state);

    // 跳转到飞书授权页
    window.location.href = authUrl;
  } catch (error: any) {
    ElMessage.error(error.message || '获取飞书授权地址失败');
    feishuLoading.value = false;
  }
}

/**
 * 处理飞书登录回调
 */
async function handleFeishuCallback() {
  const code = route.query.code as string;
  const state = route.query.state as string;

  if (!code) {
    return;
  }

  // 检查是否是绑定请求（state 以 bind_ 开头）
  // 如果是绑定请求，转发到 profile 页面处理
  if (state?.startsWith('bind_')) {
    router.replace({
      path: '/profile',
      query: { code, state },
    });
    return;
  }

  // 验证 state
  const savedState = sessionStorage.getItem('feishu_oauth_state');
  if (savedState && state !== savedState) {
    ElMessage.error('登录状态验证失败，请重试');
    // 清除 URL 参数
    router.replace({ path: route.path });
    return;
  }

  sessionStorage.removeItem('feishu_oauth_state');

  try {
    feishuLoading.value = true;

    // 调用飞书登录 API
    const { accessToken } = await feishuLoginApi(code);

    if (accessToken) {
      // 存储 accessToken
      accessStore.setAccessToken(accessToken);

      // 获取用户信息和权限码
      const [userInfo, accessCodes] = await Promise.all([
        getUserInfoApi(),
        getAccessCodesApi(),
      ]);

      userStore.setUserInfo(userInfo);
      accessStore.setAccessCodes(accessCodes);

      ElMessage.success('飞书登录成功');

      // 跳转到首页
      await router.push(userInfo.homePath || preferences.app.defaultHomePath);
    }
  } catch (error: any) {
    ElMessage.error(error.message || '飞书登录失败');
  } finally {
    feishuLoading.value = false;
    // 清除 URL 参数
    router.replace({ path: route.path });
  }
}

const formSchema = computed((): VbenFormSchema[] => {
  const baseSchema: VbenFormSchema[] = [
    {
      component: 'VbenInput',
      componentProps: {
        placeholder: $t('authentication.usernameTip'),
      },
      defaultValue: 'root',
      fieldName: 'username',
      label: $t('authentication.username'),
      rules: z.string().min(1, { message: $t('authentication.usernameTip') }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: $t('authentication.password'),
      },
      defaultValue: 'root',
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, { message: $t('authentication.passwordTip') }),
    },
  ];

  // 根据开关状态决定是否显示验证码
  if (captchaEnabled.value) {
    baseSchema.push({
      component: markRaw(SliderCaptcha),
      fieldName: 'captcha',
      rules: z.boolean().refine((value) => value, {
        message: $t('authentication.verifyRequiredTip'),
      }),
    });
  }

  return baseSchema;
});
</script>

<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading || feishuLoading"
    @submit="authStore.authLogin"
  >
    <!-- 自定义第三方登录 -->
    <template #third-party-login>
      <div v-if="socialLoginEnabled" class="w-full sm:mx-auto md:max-w-md">
        <div class="mt-4 flex items-center justify-between">
          <span
            class="w-[35%] border-b border-input dark:border-gray-600"
          ></span>
          <span class="text-center text-xs uppercase text-muted-foreground">
            {{ $t('authentication.thirdPartyLogin') }}
          </span>
          <span
            class="w-[35%] border-b border-input dark:border-gray-600"
          ></span>
        </div>

        <div class="mt-4 flex flex-wrap justify-center gap-3">
          <!-- 飞书登录按钮 -->
          <button
            class="flex h-10 w-10 items-center justify-center rounded-full border border-gray-200 transition-colors hover:bg-primary/10 dark:border-gray-700"
            :disabled="feishuLoading"
            title="飞书登录"
            @click="handleFeishuLogin"
          >
            <svg
              class="h-5 w-5"
              viewBox="0 0 24 24"
              fill="currentColor"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                d="M3.5 5.5L9.5 2L20.5 10L14.5 22L3.5 13.5L7 10L3.5 5.5Z"
                fill="#3370FF"
              />
              <path d="M7 10L3.5 13.5L14.5 22L11 14L7 10Z" fill="#00D6B9" />
            </svg>
          </button>
        </div>
      </div>
    </template>
  </AuthenticationLogin>
</template>
