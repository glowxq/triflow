<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';
import type { Recordable } from '@vben/types';

import { computed, onMounted, ref } from 'vue';

import { AuthenticationCodeLogin, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { ElMessage } from 'element-plus';

import { getSwitchStatus, SwitchKey } from '#/api/base';
import { sendSmsCodeApi } from '#/api/core/auth';
import { useAuthStore } from '#/store';

defineOptions({ name: 'CodeLogin' });

const authStore = useAuthStore();
const CODE_LENGTH = 6;

/** 手机号登录开关 */
const phoneLoginEnabled = ref(true);
/** AuthenticationCodeLogin 组件引用 */
const codeLoginRef = ref<InstanceType<typeof AuthenticationCodeLogin>>();

onMounted(async () => {
  phoneLoginEnabled.value = await getSwitchStatus(
    SwitchKey.USER_LOGIN_PHONE,
  );
});

/**
 * 发送短信验证码
 */
async function handleSendCode() {
  const formApi = codeLoginRef.value?.getFormApi?.();
  const values = formApi ? await formApi.getValues() : {};
  const phone = values?.phone || '';
  if (!/^\d{11}$/.test(phone)) {
    ElMessage.warning('请先输入正确的手机号');
    throw new Error('手机号格式不正确');
  }
  if (!phoneLoginEnabled.value) {
    ElMessage.warning('手机号登录功能暂未开启');
    throw new Error('手机号登录未开启');
  }
  await sendSmsCodeApi(phone, 'login');
  ElMessage.success('验证码已发送');
}

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      component: 'VbenInput',
      componentProps: {
        placeholder: $t('authentication.mobile'),
      },
      fieldName: 'phone',
      label: $t('authentication.mobile'),
      rules: z
        .string()
        .min(1, { message: $t('authentication.mobileTip') })
        .refine((v) => /^\d{11}$/.test(v), {
          message: $t('authentication.mobileErrortip'),
        }),
    },
    {
      component: 'VbenPinInput',
      componentProps: {
        codeLength: CODE_LENGTH,
        createText: (countdown: number) => {
          return countdown > 0
            ? $t('authentication.sendText', [countdown])
            : $t('authentication.sendCode');
        },
        handleSendCode,
        placeholder: $t('authentication.code'),
      },
      fieldName: 'smsCode',
      label: $t('authentication.code'),
      rules: z.string().length(CODE_LENGTH, {
        message: $t('authentication.codeTip', [CODE_LENGTH]),
      }),
    },
  ];
});

/**
 * 处理手机号登录
 */
async function handleLogin(values: Recordable<any>) {
  if (!phoneLoginEnabled.value) {
    ElMessage.warning('手机号登录功能暂未开启');
    return;
  }
  await authStore.authLogin({
    grantType: 'sms',
    phone: values.phone,
    smsCode: values.smsCode,
  });
}
</script>

<template>
  <AuthenticationCodeLogin
    ref="codeLoginRef"
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="handleLogin"
  />
</template>
