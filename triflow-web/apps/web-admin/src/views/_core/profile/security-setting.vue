<script setup lang="ts">
import type { ProfileApi } from '#/api';

import { computed, onMounted, ref } from 'vue';

import { ProfileSecuritySetting } from '@vben/common-ui';

import {
  ElButton,
  ElCard,
  ElDialog,
  ElDivider,
  ElEmpty,
  ElMessage,
  ElMessageBox,
  ElSpace,
  ElTag,
} from 'element-plus';

import {
  bindFeishuApi,
  getFeishuAuthUrl,
  getUserSocialsApi,
  unbindSocialApi,
} from '#/api';

// 第三方平台配置
const socialPlatforms = [
  {
    type: 'feishu',
    name: '飞书',
    icon: 'i-simple-icons-bytedance',
    color: '#3370ff',
    available: true, // 是否可用
  },
  {
    type: 'wechat',
    name: '微信',
    icon: 'i-simple-icons-wechat',
    color: '#07c160',
    available: false, // 暂未实现
  },
  {
    type: 'qq',
    name: 'QQ',
    icon: 'i-simple-icons-tencentqq',
    color: '#12b7f5',
    available: false,
  },
  {
    type: 'github',
    name: 'GitHub',
    icon: 'i-simple-icons-github',
    color: '#333',
    available: false,
  },
];

// 已绑定的第三方账号
const boundSocials = ref<ProfileApi.UserSocialVO[]>([]);
const loading = ref(false);

// 绑定弹窗
const bindDialogVisible = ref(false);
const bindLoading = ref(false);

// 基础安全设置
const formSchema = computed(() => {
  return [
    {
      value: true,
      fieldName: 'accountPassword',
      label: '账户密码',
      description: '可在"修改密码"标签页中修改密码',
    },
  ];
});

// 加载已绑定的第三方账号
async function loadBoundSocials() {
  try {
    loading.value = true;
    boundSocials.value = await getUserSocialsApi();
  } catch {
    ElMessage.error('加载第三方绑定信息失败');
  } finally {
    loading.value = false;
  }
}

// 检查平台是否已绑定
function isBound(socialType: string): boolean {
  return boundSocials.value.some((s) => s.socialType === socialType);
}

// 获取已绑定的账号信息
function getBoundInfo(socialType: string): ProfileApi.UserSocialVO | undefined {
  return boundSocials.value.find((s) => s.socialType === socialType);
}

// 处理绑定
async function handleBind(platform: (typeof socialPlatforms)[0]) {
  if (!platform.available) {
    ElMessage.warning(`${platform.name}绑定功能暂未开放`);
    return;
  }

  if (platform.type === 'feishu') {
    await bindFeishu();
  }
}

// 飞书绑定
async function bindFeishu() {
  try {
    // 生成 state 用于防 CSRF（以 bind_ 开头标识为绑定请求）
    const state = `bind_${Date.now()}`;
    // 设置回调地址 - 使用登录页面作为回调（飞书后台已配置）
    // 登录页面检测到 bind_ 开头的 state 会自动转发到 profile 页面
    const redirectUri = `${window.location.origin}/auth/login`;

    // 存储 state 到 sessionStorage
    sessionStorage.setItem('feishu_bind_state', state);

    // 获取飞书授权 URL
    const authUrl = await getFeishuAuthUrl(redirectUri, state);

    // 跳转到飞书授权页面
    window.location.href = authUrl;
  } catch {
    ElMessage.error('获取飞书授权地址失败');
  }
}

// 处理解绑
async function handleUnbind(platform: (typeof socialPlatforms)[0]) {
  const boundInfo = getBoundInfo(platform.type);
  if (!boundInfo) return;

  try {
    await ElMessageBox.confirm(
      `确定要解绑${platform.name}账号"${boundInfo.nickname || platform.name}"吗？`,
      '解绑确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    );

    await unbindSocialApi(platform.type);
    ElMessage.success('解绑成功');
    await loadBoundSocials();
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('解绑失败');
    }
  }
}

