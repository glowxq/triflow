<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';

import { computed, onMounted, ref } from 'vue';

import { ProfileBaseSetting } from '@vben/common-ui';

import { ElMessage } from 'element-plus';

import { getUserInfoApi, updateProfileApi } from '#/api';

const profileBaseSettingRef = ref();
const loading = ref(false);

// 性别选项
const GENDER_OPTIONS = [
  { label: '未知', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
];

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      fieldName: 'realName',
      component: 'Input',
      label: '真实姓名',
      componentProps: {
        placeholder: '请输入真实姓名',
      },
    },
    {
      fieldName: 'nickname',
      component: 'Input',
      label: '昵称',
      componentProps: {
        placeholder: '请输入昵称',
      },
    },
    {
      fieldName: 'gender',
      component: 'Select',
      componentProps: {
        options: GENDER_OPTIONS,
        placeholder: '请选择性别',
      },
      label: '性别',
    },
    {
      fieldName: 'phone',
      component: 'Input',
      label: '手机号',
      componentProps: {
        placeholder: '请输入手机号',
      },
    },
    {
      fieldName: 'email',
      component: 'Input',
      label: '邮箱',
      componentProps: {
        placeholder: '请输入邮箱',
      },
    },
    {
      fieldName: 'introduction',
      component: 'Textarea',
      label: '个人简介',
      componentProps: {
        placeholder: '请输入个人简介',
        rows: 4,
      },
    },
  ];
});

onMounted(async () => {
  const data = await getUserInfoApi();
  profileBaseSettingRef.value.getFormApi().setValues({
    realName: data.realName,
    nickname: data.username, // 用 username 作为默认昵称
    gender: 0,
    phone: '',
    email: '',
    introduction: data.desc || '',
  });
});

// 处理保存
async function handleSave(values: Record<string, any>) {
  try {
    loading.value = true;
    await updateProfileApi({
      realName: values.realName,
      nickname: values.nickname,
      gender: values.gender,
      phone: values.phone,
      email: values.email,
      introduction: values.introduction,
    });
    ElMessage.success('保存成功');
  } catch {
    ElMessage.error('保存失败');
  } finally {
    loading.value = false;
  }
}
</script>
<template>
  <ProfileBaseSetting
    ref="profileBaseSettingRef"
    :form-schema="formSchema"
    :loading="loading"
    @save="handleSave"
  />
</template>