// 处理飞书绑定回调（从 URL 参数中获取 code）
async function handleFeishuBindCallback() {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');
  const state = urlParams.get('state');

  if (!code) return;

  // 验证 state
  const savedState = sessionStorage.getItem('feishu_bind_state');
  if (state !== savedState) {
    ElMessage.error('绑定请求无效，请重试');
    // 清理 URL 参数
    window.history.replaceState({}, '', window.location.pathname);
    return;
  }

  try {
    bindLoading.value = true;
    await bindFeishuApi({ code, force: false });
    ElMessage.success('飞书账号绑定成功');
    await loadBoundSocials();
  } catch (error: any) {
    // 处理已被其他账号绑定的情况
    if (error?.message?.includes('已被其他用户绑定')) {
      try {
        await ElMessageBox.confirm(
          '该飞书账号已被其他用户绑定，是否强制绑定到当前账号？（原账号将解除绑定）',
          '绑定冲突',
          {
            confirmButtonText: '强制绑定',
            cancelButtonText: '取消',
            type: 'warning',
          },
        );
        // 强制绑定
        await bindFeishuApi({ code, force: true });
        ElMessage.success('飞书账号绑定成功');
        await loadBoundSocials();
      } catch (error_: any) {
        if (error_ !== 'cancel') {
          ElMessage.error('绑定失败');
        }
      }
    } else {
      ElMessage.error(error?.message || '绑定失败');
    }
  } finally {
    bindLoading.value = false;
    // 清理
    sessionStorage.removeItem('feishu_bind_state');
    window.history.replaceState({}, '', window.location.pathname);
  }
}

// 格式化绑定时间
function formatBindTime(time?: string): string {
  if (!time) return '';
  return new Date(time).toLocaleString();
}

onMounted(async () => {
  await loadBoundSocials();
  // 检查是否是绑定回调
  handleFeishuBindCallback();
});
</script>

<template>
  <div class="security-setting">
    <!-- 基础安全设置 -->
    <ProfileSecuritySetting :form-schema="formSchema" />

    <ElDivider />

    <!-- 第三方账号绑定 -->
    <ElCard shadow="never">
      <template #header>
        <div class="flex items-center justify-between">
          <span class="text-base font-medium">第三方账号绑定</span>
          <ElButton
            type="primary"
            size="small"
            @click="bindDialogVisible = true"
          >
            <span class="i-lucide-plus mr-1"></span>
            绑定账号
          </ElButton>
        </div>
      </template>

      <div v-if="boundSocials.length === 0" class="py-8">
        <ElEmpty description="暂无绑定的第三方账号" />
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="social in boundSocials"
          :key="social.id"
          class="flex items-center justify-between rounded-lg border border-gray-200 p-4"
        >
          <div class="flex items-center gap-3">
            <span
              :class="
                socialPlatforms.find((p) => p.type === social.socialType)
                  ?.icon || 'i-lucide-link'
              "
              :style="{
                color:
                  socialPlatforms.find((p) => p.type === social.socialType)
                    ?.color || '#666',
              }"
              class="text-2xl"
            ></span>
            <div>
              <div class="font-medium">
                {{
                  socialPlatforms.find((p) => p.type === social.socialType)
                    ?.name || social.socialType
                }}
              </div>
              <div class="text-sm text-gray-500">
                {{ social.nickname || '已绑定' }}
                <span v-if="social.bindTime" class="ml-2">
                  · {{ formatBindTime(social.bindTime) }}
                </span>
              </div>
            </div>
          </div>
          <ElButton
            type="danger"
            link
            @click="
              handleUnbind(
                socialPlatforms.find((p) => p.type === social.socialType) || {
                  type: social.socialType,
                  name: social.socialType,
                  icon: '',
                  color: '',
                  available: true,
                },
              )
            "
          >
            解绑
          </ElButton>
        </div>
      </div>
    </ElCard>

    <!-- 绑定账号弹窗 -->
    <ElDialog v-model="bindDialogVisible" title="绑定第三方账号" width="400px">
      <div class="space-y-4">
        <div
          v-for="platform in socialPlatforms"
          :key="platform.type"
          class="flex items-center justify-between rounded-lg border border-gray-200 p-4 transition-colors hover:bg-gray-50"
          :class="{ 'cursor-not-allowed opacity-50': !platform.available }"
        >
          <div class="flex items-center gap-3">
            <span
              :class="platform.icon"
              :style="{ color: platform.color }"
              class="text-2xl"
            ></span>
            <span class="font-medium">{{ platform.name }}</span>
          </div>
          <ElSpace>
            <ElTag v-if="isBound(platform.type)" type="success" size="small">
              已绑定
            </ElTag>
            <ElTag
              v-else-if="!platform.available"
              type="info"
              size="small"
              effect="plain"
            >
              暂未开放
            </ElTag>
            <ElButton
              v-else
              type="primary"
              size="small"
              :loading="bindLoading"
              @click="handleBind(platform)"
            >
              绑定
            </ElButton>
          </ElSpace>
        </div>
      </div>
    </ElDialog>
  </div>
</template>

<style scoped>
.security-setting {
  max-width: 800px;
}
</style>
